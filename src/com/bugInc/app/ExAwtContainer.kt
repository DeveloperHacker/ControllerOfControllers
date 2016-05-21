package com.bugInc.app

import java.awt.Color
import java.awt.Container
import java.awt.Dimension
import javax.swing.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

const val LineHeight = 25
const val ValueLength = 60
const val NameLength = 120
const val LampLength = LineHeight
const val SpacerLength = 5
const val SpacerHeight = 5
const val ButtonLength = 100
const val LabelLength = 60
const val IDLength = 70
const val TextLength = 250
const val ControllerLength = 2 * ValueLength + IDLength + NameLength + LampLength + 7 * SpacerLength
const val SensorLength = 2 * ValueLength + IDLength + LampLength + 6 * SpacerLength
const val LogHeight = 10 * LineHeight
const val CodeHeight = 12 * LineHeight
const val MessageHeight = 3 * LineHeight
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

fun Container.row(init: Container.() -> Unit): JComponent {
    val pane = JPanel()
    pane.layout = BoxLayout(pane, BoxLayout.LINE_AXIS)
    pane.init()
    this.add(pane)
    return pane
}

fun Container.column(init: Container.() -> Unit): JComponent {
    val pane = JPanel()
    pane.layout = BoxLayout(pane, BoxLayout.PAGE_AXIS)
    pane.init()
    this.add(pane)
    return pane
}

var JComponent.dimension: Dimension
    set(value) {
        maximumSize = value
        preferredSize = value
        minimumSize = value
    }
    get() = preferredSize

operator fun Pair<Color, Color>.get(part: Int): Color {
    var p = part
    if (p > 255) p = 255
    if (p < 0) p = 0
    val t = p.toDouble() / 255.0
    val r = first.red * t + second.red * (1 - t)
    val g = first.green * t + second.green * (1 - t)
    val b = first.blue * t + second.blue * (1 - t)
    return Color(r.toInt(), g.toInt(), b.toInt())
}