package com.bugInc.math

import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

data class Vector (val x: Double, val y: Double) {

    var abs: Double = -1.0
        get() {
            if (field < 0) field = Math.sqrt(x * x + y * y)
            return field
        }

    constructor() : this(0.0, 0.0)

    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

    operator fun invoke() = this * (1 / abs)
    operator fun invoke(vector: Vector) = scalar_mul(vector)
    operator fun invoke(theta: Double) = rotate(theta)
    infix operator fun plus(num: Int) = Vector(x + num, y + num)
    infix operator fun plus(num: Double) = Vector(x + num, y + num)
    infix operator fun plus(vector: Vector) = Vector(x + vector.x, y + vector.y)
    infix operator fun minus(num: Int) = Vector(x - num, y - num)
    infix operator fun minus(num: Double) = Vector(x - num, y - num)
    infix operator fun minus(vector: Vector) = Vector(x - vector.x, y - vector.y)
    infix operator fun times(num: Int) = Vector(x * num, y * num)
    infix operator fun times(num: Double) = Vector(x * num, y * num)
    infix operator fun times(vector: Vector) = x * vector.y - vector.x * y
    operator fun unaryMinus() = Vector(-x, -y)
    operator fun unaryPlus() = this

    fun x() = x.toInt()
    fun y() = y.toInt()

    fun scalar_mul(vector: Vector) = x * vector.x + y * vector.y
    fun cos(vector: Vector) = scalar_mul(vector) / (abs * vector.abs)
    fun rotate(theta: Double): Vector {
        val cos = Math.cos(theta)
        val sin = Math.sin(theta)
        return Vector(x * cos - y * sin, x * sin + y * cos)
    }

    fun `~=`(vector: Vector, error: Double)
            = `~=`(x, vector.x, error) && `~=`(y, vector.y, error)

    companion object {
        fun `~=`(v1: Double, v2: Double, error: Double)
                = (v1 in v2 - error..v2 + error) && (v2 in v1 - error..v1 + error)

        fun round(value: Double, scale: Int)
                = Math.round(value * Math.pow(10.0, scale.toDouble())) / Math.pow(10.0, scale.toDouble())

        fun random(minX: Double, maxX: Double, minY: Double, maxY: Double, random: Random)
                = Vector(random.nextDouble() * (maxX - minX) + minX, random.nextDouble() * (maxY - minY) + minY)

        fun random(minX: Int, maxX: Int, minY: Int, maxY: Int, random: Random)
                = random(minX.toDouble(), maxX.toDouble(), minY.toDouble(), maxY.toDouble(), random)
    }
}

