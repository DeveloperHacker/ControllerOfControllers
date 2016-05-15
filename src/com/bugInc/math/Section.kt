package com.bugInc.math

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

data class Section (val first: Vector, val second: Vector) {

    constructor(x1: Double, y1: Double, x2: Double, y2: Double) : this(Vector(x1, y1), Vector(x2, y2))

    constructor(x1: Int, y1: Int, x2: Int, y2: Int) : this(Vector(x1, y1), Vector(x2, y2))

    fun intersect(section: Section, error: Double = 0.0): Vector? {
        val x11 = first.x
        val x12 = second.x
        val x21 = section.first.x
        val x22 = section.second.x
        val y11 = first.y
        val y12 = second.y
        val y21 = section.first.y
        val y22 = section.second.y
        val x = (x11 * x21 * y12 - x11 * x21 * y22 - x11 * x22 * y12 + x11 * x22 * y21 - x12 * x21 * y11 + x12 * x21 * y22 + x12 * x22 * y11 - x12 * x22 * y21) / (x11 * y21 - x11 * y22 - x12 * y21 + x12 * y22 - x21 * y11 + x21 * y12 + x22 * y11 - x22 * y12)
        val y = (x11 * y12 * y21 - x11 * y12 * y22 - x12 * y11 * y21 + x12 * y11 * y22 - x21 * y11 * y22 + x21 * y12 * y22 + x22 * y11 * y21 - x22 * y12 * y21) / (x11 * y21 - x11 * y22 - x12 * y21 + x12 * y22 - x21 * y11 + x21 * y12 + x22 * y11 - x22 * y12)
        if (x != x || y != y) return null
        val O = Vector(x, y)
        if ((second - O).abs + (O - first).abs > (first - second).abs + error) return null
        if ((section.second - O).abs + (O - section.first).abs > (section.first - section.second).abs + error) return null
        return O
    }
}
