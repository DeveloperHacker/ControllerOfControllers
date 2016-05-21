package com.bugInc.core

import com.bugInc.containers.ImmutableList

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

data class Expression(val sensorId: Byte, val minValue: Byte, val maxValue: Byte, val inpState: Byte, val outState: Byte)

class Controller(val id: Byte, val name: String, val startState: Byte, val transitions: ImmutableList<Expression>) {

    var state = startState
        private set

    fun nextState(sensorId: Byte, value: Byte): Byte {
        transitions.forEach { exp ->
            if (exp.sensorId == sensorId && value in exp.minValue..exp.maxValue && state == exp.inpState)
                return exp.outState
        }
        return state
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this !is Controller) return false
        return id == (other as Controller).id
    }

    override fun hashCode() = id.hashCode()

    companion object {}
}

data class Sensor(val id: Byte, val name: String)