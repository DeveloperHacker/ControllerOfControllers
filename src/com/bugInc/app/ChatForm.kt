package com.bugInc.app

import com.bugInc.core.Connector
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class ChatForm(title: String, connector: Connector) : JFrame(title) {

    val logPane: JTextPane
    init {
        contentPane = JPanel()
        contentPane.layout = BoxLayout(contentPane, BoxLayout.PAGE_AXIS)
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        isVisible = true

        contentPane.spacer(LineLength, SpacerHeight)
        val firstLine = JPanel()
        firstLine.layout = BoxLayout(firstLine, BoxLayout.LINE_AXIS)
        firstLine.spacer(SpacerLength, LineHeight)
        val messageField = firstLine.textField(LineLength - 3 * SpacerLength - ButtonLength, LineHeight, "")
        firstLine.spacer(SpacerLength, LineHeight)
        val sendButton = firstLine.button(ButtonLength, LineHeight, "send")
        firstLine.spacer(SpacerLength, LineHeight)
        contentPane.add(firstLine)

        val lines = 10
        contentPane.spacer(LineLength, SpacerHeight)
        val secondLine = JPanel()
        secondLine.layout = BoxLayout(secondLine, BoxLayout.LINE_AXIS)
        secondLine.spacer(SpacerLength, lines * LineHeight)
        logPane = textPane("")
        logPane.isEditable = false
        secondLine.scrollPane(LineLength - 2 * SpacerLength, lines * LineHeight, logPane)
        secondLine.spacer(SpacerLength, lines * LineHeight)
        contentPane.add(secondLine)

        contentPane.spacer(LineLength, SpacerHeight)

        setBounds(100, 100, LineLength + SpacerLength, (lines + 2) * LineHeight + 4 * SpacerHeight)
        isResizable = false

        sendButton.addActionListener {
            try {
                val message = messageField.text
                logPane.text = "${logPane.text}> $message\n"
                for (byte in message.toByteArray()) connector.send(byte)
            } catch (error: Exception) {
                MessageBox("Error", error.toString())
            }
        }

        messageField.addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent) = Unit
            override fun keyReleased(e: KeyEvent) = Unit
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    sendButton.doClick()
                    messageField.text = ""
                }
            }
        })
    }

    fun add(message: String) {
        logPane.text = "${logPane.text}< $message\n"
    }
}
