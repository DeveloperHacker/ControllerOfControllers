package com.bugInc.core

import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class Geometry constructor(
        var pos: Vector, var outline: List<Vector>
) {

    private constructor(geometry: Geometry) : this(geometry.pos, geometry.outline)

    constructor(pos: Vector, width: Double, height: Double) : this(rectangle(pos, width, height))

    companion object {
        fun rectangle(pos: Vector, width: Double, height: Double): Geometry {
            val outline = ArrayList<Vector>(3)
            outline.add(Vector(width, 0.0))
            outline.add(Vector(width, height))
            outline.add(Vector(0.0, height))
            return Geometry(pos, outline)
        }
    }
}
