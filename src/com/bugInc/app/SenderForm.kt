package com.bugInc.app

import com.bugInc.core.Connector
import com.bugInc.core.loadCommands
import com.bugInc.core.loadControllers
import com.fazecast.jSerialComm.SerialPort
import java.awt.Container
import java.util.*
import javax.swing.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class SenderForm(title: String) : JFrame(title) {

    private lateinit var portBox: JComboBox<String>
    private lateinit var baudRateBox: JComboBox<Int>
    private lateinit var parityBox: JComboBox<String>
    private lateinit var stopBitsBox: JComboBox<Int>
    private lateinit var updateButton: JButton
    private lateinit var connectButton: JButton
    private lateinit var chatButton: JButton
    private lateinit var runButton: JButton
    private lateinit var settingButton: JButton
    private lateinit var sendButtons: List<JButton>
    private lateinit var commandFields: List<JTextField>

    private lateinit var ports: Array<SerialPort>
    private lateinit var connector: Connector

    private val map = loadCommands()
    private var chat: ChatForm? = null
    private var setting: SettingForm? = null

    init {
        var lines = 1

        contentPane = JPanel()
        contentPane.layout = BoxLayout(contentPane, BoxLayout.PAGE_AXIS)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true

        contentPane.spacer(LineLength, SpacerHeight)
        contentPane.line {
            spacer(SpacerLength, LineHeight)
            portBox = comboBox<String>()
            spacer(SpacerLength, LineHeight)
            baudRateBox = comboBox(4, 300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 74880, 115200, 230400, 250000)
            spacer(SpacerLength, LineHeight)
            parityBox = comboBox(2, "none", "odd", "even", "mark", "space")
            spacer(SpacerLength, LineHeight)
            stopBitsBox = comboBox(1, 1, 2)
            spacer(SpacerLength, LineHeight)
        }
        ++lines

        contentPane.spacer(LineLength, SpacerHeight)
        contentPane.line {
            spacer(LineLength - 3 * (SpacerLength + ButtonLength) - SpacerLength, LineHeight)
            updateButton = button(ButtonLength, LineHeight, "update")
            spacer(SpacerLength, LineHeight)
            connectButton = button(ButtonLength, LineHeight, "connect")
            spacer(SpacerLength, LineHeight)
            settingButton = button(ButtonLength, LineHeight, "setting")
            spacer(SpacerLength, LineHeight)
        }
        ++lines

        contentPane.spacer(LineLength, SpacerHeight)
        contentPane.line {
            spacer(LineLength - 2 * (SpacerLength + ButtonLength) - SpacerLength, LineHeight)
            runButton = button(ButtonLength, LineHeight, "run")
            spacer(SpacerLength, LineHeight)
            chatButton = button(ButtonLength, LineHeight, "chat")
            spacer(SpacerLength, LineHeight)
        }
        ++lines

        val pair = contentPane.load()
        commandFields = pair.first
        sendButtons = pair.second
        lines += map.size

        contentPane.spacer(LineLength, SpacerHeight)

        setBounds(30, 30, LineLength, (LineHeight + SpacerHeight) * lines + SpacerHeight)
        isResizable = false

        initPortBox()
        initUpdateButton()
        initConnector()
        initConnectButton()
        initSendButtons()
        initChatButton()
        initRunButton()
        initSettingButton()
    }

    private fun initPortBox() {
        ports = SerialPort.getCommPorts()
        for (port in ports) portBox.addItem(port.systemPortName)
    }

    private fun initUpdateButton() {
        updateButton.addActionListener {
            ports = SerialPort.getCommPorts()
            portBox.removeAllItems()
            for (port in ports) portBox.addItem(port.systemPortName)
        }
    }

    private fun initConnector() {
        connector = Connector({
            chat?.out("ID: ${it.id.toChar()}\n   COMMAND: ${it.command.toChar()}\n   DATA: ${it.data.toChar()}")
        }, {
            chat?.out(Character.toString(it.toChar().toChar()))
        })
        connector.loadControllers()
    }

    private fun initConnectButton() {
        var openPort: SerialPort? = null
        connectButton.addActionListener {
            if (ports.size != 0) {
                if (openPort == null || !openPort!!.isOpen) {
                    openPort = SerialPort.getCommPort(portBox.selectedItem as String)
                    openPort!!.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0)
                    openPort!!.baudRate = baudRateBox.selectedItem as Int
                    openPort!!.numStopBits = stopBitsBox.selectedItem as Int
                    openPort!!.parity = when (parityBox.selectedItem as String) {
                        "odd" -> SerialPort.ODD_PARITY
                        "even" -> SerialPort.EVEN_PARITY
                        "mark" -> SerialPort.MARK_PARITY
                        "space" -> SerialPort.SPACE_PARITY
                        else -> SerialPort.NO_PARITY
                    }
                    if (openPort!!.openPort()) {
                        connectButton.text = "disconnect"
                        portBox.isEnabled = false
                        baudRateBox.isEnabled = false
                        updateButton.isEnabled = false
                        parityBox.isEnabled = false
                        stopBitsBox.isEnabled = false
                        sendButtons.forEach { button -> button.isEnabled = true }
                        chatButton.isEnabled = true
                        runButton.isEnabled = true
                        connector.run(openPort!!)
                    }
                } else {
                    connectButton.text = "connect"
                    portBox.isEnabled = true
                    baudRateBox.isEnabled = true
                    updateButton.isEnabled = true
                    parityBox.isEnabled = true
                    stopBitsBox.isEnabled = true
                    sendButtons.forEach { button -> button.isEnabled = false }
                    chatButton.isEnabled = false
                    runButton.isEnabled = false
                    chat?.dispose()
                    setting?.dispose()
                    openPort!!.closePort()
                    connector.stop()
                }
            }
        }
    }

    private fun initSendButtons() {
        val iBut = sendButtons.iterator()
        val iFie = commandFields.iterator()
        while (iBut.hasNext() && iFie.hasNext()) {
            val button = iBut.next()
            val field = iFie.next()
            button.isEnabled = false
            button.addActionListener {
                val message = field.text
                connector.send(message)
                chat?.inp(message)
            }
        }
    }

    private fun initChatButton() {
        chatButton.isEnabled = false
        chatButton.addActionListener {
            chat?.dispose()
            SwingUtilities.invokeLater { chat = ChatForm("Chat", connector) }
        }
    }

    private fun initRunButton() {
        runButton.isEnabled = false
        runButton.addActionListener {

        }
    }

    private fun initSettingButton() {
        settingButton.addActionListener {
            setting?.dispose()
            SwingUtilities.invokeLater { setting = SettingForm("Settings", connector) }
        }
    }

    private fun Container.load(): Pair<MutableList<JTextField>, MutableList<JButton>> {
        val fields = LinkedList<JTextField>()
        val buttons = LinkedList<JButton>()
        map.forEach { entry ->
            spacer(LineLength, SpacerHeight)
            val pair = line(entry.key, entry.value, "send")
            fields.add(pair.first)
            buttons.add(pair.second)
        }
        return Pair(fields, buttons)
    }

    private fun Container.line(labelName: String, text: String, buttonName: String): Pair<JTextField, JButton> {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.LINE_AXIS)
        panel.spacer(SpacerLength, LineHeight)
        panel.label(LabelLength, LineHeight, labelName)
        val field = panel.textField(TextLength, LineHeight, text)
        panel.spacer(SpacerLength, LineHeight)
        val button = panel.button(ButtonLength, LineHeight, buttonName)
        panel.spacer(SpacerLength, LineHeight)
        this.add(panel)
        return Pair(field, button)
    }
}

fun main(args: Array<String>) = SwingUtilities.invokeLater {
    SenderForm("Sender")
}