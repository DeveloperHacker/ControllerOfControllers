package com.bugInc.core

import com.fazecast.jSerialComm.SerialPort
import java.io.PrintWriter
import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

data class Letter constructor(
        val id: Char,
        val command: Char,
        val data: Char
) {
    companion object {
        val FIELDS = 3
    }
}

class Port constructor(openPort: SerialPort, private val letter: (Letter) -> Unit, private val char: (Char) -> Unit) {

    val outMail: Queue<Char> = LinkedList()
    val inpMail: Queue<Char> = LinkedList()

    private lateinit var inpThread: Thread
    private lateinit var outThread: Thread

    private var start = false
    fun start() {
        scanner.useDelimiter("")
        if (!start) {
            inpThread = object : Thread() {
                override fun run() {
                    while (start) {
                        if (scanner.hasNext()) {
                            val char = scanner.next()[0]
                            inpMail.add(char)
                            char(char)
                        }
                        while (inpMail.size >= Letter.FIELDS) {
                            letter(Letter(
                                    id = inpMail.poll(),
                                    command = inpMail.poll(),
                                    data = inpMail.poll()
                            ))
                        }
                    }
                }
            }
            outThread = object : Thread() {
                override fun run() {
                    while (start) {
                        synchronized(outMail) {
                            if (outMail.size >= 3) {
                                val str = StringBuilder("")
                                for (i in 1..3) str.append(outMail.poll())
                                output.print(str.toString())
                                output.flush()
                            }
                        }
                        sleep(sendDelay)
                    }
                }
            }
            start = true
            inpThread.start()
            outThread.start()
        }
    }

    fun stop() {
        start = false
    }

    val output = PrintWriter(openPort.outputStream)

    val scanner = Scanner(openPort.inputStream)

    fun put(char: Char) = synchronized(outMail) { outMail.add(char) }

    fun put(letter: Letter) = synchronized(outMail) {
        outMail.add(letter.id)
        outMail.add(letter.command)
        outMail.add(letter.data.toChar())
    }

    fun put(string: String) = synchronized(outMail) { string.forEach { char -> outMail.add(char) } }
}