package com.bugInc.core

import com.fazecast.jSerialComm.SerialPort
import java.io.PrintWriter
import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

data class Letter constructor(
        val ID: Byte,
        val COMMAND: Byte,
        val DATA: Byte,
        val FLAG: Byte
) {
    companion object {
        val FIELDS = 4
    }
}

class Port constructor(openPort: SerialPort, private val letter: (Letter) -> Unit, private val byte: (Byte) -> Unit) {

    val outMail: Queue<Byte> = LinkedList()
    val inpMail: Queue<Byte> = LinkedList()

    private lateinit var inpThread: Thread
    private lateinit var outThread: Thread

    private var start = false
    fun start() {
        if (!start) {
            inpThread = object : Thread() {
                override fun run() {
                    while (start) {
                        synchronized(scanner) {
                            if (scanner.hasNextLine()) {
                                val b = scanner.nextLine().toByte()
                                inpMail.add(b)
                                byte(b)
                            }
                            if (inpMail.size == Letter.FIELDS) {
                                letter(Letter(
                                        ID = inpMail.poll(),
                                        COMMAND = inpMail.poll(),
                                        DATA = inpMail.poll(),
                                        FLAG = inpMail.poll()
                                ))
                            }
                        }
                        sleep(100)
                    }
                }
            }
            outThread = object : Thread() {
                override fun run() {
                    while (start) {
                        synchronized(outMail) {
                            if (!outMail.isEmpty()) {
                                output.print(outMail.element())
                                output.flush()
                                outMail.remove()
                            }
                        }
                        sleep(200)
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
            outMail.add(letter.FLAG)
        }
    }

    fun put(byte: Byte) = synchronized(outMail) { outMail.add(byte) }
}