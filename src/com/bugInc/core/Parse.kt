package com.bugInc.core

import com.bugInc.containers.ImmutableList
import java.io.File
import java.io.PrintWriter
import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

class ParseException(message: String) : Exception(message)

fun Connector.saveControllers() {
    val output = "bin/initControllers.txt".output()
    val it = controllers().iterator()
    while (it.hasNext()) output.println("${it.next().code()}\n")
    output.flush()
    output.close()
}

fun Connector.loadControllers() {
    val input = "bin/initControllers.txt".input()
    var controller = input.parseController()
    while (controller != null) {
        if (containsController(controller.id))
            throw ParseException("Controller.${controller.name} with id ${controller.id} is already exist")
        addController(controller)
        controller = input.parseController()
    }
    input.close()
}

data class Trio<F, S, T>(val first: F, val second: S, val third: T)

fun parseAudios(): Collection<Trio<Char, IntRange, String>> {
    val input = "bin/initAudio.txt".input()
    val result = ArrayList<Trio<Char, IntRange, String>>()
    while (input.hasNextLine()) {
        val line = input.nextLine()
        if (line == "") continue
        val scnLine = Scanner(line)
        if (!scnLine.hasNext()) throw ParseException("initAudios: Syntax error in line ${result.size}, not found Id")
        val token = scnLine.next()
        if (token.length != 1) throw ParseException("initAudios: Syntax error in line ${result.size}, id is not char")
        val id = token[0]
        if (!scnLine.hasNextInt()) throw ParseException("initAudios: Syntax error in line ${result.size}, not found min Value")
        val min = scnLine.nextInt()
        if (!scnLine.hasNextInt()) throw ParseException("initAudios: Syntax error in line ${result.size}, not found max Value")
        val max = scnLine.nextInt()
        if (!scnLine.hasNext()) throw ParseException("initAudios: Syntax error in line ${result.size}, not found audio url")
        val url = scnLine.next()
        // it is ................
        result.add(Trio(id, min..max, url))
    }
    return result
}

fun loadCommands(): Map<String, String> {
    val map = HashMap<String, String>()
    val input = "bin/initLines.txt".input()
    while (input.hasNextLine()) {
        val line = input.nextLine()
        if (line == "") continue
        val it = Scanner(line)
        val key = it.next()
        val value = it.next()
        map.put(key, value)
    }
    input.close()
    return map
}

fun Scanner.parseController(): Controller? {
    val input = this
    val itTCB = templateControllerBody().iterator()
    val controllerBuilder = ControllerBuilder()
    var token: String
    var empty = true
    while (itTCB.hasNext()) {
        do {
            if (!input.hasNext()) if (empty) return null else throw ParseException("Not expected EOF")
            else token = input.next()
        } while (token == "")
        empty = false
        if (token == "*") throw ParseException("Undefined reference '$token'")
        val eToken = itTCB.next()
        if (eToken == token) continue
        if (eToken == "*") {
            var readExpression = false
            while (token != "}") {
                readExpression = controllerBuilder.addToken(token)
                if (!input.hasNext()) throw ParseException("Not expected EOF")
                else token = input.next()
            }
            if (readExpression) throw ParseException("Token '}' not expected")
            itTCB.next()
            continue
        }
        if (eToken != "") throw ParseException("Undefined reference '$token'")
        controllerBuilder.addToken(token)
    }
    return controllerBuilder.result()
}

private class ControllerBuilder() {
    private var id: Char? = null
    private var name: String? = null
    private var startState: Char? = null
    private var transitions = LinkedList<Expression>()
    private var expressionBuilder: ExpressionBuilder? = null
    private var size = 0

    fun addToken(token: String) = when (size) {
        0 -> {
            if (token.length != 1) throw ParseException("id is not Char")
            id = token[0]
            ++size
            false
        }
        1 -> {
            name = token
            ++size
            false
        }
        2 -> {
            if (token.length != 1) throw ParseException("id is not Char")
            startState = token[0]
            ++size
            false
        }
        else -> {
            if (expressionBuilder == null) expressionBuilder = ExpressionBuilder()
            val exp = expressionBuilder?.addToken(token)
            if (exp != null) {
                expressionBuilder = null
                transitions.add(exp)
                false
            } else {
                true
            }
        }
    }

    fun result() = if (size != 3 || expressionBuilder != null) null else Controller(id!!, name!!, startState!!, ImmutableList(transitions))
}

private class ExpressionBuilder() {
    private var sensorId: Char? = null
    private var minValue: Int? = null
    private var maxValue: Int? = null
    private var inpState: Char? = null
    private var outState: Char? = null
    private var size = 0
    private val itTTB = templateTransitionsBody().iterator()

    fun addToken(token: String): Expression? {
        val eToken = itTTB.next()
        if (eToken == token) return null
        if (eToken != "") throw ParseException("Undefined reference $token")
        when (size) {
            0 -> {
                if (token.length != 1) throw ParseException("id is not Char")
                sensorId = token[0]
                ++size
            }
            1 -> {
                try {
                    minValue = token.toInt()
                } catch(exc: ParseException) {
                    throw ParseException("minValue is not Int")
                }
                ++size
            }
            2 -> {
                try {
                    maxValue = token.toInt()
                } catch(exc: ParseException) {
                    throw ParseException("maxValue is not Int")
                }
                ++size
            }
            3 -> {
                try {
                    if (token.length != 1) throw ParseException("id is not Char")
                    inpState = token[0]
                } catch(exc: ParseException) {
                    throw ParseException("inpState is not Int")
                }
                ++size
            }
            4 -> {
                try {
                    if (token.length != 1) throw ParseException("id is not Char")
                    outState = token[0]
                } catch(exc: ParseException) {
                    throw ParseException("outState is not Int")
                }
                ++size
                return Expression(sensorId!!, minValue!!, maxValue!!, inpState!!, outState!!)
            }
        }
        return null
    }
}

private var templateControllerBody: List<String>? = null
private fun templateControllerBody(): List<String> {
    templateControllerBody?.let { return it }
    val template = LinkedList<String>()
    template.add("controller")
    template.add("{")
    template.add("id")
    template.add("=")
    template.add("")
    template.add("name")
    template.add("=")
    template.add("")
    template.add("startState")
    template.add("=")
    template.add("")
    template.add("transitions")
    template.add("=")
    template.add("{")
    template.add("*")
    template.add("}")
    template.add("}")
    templateControllerBody = template
    return template
}

private var templateTransitionsBody: List<String>? = null
private fun templateTransitionsBody(): List<String> {
    templateTransitionsBody?.let { return it }
    val template = LinkedList<String>()
    template.add("(")
    template.add("")
    template.add(".value")
    template.add("in")
    template.add("")
    template.add("..")
    template.add("")
    template.add("&&")
    template.add("state")
    template.add("==")
    template.add("")
    template.add(")")
    template.add("->")
    template.add("")
    templateTransitionsBody = template
    return template
}

fun Controller.code(): String {
    val code = StringBuilder()
    code.append("controller {\r\n")
    code.append("   id = $id\r\n")
    code.append("   name = $name\r\n")
    code.append("   startState = $startState\r\n")
    code.append("   transitions = {\r\n")
    transitions.forEach { exp ->
        code.append("       ( ${exp.sensorId} .value in ${exp.minValue} .. ${exp.maxValue} " +
                "&& state == ${exp.inpState} ) -> ${exp.outState}\r\n")
    }
    code.append("   }\r\n")
    code.append("}\r\n")
    return code.toString()
}

fun String.input(): Scanner {
    val file = File(this)
    if (!file.isFile) throw ParseException("File not found ${file.absolutePath}")
    return Scanner(file, "windows-1251")
}

fun String.output(): PrintWriter {
    val file = File(this)
    return file.printWriter()
}