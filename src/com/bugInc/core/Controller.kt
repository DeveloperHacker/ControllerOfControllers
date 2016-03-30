package com.bugInc.core

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

const val UNABLE: Byte = 0
const val ABLE: Byte = 1

data class Controller constructor(
        val ID: Byte,
        val name: String,
        var state: Byte = UNABLE,
        var geometry: Geometry
)
