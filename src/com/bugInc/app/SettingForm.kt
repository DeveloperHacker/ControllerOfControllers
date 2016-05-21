package com.bugInc.app

import com.bugInc.containers.ImmutableList
import com.bugInc.core.*
import java.util.*
import javax.swing.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class SettingForm(title: String, private val connector: Connector) : JFrame(title) {

    private lateinit var controllerBox: JComboBox<String>
    private lateinit var deleteButton: JButton
    private lateinit var saveButton: JButton
    private lateinit var codePane: JTextPane

    init {
        contentPane = JPanel()
        contentPane.layout = BoxLayout(contentPane, BoxLayout.PAGE_AXIS)
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        isVisible = true

        spacer(LineLength, SpacerHeight)
        row {
            spacer(SpacerLength, LineHeight)
            controllerBox = comboBox<String>()
            spacer(SpacerLength, LineHeight)
            deleteButton = button(ButtonLength, LineHeight, "delete")
            spacer(SpacerLength, LineHeight)
            saveButton = button(ButtonLength, LineHeight, "save")
            spacer(SpacerLength, LineHeight)
        }
        spacer(LineLength, SpacerHeight)
        row {
            codePane = textPane("")
            scrollPane(LineLength - 2 * SpacerLength, CodeHeight, codePane)
        }
        spacer(LineLength, SpacerHeight)

        setBounds(50, 50, LineLength, CodeHeight + 2 * LineHeight + 3 * SpacerHeight)
        isResizable = false

        initControllerBox()
        initDeleteButton()
        initSaveButton()
    }

    private fun initControllerBox() {
        val it = connector.controllers().iterator()
        while (it.hasNext()) controllerBox.addItem(it.next().idName())

        val default = defaultController()
        codePane.text = default.code()
        controllerBox.addItem(default.idName())
        controllerBox.selectedIndex = controllerBox.itemCount - 1

        controllerBox.addActionListener {
            val string = controllerBox.selectedItem as String
            var inId = false
            val idStr = string.filter { char ->
                if (char == ' ') inId = true
                inId
            }
            val id = idStr[2]
            val controller = connector.getController(id) ?: defaultController()
            codePane.text = controller.code()
        }
    }

    private fun initDeleteButton() {
        deleteButton.addActionListener {
            val string = controllerBox.selectedItem as String
            var inId = false
            val idStr = string.filter { char ->
                if (char == ' ') inId = true
                inId
            }
            val id = idStr[2]
            if (connector.containsController(id)) {
                connector.removeController(id)
                controllerBox.removeItemAt(controllerBox.selectedIndex)
            }
            controllerBox.selectedIndex = controllerBox.itemCount - 1
            val default = defaultController()
            controllerBox.selectedItem = default.idName()
            codePane.text = default.code()
            connector.saveControllers()
        }
    }

    private fun initSaveButton() {
        saveButton.addActionListener {
            try {
                val idName = controllerBox.selectedItem as String
                var inId = false
                val idStr = idName.filter { char ->
                    if (char == ' ') inId = true
                    inId
                }
                val id = idStr[2]
                val controller = Scanner(codePane.text).parseController() ?: throw ParseException("Not expected EOF")
                if (connector.containsController(controller.id) && controller.id != id)
                    throw Exception("Controller with id${controller.id} is already exist")
                controllerBox.selectedItem = controller.idName()
                val selectedIndex = controllerBox.selectedIndex
                if (connector.containsController(id)) {
                    connector.removeController(id)
                    connector.addController(controller)
                } else {
                    connector.addController(controller)
                    controllerBox.addItem(defaultController().idName())
                }
                controllerBox.selectedIndex = selectedIndex
                connector.saveControllers()
            } catch(exc: Exception) {
                SwingUtilities.invokeLater { MessageBox("Error", "${exc.javaClass}: " + exc.toString()) }
            }
        }
    }

    private fun Controller.idName() = "Controller.$name [$id]"

    private fun defaultController()
            = Controller(connector.getUniqueId() ?: throw Exception("Unable to allocate a unique id"),
            "default", '0', ImmutableList<Expression>())
}
