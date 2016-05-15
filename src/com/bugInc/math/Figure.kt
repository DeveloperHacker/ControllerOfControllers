package com.bugInc.math

import com.bugInc.containers.ImmutableList
import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

data class Figure (var position: Vector, val outline: ImmutableList<Vector>, val eqError: Double) {

    constructor(figure: Figure, eqError: Double = figure.eqError)
    : this(figure.position, figure.outline, eqError)

    constructor(position: Vector, vararg outline: Vector, eqError: Double)
    : this(position, ImmutableList(outline.toList()), eqError)

    constructor(position: Vector, outline: List<Vector>, eqError: Double)
    : this(position, ImmutableList(outline.toList()), eqError)

    constructor(position: Vector, width: Double, height: Double, eqError: Double)
    : this(rectangle(position, width, height, eqError), eqError)

    constructor(position: Vector, size: Vector, eqError: Double)
    : this(rectangle(position, size.x, size.y, eqError), eqError)

    constructor(position: Vector, qVertices: Int, radius: Double, theta: Double = 0.0, eqError: Double)
    : this(rightFigure(position, qVertices, radius, theta, eqError), eqError)

    private var center: Vector? = null
    fun center(): Vector {
        center?.let { return it }
        var center = Vector()
        var i = outline.size
        outline.forEach { vector ->
            center += vector * i
            --i
        }
        this.center = Vector(center.x / outline.size, center.y / outline.size)
        return this.center!!
    }

    fun isInside(point: Vector): Boolean? {
        if (!convex()) return null
        var m = point - position
        var direct = 0.0
        var temp: Double
        var init = false
        for (vector in outline) {
            temp = vector * m
            m -= vector
            if (temp != 0.0) {
                if (!init) init = true
                else if (direct * temp < 0) return false
                direct = temp
            }
        }
        return true
    }

    private var convex: Boolean? = null
    fun convex(): Boolean {
        convex?.let { return it }
        var direct = outline.last() * outline.first()
        for (i in 1..outline.lastIndex)
            if (0 > direct * (outline[i - 1] * outline[i])) {
                convex = false
                return false
            }
        convex = true
        return true
    }

    private var height: Double? = null
    fun height(): Double {
        height ?: if (maxY != null && minY != null) height = Math.abs(maxY!! - maxY!!)
        height?.let { return it }
        var prev = position
        var min = prev.y
        var max = prev.y
        for (vector in outline) {
            prev += vector
            if (prev.y > max) max = prev.y
            if (prev.y < min) min = prev.y
        }
        minY = min
        maxY = max
        height = Math.abs(max - min)
        return height!!
    }

    private var width: Double? = null
    fun width(): Double {
        width ?: if (maxX != null && minX != null) width = Math.abs(maxX!! - maxX!!)
        width?.let { return it }
        var prev = position
        var min = prev.x
        var max = prev.x
        for (vector in outline) {
            prev += vector
            if (prev.x > max) max = prev.x
            if (prev.x < min) min = prev.x
        }
        minX = min
        maxX = max
        width = Math.abs(max - min)
        return width!!
    }

    private var minX: Double? = null
    fun minX(): Double {
        minX?.let { return it }
        var min = position.x
        var current = position
        for (vector in outline) {
            current += vector
            if (current.x < min) min = current.x
        }
        minX = min
        return min
    }

    private var maxX: Double? = null
    fun maxX(): Double {
        maxX?.let { return it }
        var max = position.x
        var current = position
        for (vector in outline) {
            current += vector
            if (current.x < max) max = current.x
        }
        maxX = max
        return max
    }

    private var minY: Double? = null
    fun minY(): Double {
        minY?.let { return it }
        var min = position.y
        var current = position
        for (vector in outline) {
            current += vector
            if (current.y < min) min = current.y
        }
        minY = min
        return min
    }

    private var maxY: Double? = null
    fun maxY(): Double {
        maxY?.let { return it }
        var max = position.y
        var current = position
        for (vector in outline) {
            current += vector
            if (current.y > max) max = current.y
        }
        maxY = max
        return max
    }

    fun scale(origin: Vector, scale: Double): Figure {
        var position = origin + (position - origin) * scale
        val outline = LinkedList<Vector>()
        for (vector in this.outline) outline.add(vector * scale)
        return Figure(position, outline, eqError)
    }

    fun rotate(origin: Vector, theta: Double): Figure {
        var position = (position - origin).rotate(theta) + origin
        val outline = LinkedList<Vector>()
        for (vector in outline) outline.add(vector.rotate(theta))
        return Figure(position, outline, eqError)
    }

    init {
        var sum = Vector()
        outline.forEach { vector -> sum += vector }
        if (!Vector.`~=`(sum.abs, 0.0, eqError) || outline.size == 0)
            throw IllegalArgumentException("Figure can not be unclosed")
    }

    companion object {
        fun rectangle(position: Vector, width: Double, height: Double, eqError: Double): Figure {
            val outline = ArrayList<Vector>(4)
            outline.add(Vector(width, 0.0))
            outline.add(Vector(0.0, height))
            outline.add(Vector(-width, 0.0))
            outline.add(Vector(0.0, -height))
            return Figure(position, outline, eqError)
        }

        fun rightFigure(position: Vector, qVertices: Int, radius: Double, theta: Double, eqError: Double): Figure {
            val outline = ArrayList<Vector>()
            var prevRadius = Vector(0.0, -radius).rotate(theta)
            var nextRadius: Vector
            for (i in 0..qVertices - 1) {
                nextRadius = prevRadius.rotate(Math.PI * 2.0 / qVertices)
                outline.add(nextRadius - prevRadius)
                prevRadius = nextRadius
            }
            return Figure(position, outline, eqError)
        }
    }
}
