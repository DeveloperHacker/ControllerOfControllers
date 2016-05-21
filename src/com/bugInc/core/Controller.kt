package com.bugInc.core

import com.bugInc.containers.ImmutableList
import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

data class Expression(val sensorId: Char, val minValue: Byte, val maxValue: Byte, val inpState: Char, val outState: Char)

class Controller(
        val id: Char, val name: String, val startState: Char, val transitions: ImmutableList<Expression>
) : Comparable<Controller> {

    var state = startState

    fun nextState(sensorId: Char, value: Byte): Char? {
        transitions.forEach { exp ->
            if (exp.sensorId == sensorId && value in exp.minValue..exp.maxValue && state == exp.inpState) {
                state = exp.outState
                return state
            }
        }
        return null
    }

    fun boundSensors(): Set<Char> {
        val result = TreeSet<Char>()
        transitions.forEach { exp -> if (!result.contains(exp.sensorId)) result.add(exp.sensorId) }
        return result
    }

    override fun compareTo(other: Controller) = id.compareTo(other.id)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this !is Controller) return false
        return id == (other as Controller).id
    }

    override fun hashCode() = id.hashCode()
}

class Sensor(val id: Char, private val connector: Connector) : Comparable<Sensor> {

    private val controllers = TreeSet<Controller>()

    var value: Byte = 0
        set(value) = controllers.forEach {
            field = value
            val state = it.nextState(id, value)
            if (state != null) connector.send(Letter(id, Command.SetState(), state))
        }

    fun countControllers() = controllers.size

    fun add(controller: Controller): Boolean {
        if (controllers.contains(controller)) return false
        controllers.add(controller)
        return true
    }

    fun remove(controller: Controller): Boolean {
        if (!controllers.contains(controller)) return false
        controllers.remove(controller)
        return true
    }

    override fun compareTo(other: Sensor) = id.compareTo(other.id)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this !is Sensor) return false
        return id == (other as Sensor).id
    }

    override fun hashCode() = id.hashCode()
}