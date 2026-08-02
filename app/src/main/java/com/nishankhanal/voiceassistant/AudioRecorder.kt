package com.nishankhanal.voiceassistant

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.min

object AudioRecorder {
    // 16 kHz, mono, PCM 16
    private const val SAMPLE_RATE = 16000

    fun recordToFile(durationSeconds: Int = 5): File? {
        val minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        val bufferSize = if (minBufSize == AudioRecord.ERROR || minBufSize == AudioRecord.ERROR_BAD_VALUE) SAMPLE_RATE * 2 else minBufSize

        val recorder = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
        val audioData = ShortArray(bufferSize / 2)
        val pcmFile = File.createTempFile("voice_capture", ".pcm", null)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(pcmFile)
            recorder.startRecording()
            var secondsRecorded = 0
            val readBuffer = ShortArray(1024)
            val endTime = System.currentTimeMillis() + durationSeconds * 1000L
            while (System.currentTimeMillis() < endTime) {
                val read = recorder.read(readBuffer, 0, readBuffer.size)
                if (read > 0) {
                    // write little endian
                    val bytes = ShortArray(read)
                    System.arraycopy(readBuffer, 0, bytes, 0, read)
                    for (i in 0 until read) {
                        val s = bytes[i]
                        fos.write((s and 0x00FF).toByte().toInt())
                        fos.write(((s.toInt() shr 8) and 0x00FF).toByte().toInt())
                    }
                }
            }
            recorder.stop()
            fos.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            recorder.release()
            try { fos?.close() } catch (e: IOException) {}
        }

        // convert pcm to wav
        return try {
            val wavFile = File.createTempFile("voice_capture", ".wav", null)
            WavUtils.pcmToWav(pcmFile, wavFile, SAMPLE_RATE, 1, 16)
            pcmFile.delete()
            wavFile
        } catch (e: Exception) {
            e.printStackTrace()
            pcmFile
        }
    }
}
