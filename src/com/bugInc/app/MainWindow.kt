package com.bugInc.app

import com.bugInc.core.*
import com.fazecast.jSerialComm.SerialPort
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JPanel

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class MainWindow {

    init {
        val window = JFrame()
        window.title = "MainWindow"
        window.setSize(400, 75)
        window.layout = BorderLayout()
        window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        val portList = JComboBox<String>()
        val baudRateList = JComboBox<Int>()
        val runButton = JButton("Run")
        val sendButton = JButton("Send")
        val updateButton = JButton("Update")
        val topPanel = JPanel()
        topPanel.add(updateButton)
        topPanel.add(portList)
        topPanel.add(baudRateList)
        topPanel.add(runButton)
        topPanel.add(sendButton)
        window.add(topPanel, BorderLayout.NORTH)

        baudRateList.addItem(300)
        baudRateList.addItem(1200)
        baudRateList.addItem(2400)
        baudRateList.addItem(4800)
        baudRateList.addItem(9600)
        baudRateList.addItem(19200)
        baudRateList.addItem(38400)
        baudRateList.addItem(57600)
        baudRateList.addItem(74880)
        baudRateList.addItem(115200)
        baudRateList.addItem(230400)
        baudRateList.addItem(250000)
        baudRateList.selectedIndex = 4

        var ports: Array<SerialPort> = SerialPort.getCommPorts()
        for (i in ports.indices) portList.addItem(ports[i].systemPortName)
        updateButton.addActionListener {
            ports = SerialPort.getCommPorts()
            portList.removeAllItems()
            for (i in ports.indices) portList.addItem(ports[i].systemPortName)
        }

        val connector = Connector()
        var openPort: SerialPort? = null
        runButton.addActionListener {
            if (!ports.isEmpty()) {
                if (openPort?.isOpen != true) {
                    openPort = SerialPort.getCommPort(portList.selectedItem.toString())
                    openPort!!.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0)
                    openPort!!.baudRate = baudRateList.selectedItem as Int
                    if (openPort!!.openPort()) {
                        runButton.text = "Stop"
                        portList.isEnabled = false
                        baudRateList.isEnabled = false
                        updateButton.isEnabled = false
                        sendButton.isEnabled = true
                        connector.run(openPort!!)
                    }
                } else {
                    openPort!!.closePort()
                    portList.isEnabled = true
                    baudRateList.isEnabled = true
                    updateButton.isEnabled = true
                    sendButton.isEnabled = false
                    runButton.text = "Run"
                    connector.stop()
                }
            }
        }

        connector.addController(0, "test", UNABLE, Geometry(Vector(0.0, 0.0), 10.0, 10.0))
        sendButton.isEnabled = false
        sendButton.addActionListener {
            connector.send(Letter(0, SYNC, -127, 127))
        }

        window.isVisible = true
    }
}

fun main(args: Array<String>) {
    MainWindow()
}