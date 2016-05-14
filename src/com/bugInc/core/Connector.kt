package com.bugInc.core

import com.fazecast.jSerialComm.SerialPort
import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

const val SYNC: Byte = 1
const val GET_STATE: Byte = 2
const val SET_STATE: Byte = 3

class Connector(private val _send: (Letter) -> Any, private val _receive: (Letter) -> Any) {

    private var port: Port? = null

    private val controllers = HashMap<Byte, Controller>()

    fun addController(ID: Byte, name: String, state: Byte = UNABLE, geometry: Geometry) {
        if (controllers.contains(ID)) throw IllegalArgumentException("Controller with ID: $ID is already exist")
        controllers.put(ID, Controller(ID, name, state, geometry))
    }

    fun removeController(ID: Byte) {
        if (!controllers.contains(ID)) throw IllegalArgumentException("Controller with ID: $ID is not found")
        controllers.remove(ID)
    }

    fun getState(ID: Byte): Byte {
        if (!controllers.contains(ID)) throw IllegalArgumentException("Controller with ID: $ID is not found")
        return (controllers[ID] as Controller).state
    }

    fun setState(ID: Byte, state: Byte) {
        if (!controllers.contains(ID)) throw IllegalArgumentException("Controller with ID: $ID is not found")
        (controllers[ID] as Controller).state = state
        send(Letter(ID, SET_STATE, state, 0))
    }

    fun syncState(ID: Byte) {
        if (!controllers.contains(ID)) throw IllegalArgumentException("Controller with ID: $ID is not found")
        send(Letter(ID, GET_STATE, 0, 0))
    }

    fun getGeometry(ID: Byte): Geometry {
        if (!controllers.contains(ID)) throw IllegalArgumentException("Controller with ID: $ID is not found")
        return (controllers[ID] as Controller).geometry
    }

    private val receive: (Letter) -> Unit = { letter ->
        _receive(letter)
        when (letter.COMMAND) {
            SYNC -> (controllers[letter.ID] as Controller).state = letter.DATA
            else -> throw IllegalArgumentException("Command ${letter.COMMAND} is not found")
        }
    }

    fun send(letter: Letter) {
        _send(letter)
        if (port != null) port!!.put(letter)
        else throw Error("COM Port is not open")
    }

    fun run(port: SerialPort) {
        this.port = Port(port, receive)
        this.port!!.start = true
    }

    fun stop() {
        if (port != null) port!!.start = false
        else throw Error("COM Port is not open")
    }
}