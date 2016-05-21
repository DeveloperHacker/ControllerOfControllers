package com.bugInc.core

import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

enum class Command private constructor(val id: Byte) {
    REPORT(1),
    READ(2),
    WRITE(3);

    init {
        opcodes.put(id, this)
    }

    operator fun invoke() = id

    companion object {

        private val opcodes = HashMap<Byte, Command>()

        operator fun get(id: Byte) = opcodes[id]
    }
}
