package com.zenith.focus.domain.usecase

import android.content.Context
import android.media.MediaPlayer
import android.media.AudioAttributes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioLoopUseCase(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentFilePath: String? = null

    suspend fun startLoop(filePath: String) = withContext(Dispatchers.Default) {
        try {
            if (mediaPlayer?.isPlaying == true) {
                stopLoop()
            }

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(filePath)
                isLooping = true
                prepare()
                start()
            }
            currentFilePath = filePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun stopLoop() = withContext(Dispatchers.Default) {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
            mediaPlayer = null
            currentFilePath = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun pauseLoop() = withContext(Dispatchers.Default) {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun resumeLoop() = withContext(Dispatchers.Default) {
        try {
            if (mediaPlayer != null && mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVolume(volume: Float) {
        try {
            mediaPlayer?.setVolume(volume, volume)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    fun getCurrentFile(): String? = currentFilePath

    fun release() {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
