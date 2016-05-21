package com.bugInc.core

import com.fazecast.jSerialComm.SerialPort
import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class Connector(private val letter: (Letter) -> Unit, private val byte: (Byte) -> Unit) {

    private var port: Port? = null

    private val controllers = HashMap<Byte, Controller>()

    private val sensors = HashMap<Byte, Sensor>()

    fun controllers(): Iterator<Controller> = controllers.values.iterator()

    fun sensors(): Iterator<Sensor> = sensors.values.iterator()

    fun addController(controller: Controller): Boolean {
        if (controllers.contains(controller.id)) return false
        controllers.put(controller.id, controller)
        return true
    }

    fun addSensor(sensor: Sensor): Boolean {
        if (sensors.contains(sensor.id)) return false
        sensors.put(sensor.id, sensor)
        return true
    }

    fun removeController(id: Byte) {
        if (!controllers.contains(id)) throw NotFoundExceptions("Controller with id: $id is not found")
        controllers.remove(id)
    }

    fun getController(id: Byte) = controllers[id]

    fun getSensors(id: Byte) = sensors[id]

    fun containsController(id: Byte) = controllers.containsKey(id)

    fun containsSensors(id: Byte) = sensors.containsKey(id)

    fun getUniqueId(): Byte? {
        var id = 0.toByte()
        while (id <= Byte.MAX_VALUE) {
            if (!controllers.containsKey(id)) return id
            ++id
        }
        return null
    }

    private val receive: (Letter) -> Unit = { letter ->
        letter(letter)
//        when (letter.command) {
//            SYNC -> (controllers[letter.id] as Controller).state = letter.data
//            else -> throw IllegalArgumentException("Command ${letter.command} is not found")
//        }
    }

    fun send(letter: Letter) = port?.let { it.put(letter) } ?: throw PortNotOpenException()

    fun send(string: String) = port?.let { it.put(string) } ?: throw PortNotOpenException()

    fun send(byte: Byte) = port?.let { it.put(byte) } ?: throw PortNotOpenException()

    fun run(port: SerialPort) {
        this.port = Port(port, receive, { byte(it) })
        this.port!!.start()
    }

    fun stop() {
        if (port != null) port!!.stop()
        else throw PortNotOpenException()
    }
}

class PortNotOpenException() : Exception()

class NotFoundExceptions(message: String) : Exception(message)