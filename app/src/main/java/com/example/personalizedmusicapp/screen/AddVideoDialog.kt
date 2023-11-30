package com.example.personalizedmusicapp.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.personalizedmusicapp.data.Item
import com.example.personalizedmusicapp.model.VideoEvent
import com.example.personalizedmusicapp.model.VideoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVideoDialog(
    state: VideoState,
    onEvent: (VideoEvent) -> Unit,
    modifier: Modifier = Modifier
)
{
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onEvent(VideoEvent.HideDialog) },
        title = { Text(text = "Add Video") },
        text = {
            TextField(
                value = state.youtubeId,
                onValueChange = {
                    onEvent(VideoEvent.SetVideo(it, "", "04:00"))
                },
                placeholder = {
                    Text(text = "Youtube ID")
                }
            )
        },
        confirmButton={
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = {
                    onEvent(VideoEvent.SaveVideo)
                }) {
                    Text(text = "Save")
                }
            }
        }
    )
}