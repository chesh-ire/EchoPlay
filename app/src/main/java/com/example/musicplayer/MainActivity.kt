package com.example.musicplayer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.Player
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : ComponentActivity() {

    private var exoPlayer: ExoPlayer? = null
    private var playerUiState by mutableStateOf(PlayerUiState())


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()

        setContent {
            PlayerScreen(
                onPlayClick = { playMusic() },
                onPauseClick = { pauseMusic() },
                uiState = playerUiState
            )
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun playMusic() {
        val intent = Intent(this, MusicService::class.java)
        startForegroundService(intent)
    }


    private fun pauseMusic() {
        stopService(Intent(this, MusicService::class.java))
    }


    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        exoPlayer = null
    }
}

@Composable
fun PlayerScreen(
    uiState: PlayerUiState,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (uiState.isBuffering) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading...")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onPlayClick,
            enabled = !uiState.isPlaying && !uiState.isBuffering
        ) {
            Text("Play")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onPauseClick,
            enabled = uiState.isPlaying
        ) {
            Text("Pause")
        }
    }

}






data class PlayerUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false
)


