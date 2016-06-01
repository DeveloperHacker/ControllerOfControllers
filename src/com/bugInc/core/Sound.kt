package com.bugInc.core

import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

object Sound {

    private val clip: Clip? = null

    fun stop() = clip?.let {
        it.stop()
        it.close()
    }

    fun play(name: String) {
        stop()
        val soundFile = File(name)
        val ais = AudioSystem.getAudioInputStream(soundFile)
        val clip = AudioSystem.getClip()
        clip.open(ais)
        clip.framePosition = 0
        clip.start()
    }
}
