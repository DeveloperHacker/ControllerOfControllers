package com.bugInc.app

import java.awt.Container
import java.awt.Dimension
import javax.swing.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

const val LineHeight = 25
const val SpacerHeight = 5
const val ButtonLength = 100
const val LabelLength = 60
const val TextLength = 250
const val SpacerLength = 5
const val LineLength = SpacerLength + LabelLength + TextLength + SpacerLength + ButtonLength + 2 * SpacerLength

fun Container.spacer(width: Int, height: Int): JPanel {
    val spacer = JPanel()
    spacer.dimension = Dimension(width, height)
    this.add(spacer)
    return spacer
}

fun Container.button(width: Int, height: Int, name: String): JButton {
    val button = JButton(name)
    button.dimension = Dimension(width, height)
    this.add(button)
    return button
}

fun <T> Container.comboBox(current: Int, vararg items: T): JComboBox<T> {
    val box = JComboBox<T>()
    items.forEach { item -> box.addItem(item) }
    box.selectedIndex = current
    this.add(box)
    return box
}

fun <T> Container.comboBox(): JComboBox<T> {
    val box = JComboBox<T>()
    this.add(box)
    return box
}

fun Container.textField(width: Int, height: Int, text: String): JTextField {
    val field = JTextField(text)
    field.dimension = Dimension(width, height)
    this.add(field)
    return field
}

fun Container.textPane(width: Int, height: Int, text: String): JTextPane {
    val pane = JTextPane()
    pane.text = text
    pane.dimension = Dimension(width, height)
    this.add(pane)
    return pane
}

fun textPane(text: String): JTextPane {
    val pane = JTextPane()
    pane.text = text
    return pane
}

fun Container.scrollPane(width: Int, height: Int, container: Container): JScrollPane {
    val pane = JScrollPane(container)
    pane.dimension = Dimension(width, height)
    this.add(pane)
    return pane
}

fun Container.label(width: Int, height: Int, name: String): JLabel {
    val label = JLabel(name)
    label.dimension = Dimension(width, height)
    this.add(label)
    return label
}

var JComponent.dimension: Dimension
    set(value) {
        maximumSize = value
        preferredSize = value
        minimumSize = value
    }
    get() = preferredSize
