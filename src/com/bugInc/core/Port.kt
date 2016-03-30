package com.bugInc.core

import com.fazecast.jSerialComm.SerialPort
import java.io.PrintWriter
import java.util.*

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

const val QFIELDS = 5

data class Letter constructor(
        val ID: Byte,
        val COMMAND: Byte,
        val DATA: Byte,
        val FLAG: Byte,
        val SUM: Byte = (ID.hashCode() * 3 + COMMAND.hashCode() * 5 + DATA.hashCode() * 7 + FLAG.hashCode() * 11).toByte()
)

class Port constructor(openPort: SerialPort, private val receive: (Letter) -> Unit) {

    var start = false
        set(value) {
            if (!start && value) {
                val thread1 = object : Thread() {
                    override fun run() {
                        while (start) {
                            synchronized(mail) {
                                if (!mail.isEmpty()) {
                                    output.print(mail.element())
                                    output.flush()
                                    mail.remove()
                                }
                            }
                            sleep(200)
                        }
                    }
                }
                val letter = ArrayList<Byte>()
                val thread2 = object : Thread() {
                    override fun run() {
                        while (start) {
                            synchronized(scanner) {
                                if (scanner.hasNextLine()) {
                                    val str = scanner.nextLine()
                                    letter.add(str.toByte())
                                    if (letter.size == QFIELDS) {
                                        receive(Letter(letter[0], letter[1], letter[2], letter[3]))
                                        letter.clear()
                                    }
                                }
                            }
                            sleep(100)
                        }
                    }
                }
                field = value
                thread1.start()
                thread2.start()
            }
            field = value
        }

    val mail: Queue<Byte> = LinkedList()

    val output = PrintWriter(openPort.outputStream)

    val scanner = Scanner(openPort.inputStream)

    fun put(letter: Letter) {
        synchronized(mail) {
            mail.add(letter.ID)
            mail.add(letter.COMMAND)
            mail.add(letter.DATA)
            mail.add(letter.FLAG)
            mail.add(letter.SUM)
        }
    }
}