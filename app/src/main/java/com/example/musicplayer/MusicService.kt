package com.example.musicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MusicService : Service() {

    private lateinit var exoPlayer: ExoPlayer

    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayer.Builder(this).build()

        val mediaItem = MediaItem.fromUri(
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        )

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()

       // startForeground(1, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(1,createNotification())
        exoPlayer.play()
        return START_STICKY
    }

    override fun onDestroy() {
        exoPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val channelId = "music_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Music Playback",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Music Playing")
            .setContentText("Streaming audio")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()
    }
}