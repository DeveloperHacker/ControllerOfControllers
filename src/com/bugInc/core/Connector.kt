package com.bugInc.core

import com.bugInc.app.MessageBox
import com.fazecast.jSerialComm.SerialPort
import java.util.*
import javax.swing.SwingUtilities

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class Connector(private val letter: (Letter) -> Unit, private val char: (Char) -> Unit) {

    private var port: Port? = null

    private val controllers = HashMap<Char, Controller>()

    private val sensors = HashMap<Char, Sensor>()

    fun controllers(): Collection<Controller> = controllers.values

    fun sensors(): Collection<Sensor> = sensors.values

    fun addController(controller: Controller): Boolean {
        if (controllers.contains(controller.id)) return false
        controllers.put(controller.id, controller)
        controller.boundSensors().forEach { sensorId ->
            var sensor = sensors[sensorId]
            if (sensor == null) {
                sensor = Sensor(sensorId, this)
                sensors.put(sensorId, sensor)
            }
            sensor.add(controller)
        }
        return true
    }

    fun removeController(id: Char) {
        if (!controllers.contains(id)) throw NotFoundExceptions("Controller with id: $id is not found")
        val controller = controllers[id]!!
        controller.boundSensors().forEach { id ->
            val sensor = sensors[id]!!
            sensor.remove(controller)
            if (sensor.countControllers() == 0) sensors.remove(id)
        }
        controllers.remove(id)
    }

    fun getController(id: Char) = controllers[id]

    fun getSensor(id: Char) = sensors[id]

    fun containsController(id: Char) = controllers.containsKey(id)

    fun containsSensor(id: Char) = sensors.containsKey(id)

    fun getUniqueId(): Char? {
        var id = 33.toByte()
        while (id <= Byte.MAX_VALUE) {
            if (!controllers.containsKey(id.toChar())) return id.toChar()
            ++id
        }
        return null
    }

    private val receive: (Letter) -> Unit = { letter ->
        letter(letter)
        try {
            when (Command[letter.command]) {
                Command.SensorReport -> {
                    val sensor = sensors[letter.id]
                            ?: throw IllegalArgumentException("Sensor with id ${letter.id} is not found")
                    sensor.value = letter.data.toByte()
                }
                Command.ControllerReport -> {
                    val controller = controllers[letter.id]
                            ?: throw IllegalArgumentException("Controller with id ${letter.id} is not found")
                    controller.state = letter.data
                }
                else -> throw IllegalArgumentException("Command ${letter.command} is not found")
            }
        } catch(exc: Exception) {
            SwingUtilities.invokeLater { "${exc.javaClass}: " + MessageBox("Error", exc.toString()) }
            stop()
        }
    }

    fun send(letter: Letter) = port?.let { it.put(letter) } ?: throw PortNotOpenException()

    fun send(string: String) = port?.let { it.put(string) } ?: throw PortNotOpenException()

    fun send(char: Char) = port?.let { it.put(char) } ?: throw PortNotOpenException()

    fun connect(port: SerialPort) {
        this.port = Port(port, receive, { char(it) })
        this.port!!.start()
    }

    fun disconnect() {
        if (port != null) port!!.stop()
        else throw PortNotOpenException()
    }

    private lateinit var checkTread: Thread

    var stopRequest = true
        private set

    fun run() {
        if (stopRequest) {
            checkTread = object : Thread() {
                override fun run() {
                    controllers.values.forEach { send(Letter(it.id, Command.ControllerGetState(), '0')) }
                    while (!stopRequest) sensors.values.forEach { send(Letter(it.id, Command.SensorGetState(), '0')) }
                }
            }
            stopRequest = false
//            checkTread.start()
        }
    }

    fun stop() {
        stopRequest = true
    }
}

class PortNotOpenException() : Exception()

class NotFoundExceptions(message: String) : Exception(message)