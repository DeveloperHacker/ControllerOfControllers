package com.bugInc.app

import com.bugInc.core.Connector
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class ChatForm(title: String, private val connector: Connector) : JFrame(title) {

    private lateinit var messageField: JTextField
    private lateinit var sendButton: JButton
    private lateinit var logPane: JTextPane

    init {
        contentPane = JPanel()
        contentPane.layout = BoxLayout(contentPane, BoxLayout.PAGE_AXIS)
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        isVisible = true

        spacer(LineLength, SpacerHeight)
        row {
            spacer(SpacerLength, LineHeight)
            messageField = textField(LineLength - 3 * SpacerLength - ButtonLength, LineHeight, "")
            spacer(SpacerLength, LineHeight)
            sendButton = button(ButtonLength, LineHeight, "send")
            spacer(SpacerLength, LineHeight)
        }
        spacer(LineLength, SpacerHeight)
        row {
            spacer(SpacerLength, LogHeight)
            logPane = textPane("")
            scrollPane(LineLength - 2 * SpacerLength, LogHeight, logPane)
            spacer(SpacerLength, LogHeight)
        }
        spacer(LineLength, SpacerHeight)

        setBounds(500, 100, LineLength + SpacerLength, 2 * LineHeight + LogHeight + 4 * SpacerHeight)
        isResizable = false

        logPane.isEditable = false

        initSendButton()
        initMessageField()
    }

    private fun initSendButton() {
        sendButton.addActionListener {
            val message = messageField.text
            inp(message)
            for (char in message.toCharArray()) connector.send(char)
        }
    }

    private fun initMessageField() {
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

    fun inp(message: String) {
        logPane.text = "${logPane.text}> $message\n"
    }

    fun out(message: String) {
        logPane.text = "${logPane.text}< $message\n"
    }
}
