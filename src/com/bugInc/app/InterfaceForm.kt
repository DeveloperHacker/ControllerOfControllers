package com.bugInc.app

import com.bugInc.core.Controller
import com.bugInc.core.Sensor
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.util.*
import javax.swing.*
import javax.swing.Timer
import javax.swing.border.BevelBorder

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class InterfaceForm(
        title: String, controllers: Collection<Controller>, sensors: Collection<Sensor>
) : JFrame(title) {

    val repaintCycleTime = 100
    val updateCycleTime = 100

    private val paintClock: Timer
    private val updateClock: Timer

    private val controllersLamps = TreeMap<Char, Lamp>()
    private val sensorsLamps = TreeMap<Char, Lamp>()
    private val controllers = TreeMap<Controller, Char>()
    private val sensors = TreeMap<Sensor, Byte>()
    private val controllerStates = TreeMap<Char, JLabel>()
    private val sensorValues = TreeMap<Char, JLabel>()

    init {
        contentPane = JPanel()
        contentPane.layout = BoxLayout(contentPane, BoxLayout.LINE_AXIS)
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        isVisible = true

        val width = ControllerLength + SensorLength + 4 * SpacerLength
        val height = (Math.max(controllers.size, sensors.size) + 1) * (LineHeight + SpacerHeight) + 3 * SpacerHeight

        spacer(SpacerLength, height)
        column {
            controllers.forEach { controller ->
                val lamp = Lamp()
                controllersLamps.put(controller.id, lamp)
                this@InterfaceForm.controllers.put(controller, controller.state)
                spacer(ControllerLength, SpacerHeight)
                row {
                    spacer(SpacerLength, LineHeight)
                    label(IDLength, LineHeight, "Controller: ")
                    spacer(SpacerLength, LineHeight)
                    label(ValueLength, LineHeight, "ID: " + controller.id.toString())
                    spacer(SpacerLength, LineHeight)
                    label(NameLength, LineHeight, "Name: \"" + controller.name.toString() + "\"")
                    spacer(SpacerLength, LineHeight)
                    val label = label(ValueLength, LineHeight, "State: ${controller.state}")
                    controllerStates.put(controller.id, label)
                    spacer(SpacerLength, LineHeight)
                    lamp.dimension = Dimension(LampLength, LineHeight)
                    this.add(lamp)
                    spacer(SpacerLength, LineHeight)
                }.border = BorderFactory.createBevelBorder(BevelBorder.LOWERED)
                spacer(ControllerLength, SpacerHeight)
            }
        }
        spacer(SpacerLength, height)
        column {
            sensors.forEach { sensor ->
                val lamp = Lamp()
                sensorsLamps.put(sensor.id, lamp)
                this@InterfaceForm.sensors.put(sensor, sensor.value.toByte())
                spacer(SensorLength, SpacerHeight)
                row {
                    spacer(SpacerLength, LineHeight)
                    label(IDLength, LineHeight, "Sensor: ")
                    spacer(SpacerLength, LineHeight)
                    label(ValueLength, LineHeight, "ID: " + sensor.id.toString())
                    spacer(SpacerLength, LineHeight)
                    val label = label(ValueLength, LineHeight, "Value: ${sensor.value}")
                    sensorValues.put(sensor.id, label)
                    spacer(SpacerLength, LineHeight)
                    lamp.dimension = Dimension(LampLength, LineHeight)
                    this.add(lamp)
                    spacer(SpacerLength, LineHeight)
                }.border = BorderFactory.createBevelBorder(BevelBorder.LOWERED)
                spacer(SensorLength, SpacerHeight)
            }
        }
        spacer(SpacerLength, height)

        setBounds(50, 50, width, height)
        isResizable = false

        paintClock = Timer(repaintCycleTime) {
            controllersLamps.values.forEach {
                if (it.power != 0) {
                    it.update()
                    it.repaint()
                }
            }
            sensorsLamps.values.forEach {
                if (it.power != 0) {
                    it.update()
                    it.repaint()
                }
            }
        }
        updateClock = Timer(updateCycleTime) { update() }
        paintClock.start()
        updateClock.start()
    }

    override fun dispose() {
        paintClock.stop()
        updateClock.stop()
        super.dispose()
    }

    fun update() {
        controllers.forEach { controller, prevState ->
            if (controller.state != prevState) {
                controllersLamps[controller.id]?.pulse()
                controllerStates[controller.id]?.text = "State: ${controller.state}"
                controllers[controller] = controller.state
            }
        }
        sensors.forEach { sensor, prevValue ->
            if (sensor.value != prevValue) {
                sensorsLamps[sensor.id]?.pulse()
                sensorValues[sensor.id]?.text = "Value: ${sensor.value}"
                sensors[sensor] = sensor.value
            }
        }
    }
}

class Lamp : JComponent() {

    private val MaxPower = 255
    var power = MaxPower
        private set

    init {
        border = BorderFactory.createBevelBorder(BevelBorder.RAISED)
    }

    fun pulse() {
        power = MaxPower
    }

    fun update() {
        power -= 10
        if (power < 0) power = 0
    }

    override fun paint(graphics: Graphics) {
        super.paint(graphics)

        val clipRect = getComponentGraphics(graphics).create().clipBounds
        val clipX = clipRect.x + 4
        val clipY = clipRect.y + 4
        val clipW = clipRect.width - 9
        val clipH = clipRect.height - 9

        var onColor = Color(255, 0, 0)
        var offColor = Color(139, 0, 0)
        val end = 1
        for (i in 0..end) {
            onColor = onColor.darker()
            offColor = offColor.darker()
            graphics.color = (onColor.darker() to offColor.darker())[power]
            graphics.drawOval(clipX + i, clipY + i, clipW - 2 * i, clipH - 2 * i)
        }
        onColor = Color(255, 0, 0)
        offColor = Color(139, 0, 0)
        graphics.color = (onColor to offColor)[power]
        graphics.fillOval(clipX + end, clipY + end, clipW - 2 * end, clipH - 2 * end)
    }
}