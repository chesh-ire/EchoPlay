package com.example.musicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession


class MusicService : Service() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()

       
        exoPlayer = ExoPlayer.Builder(this).build()

        val mediaItem = MediaItem.fromUri(
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        )
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()


        mediaSession = MediaSession.Builder(this, exoPlayer).build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        startForeground(
            1,
            createNotification()
        )

        exoPlayer.play()
        return START_STICKY
    }

    override fun onDestroy() {
        mediaSession.release()
        exoPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null


    @OptIn(UnstableApi::class)
    private fun createNotification(): Notification {
        val channelId = "music_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Music Playback",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Music Playing")
            .setContentText("Streaming audio")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionCompatToken)
            )
            .setOngoing(true)
            .build()
    }

}
