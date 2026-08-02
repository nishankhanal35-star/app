package com.nishankhanal.voiceassistant

import java.io.*

object WavUtils {
    @Throws(IOException::class)
    fun pcmToWav(pcmFile: File, wavFile: File, sampleRate: Int, channels: Int, bitsPerSample: Int) {
        val pcmSize = pcmFile.length().toInt()
        val wavOut = DataOutputStream(BufferedOutputStream(FileOutputStream(wavFile)))

        val byteRate = sampleRate * channels * bitsPerSample / 8

        // RIFF header
        writeString(wavOut, "RIFF")
        writeInt(wavOut, 36 + pcmSize)
        writeString(wavOut, "WAVE")

        // fmt chunk
        writeString(wavOut, "fmt ")
        writeInt(wavOut, 16)
        writeShort(wavOut, 1.toShort()) // PCM
        writeShort(wavOut, channels.toShort())
        writeInt(wavOut, sampleRate)
        writeInt(wavOut, byteRate)
        writeShort(wavOut, (channels * bitsPerSample / 8).toShort())
        writeShort(wavOut, bitsPerSample.toShort())

        // data chunk
        writeString(wavOut, "data")
        writeInt(wavOut, pcmSize)

        val fis = DataInputStream(BufferedInputStream(FileInputStream(pcmFile)))
        val buffer = ByteArray(1024)
        var read: Int
        while (fis.read(buffer).also { read = it } > 0) {
            wavOut.write(buffer, 0, read)
        }
        fis.close()
        wavOut.flush()
        wavOut.close()
    }

    private fun writeInt(out: DataOutputStream, value: Int) {
        out.write(value shr 0)
        out.write(value shr 8)
        out.write(value shr 16)
        out.write(value shr 24)
    }

    private fun writeShort(out: DataOutputStream, value: Short) {
        out.write(value.toInt() shr 0)
        out.write(value.toInt() shr 8)
    }

    private fun writeString(out: DataOutputStream, value: String) {
        out.writeBytes(value)
    }
}
