package com.example.personalizedmusicapp

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YoutubePlayer(youtubeVideoId: String) {
    val youTubePlayerView = rememberYoutubePlayerView(youtubeVideoId)

    DisposableEffect(Unit) {
        onDispose {
            youTubePlayerView.release()
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
        factory = { context -> youTubePlayerView }
    )
}

@Composable
private fun rememberYoutubePlayerView(youtubeVideoId: String): YouTubePlayerView {
    val context = LocalContext.current
    return remember {
        createYoutubePlayerView(context, youtubeVideoId)
    }
}

private fun createYoutubePlayerView(context: Context, youtubeVideoId: String): YouTubePlayerView {
    val youTubePlayerView = YouTubePlayerView(context).apply {
        addYouTubePlayerListener(createYoutubePlayerListener(youtubeVideoId))
        enableBackgroundPlayback(true)
    }
    return youTubePlayerView
}

private fun createYoutubePlayerListener(youtubeVideoId: String): AbstractYouTubePlayerListener {
    return object : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
            youTubePlayer.loadVideo(youtubeVideoId, 0f)
        }
    }
}