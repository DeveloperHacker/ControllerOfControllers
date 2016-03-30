package com.bugInc.core;

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

public class Vector {
    public final double x;
    public final double y;

    public Vector() {
        x = 0;
        y = 0;
    }

    public Vector(Vector vector) {
        x = vector.x;
        y = vector.y;
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double abs() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector add(Vector vector) {
        return new Vector(x + vector.x, y + vector.y);
    }

    public Vector rem(Vector vector) {
        return new Vector(x - vector.x, y - vector.y);
    }

    public Vector scale(double num) {
        return new Vector(x * num, y * num);
    }

    public Vector minus() {
        return new Vector(-x, -y);
    }

    public double dot(Vector vector) {
        return x * vector.x + y * vector.y;
    }

    public double cos(Vector vector) {
        return dot(vector) / (abs() * vector.abs());
    }

    public double mul(Vector vector) {
        return x * vector.y - vector.x * y;
    }

    public Vector rotate(double theta) {
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        return new Vector(x * cos - y * sin, x * sin + y * cos);
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        if (this == obj) return true;
        if (obj instanceof Vector) {
            Vector vector = (Vector) obj;
            return (vector.x == x) && (vector.y == y);
        } else return false;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "point = " + x + "i + " + y + "j";
    }
}
