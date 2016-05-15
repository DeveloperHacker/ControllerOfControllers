package com.bugInc.core

import com.fazecast.jSerialComm.SerialPort
import java.io.PrintWriter
import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

data class Letter constructor(
        val ID: Byte,
        val COMMAND: Byte,
        val DATA: Byte
) {
    companion object {
        val FIELDS = 3
    }
}

class Port constructor(openPort: SerialPort, private val letter: (Letter) -> Unit, private val byte: (Byte) -> Unit) {

    val outMail: Queue<Byte> = LinkedList()
    val inpMail: Queue<Byte> = LinkedList()

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
                            val b = char.toByte()
                            inpMail.add(b)
                            byte(b)
                        }
                        while (inpMail.size >= Letter.FIELDS) {
                            letter(Letter(
                                    ID = inpMail.poll(),
                                    COMMAND = inpMail.poll(),
                                    DATA = inpMail.poll()
                            ))
                        }
                    }
                }
            }
            outThread = object : Thread() {
                override fun run() {
                    while (start) {
                        synchronized(outMail) {
                            if (!outMail.isEmpty()) {
                                val str = StringBuilder("")
                                while (!outMail.isEmpty()) str.append(outMail.poll().toChar())
                                output.print(str.toString())
                                output.flush()
                            }
                        }
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

    fun put(letter: Letter) {
        synchronized(outMail) {
            outMail.add(letter.ID)
            outMail.add(letter.COMMAND)
            outMail.add(letter.DATA)
        }
    }

    fun put(byte: Byte) = synchronized(outMail) { outMail.add(byte) }
}