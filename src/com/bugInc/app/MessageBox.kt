package com.bugInc.app

import javax.swing.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class MessageBox(title: String, message: String) : JFrame(title) {

    private lateinit var OKButton: JButton
    private lateinit var messagePane: JTextPane

    init {
        contentPane = JPanel()
        contentPane.layout = BoxLayout(contentPane, BoxLayout.PAGE_AXIS)
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        isVisible = true

        spacer(LineLength, SpacerHeight)
        row {
            spacer(SpacerLength, MessageHeight)
            messagePane = textPane(LineLength - 3 * SpacerLength, MessageHeight, message)
            spacer(SpacerLength, MessageHeight)
        }

        spacer(LineLength, SpacerHeight)
        row {
            spacer((LineLength - ButtonLength) / 2, LineHeight)
            OKButton = button(ButtonLength, LineHeight, "OK")
            spacer((LineLength - ButtonLength) / 2, LineHeight)
        }

        setBounds(200, 200, LineLength, MessageHeight + 2 * LineHeight + 4 * SpacerHeight)
        isResizable = false

        messagePane.isEditable = false

        OKButton.addActionListener {
            this@MessageBox.dispose()
        }
    }
}
