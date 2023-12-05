package com.example.personalizedmusicapp.screen

import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalizedmusicapp.YoutubePlayer
import com.example.personalizedmusicapp.viewModel.VideoEvent
import com.example.personalizedmusicapp.viewModel.VideoState
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlayerScreen(
    state: VideoState,
    onEvent: (VideoEvent) -> Unit) {
    if (state.videos.isNotEmpty()){
        // Render when the favourites list is not empty
        ScreenWithFavList(state, onEvent)
    } else {
        // Render when the favourites list is empty
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your favourites list is empty.",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp
            )
        }
    }
}

@Composable
fun myPlayer(idx: Int, state: VideoState){
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ){
            if (state.videos.isNotEmpty())
                YoutubePlayer(youtubeVideoId = state.videos[idx].youtubeId)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenWithFavList(state: VideoState, onEvent: (VideoEvent) -> Unit)
{
    // The index of the current video
    var idx by remember {
        mutableStateOf(0)
    }

    // It will refresh the counter when it is changed from false to true.
    var check by remember {
        mutableStateOf(true)
    }

    val currentTime: LocalTime = LocalTime.now()

    var second by remember {
        mutableStateOf(currentTime.second)
    }

    // The minute of the counter
    var counterMin by remember {
        mutableStateOf(state.videos[0].duration.substringBefore(":").toInt())
    }

    // The second of the counter
    var counterSec by remember {
        mutableStateOf(state.videos[0].duration.substringAfter(":").toInt())
    }


    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // If the player is playing the last video and the video is removed from the list,
        // this will limit the index to the updated upper value to avoid crash.
        if (idx > state.videos.size - 1)
            idx = state.videos.size - 1
        key(idx) {
            myPlayer(idx = idx, state = state)
        }

        // Refreshes the counter if check is true.
        if (check) {

            val currentTime: LocalTime = LocalTime.now()

            if (currentTime.second - second == 1 || currentTime.second - second == -59) {
                second = currentTime.second
                counterSec--
            }

            if (counterSec < 0) {
                counterSec = 59
                counterMin--
            }

            if (counterMin <= 0 && counterSec == 0) {
                idx++
                if (idx == state.videos.count())
                    idx = 0
                counterMin = state.videos[idx].duration.substringBefore(":").toInt()
                counterSec = state.videos[idx].duration.substringAfter(":").toInt()
            }

            check = false
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val minStr = String.format("%02d", counterMin)
            val secStr = String.format("%02d", counterSec)
            Text(state.videos[idx].title)
            Text("$minStr:$secStr | ${idx + 1} / ${state.videos.count()}")
        }

        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        shape = CircleShape,
                        color = Color.LightGray
                    ),
                onClick = {
                    idx--
                    if (idx == -1)
                        idx = state.videos.count() - 1
                    counterMin = state.videos[idx].duration.substringBefore(":").toInt()
                    counterSec = state.videos[idx].duration.substringAfter(":").toInt()
                }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Backward",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.size(100.dp, 10.dp))
            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        shape = CircleShape,
                        color = Color.LightGray
                    ),
                onClick = {
                    idx++
                    if (idx == state.videos.count())
                        idx = 0
                    counterMin = state.videos[idx].duration.substringBefore(":").toInt()
                    counterSec = state.videos[idx].duration.substringAfter(":").toInt()
                }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Forward",
                    tint = Color.White
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),

            ) {
            items(state.videos) { video ->
                PlayItemCard(
                    title = video.title,
                    duration = video.duration,
                    onEvent = onEvent,
                    youtubeId = video.youtubeId
                )
            }
            item { Row(modifier = Modifier.height(120.dp)){} }
        }
    }

    object : CountDownTimer(100, 100) {

        override fun onTick(millisUntilFinished: Long) {}

        override fun onFinish() {
            check = true
        }
    }.start()
}

@Composable
fun PlayItemCard(title: String, duration: String, youtubeId: String, onEvent: (VideoEvent) -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            ){
            Column {
                Text(title)
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically){
                    Text(duration)
                    Row(modifier= Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = {
                            onEvent(VideoEvent.DeleteVideoByYoutubeId(youtubeId))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete video"
                            )
                        }
                    }
                }
            }
        }
    }
}