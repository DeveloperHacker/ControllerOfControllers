package com.bugInc.app

import javax.swing.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class MessageBox internal constructor(title: String, message: String) : JFrame(title) {

    init {
        contentPane = JPanel()
        contentPane.layout = BoxLayout(contentPane, BoxLayout.PAGE_AXIS)
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        isVisible = true

        val lines = 3
        contentPane.spacer(LineLength, SpacerHeight)
        val firstLine = JPanel()
        firstLine.layout = BoxLayout(firstLine, BoxLayout.LINE_AXIS)
        firstLine.spacer(SpacerLength, lines * LineHeight)
        firstLine.textPane(LineLength - 3 * SpacerLength, lines * LineHeight, message).isEditable = false
        firstLine.spacer(SpacerLength, lines * LineHeight)
        contentPane.add(firstLine)

        contentPane.spacer(LineLength, SpacerHeight)
        val secondLine = JPanel()
        secondLine.layout = BoxLayout(secondLine, BoxLayout.LINE_AXIS)
        firstLine.spacer((LineLength - ButtonLength) / 2, LineHeight)
        secondLine.button(ButtonLength, LineHeight, "OK").addActionListener {
            this@MessageBox.dispose()
        }
        firstLine.spacer((LineLength - ButtonLength) / 2, LineHeight)
        contentPane.add(secondLine)

        setBounds(200, 200, LineLength, (lines + 2) * LineHeight + 4 * SpacerHeight)
        isResizable = false
    }
}
