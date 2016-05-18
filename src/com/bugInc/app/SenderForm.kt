package com.bugInc.app

import com.bugInc.core.Connector
import com.bugInc.math.Figure
import com.bugInc.math.Vector
import com.fazecast.jSerialComm.SerialPort
import java.awt.Container
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import javax.swing.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class SenderForm(title: String, init: () -> Map<String, String>) : JFrame(title) {

    private val map = init()
    private var chat: ChatForm? = null

    init {
        var lines = 1

        contentPane = JPanel()
        contentPane.layout = BoxLayout(contentPane, BoxLayout.PAGE_AXIS)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true

        contentPane.spacer(LineLength, SpacerHeight)
        val firstLine = JPanel()
        firstLine.layout = BoxLayout(firstLine, BoxLayout.LINE_AXIS)
        firstLine.spacer(SpacerLength, LineHeight)
        val portBox = firstLine.comboBox<String>()
        firstLine.spacer(SpacerLength, LineHeight)
        val baudRateBox = firstLine.comboBox(4, 300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 74880, 115200, 230400, 250000)
        firstLine.spacer(SpacerLength, LineHeight)
        val parityBox = firstLine.comboBox(2, "none", "odd", "even", "mark", "space")
        firstLine.spacer(SpacerLength, LineHeight)
        val stopBitsBox = firstLine.comboBox(1, 1, 2)
        firstLine.spacer(SpacerLength, LineHeight)
        contentPane.add(firstLine)
        ++lines

        contentPane.spacer(LineLength, SpacerHeight)
        val secondLine = JPanel()
        secondLine.layout = BoxLayout(secondLine, BoxLayout.LINE_AXIS)
        secondLine.spacer(LineLength - 3 * (SpacerLength + ButtonLength) - SpacerLength, LineHeight)
        val updateButton = secondLine.button(ButtonLength, LineHeight, "update")
        secondLine.spacer(SpacerLength, LineHeight)
        val connectButton = secondLine.button(ButtonLength, LineHeight, "connect")
        secondLine.spacer(SpacerLength, LineHeight)
        val chatButton = secondLine.button(ButtonLength, LineHeight, "chat")
        secondLine.spacer(SpacerLength, LineHeight)
        contentPane.add(secondLine)
        ++lines

        val pair = load(contentPane)
        val fields = pair.first
        val buttons = pair.second
        lines += map.size

        contentPane.spacer(LineLength, SpacerHeight)

        setBounds(30, 30, LineLength, (LineHeight + SpacerHeight) * lines + SpacerHeight)
        isResizable = false

        var ports = SerialPort.getCommPorts()
        for (port in ports) portBox.addItem(port.systemPortName)
        updateButton.addActionListener {
            ports = SerialPort.getCommPorts()
            portBox.removeAllItems()
            for (port in ports) portBox.addItem(port.systemPortName)
        }

        val connector = Connector({
            MessageBox("Input", "ID: ${it.ID.toChar()}\nCOMMAND: ${it.COMMAND.toChar()}\nDATA: ${it.DATA.toChar()}")
        }, { b ->
            chat?.add(Character.toString(b.toByte().toChar()))
            true
        })
        var openPort: SerialPort? = null
        connectButton.addActionListener {
            if (ports.size != 0) {
                if (openPort == null || !openPort!!.isOpen) {
                    openPort = SerialPort.getCommPort(portBox.selectedItem as String)
                    openPort!!.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0)
                    openPort!!.baudRate = baudRateBox.selectedItem as Int
                    openPort!!.numStopBits = stopBitsBox.selectedItem as Int
                    openPort!!.parity = (parityBox.selectedItem as String).parity()
                    if (openPort!!.openPort()) {
                        connectButton.text = "Disconnect"
                        portBox.isEnabled = false
                        baudRateBox.isEnabled = false
                        updateButton.isEnabled = false
                        parityBox.isEnabled = false
                        stopBitsBox.isEnabled = false
                        buttons.forEach { button -> button.isEnabled = true }
                        chatButton.isEnabled = true
                        connector.run(openPort!!)
                    }
                } else {
                    connectButton.text = "Connect"
                    portBox.isEnabled = true
                    baudRateBox.isEnabled = true
                    updateButton.isEnabled = true
                    parityBox.isEnabled = true
                    stopBitsBox.isEnabled = true
                    buttons.forEach { button -> button.isEnabled = false }
                    chatButton.isEnabled = false
                    chat?.dispose()
                    openPort!!.closePort()
                    connector.stop()
                }
            }
        }

        connector.addController(0.toByte(), "test", 0.toByte(), Figure(Vector(0.0, 0.0), 10.0, 10.0, 0.01))
        val iBut = buttons.iterator()
        val iFie = fields.iterator()
        while (iBut.hasNext() && iFie.hasNext()) {
            val button = iBut.next()
            val field = iFie.next()
            button.isEnabled = false
            button.addActionListener {
                try {
                    connector.send(field.text)
                } catch (error: Exception) {
                    MessageBox("Error", error.toString())
                }
            }
        }

        chatButton.isEnabled = false
        chatButton.addActionListener {
            chat?.dispose()
            SwingUtilities.invokeLater { chat = ChatForm("Chat", connector) }
        }
    }

    private fun load(panel: Container): Pair<MutableList<JTextField>, MutableList<JButton>> {
        val fields = LinkedList<JTextField>()
        val buttons = LinkedList<JButton>()
        map.forEach { entry ->
            panel.spacer(LineLength, SpacerHeight)
            val pair = line(panel, entry.key, entry.value, "send")
            fields.add(pair.first)
            buttons.add(pair.second)
        }
        return Pair(fields, buttons)
    }

    private fun line(pane: Container, labelName: String, text: String, buttonName: String): Pair<JTextField, JButton> {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.LINE_AXIS)
        panel.spacer(SpacerLength, LineHeight)
        panel.label(LabelLength, LineHeight, labelName)
        val field = panel.textField(TextLength, LineHeight, text)
        panel.spacer(SpacerLength, LineHeight)
        val button = panel.button(ButtonLength, LineHeight, buttonName)
        panel.spacer(SpacerLength, LineHeight)
        pane.add(panel)
        return Pair(field, button)
    }
}

fun String.parity() = when (this) {
    "odd" -> SerialPort.ODD_PARITY
    "even" -> SerialPort.EVEN_PARITY
    "mark" -> SerialPort.MARK_PARITY
    "space" -> SerialPort.SPACE_PARITY
    else -> SerialPort.NO_PARITY
}

fun main(args: Array<String>) = SwingUtilities.invokeLater {
    SenderForm("Sender") {
        val map = HashMap<String, String>()
        val input = "bin/initLines.txt".input()
        while (input.hasNextLine()) {
            val line = input.nextLine()
            if (line == "") continue
            val it = Scanner(line)
            val key = it.next()
            val value = it.next()
            map.put(key, value)
        }
        map
    }
}

fun String.input(): Scanner {
    val file = File(this)
    if (!file.isFile) throw FileNotFoundException(file.absolutePath)
    return Scanner(file, "windows-1251")
}