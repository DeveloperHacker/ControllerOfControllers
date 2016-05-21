package com.bugInc.core

import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

private val opcodes = HashMap<Char, Command>()

enum class Command private constructor(val id: Char) {
    ControllerReport('1'),
    SensorReport('2'),
    ControllerGetState('3'),
    SensorGetState('4'),
    SetState('5');

    init {
        opcodes.put(id, this)
    }

    operator fun invoke() = id

    companion object {
        operator fun get(id: Char) = opcodes[id]
    }
}
