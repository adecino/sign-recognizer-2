package com.ccg.slrskeletonapp

import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ccg.slrcore.common.Thresholder
import com.ccg.slrcore.engine.SimpleExecutionEngine
import com.ccg.slrcore.system.NoTrigger
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun Sign(navigationToSecondScreen:(String)->Unit) {
    lateinit var SLREngine: SimpleExecutionEngine

    val inputProgress = MutableStateFlow(0)

    val interaction = remember { MutableInteractionSource() }

    val isCameraVisible by interaction.collectIsPressedAsState()

    if (isCameraVisible) {
        SLREngine.poll()
    }
    else {
        if (SLREngine.buffer.trigger is NoTrigger)
            SLREngine.buffer.triggerCallbacks()
        SLREngine.pause()
        //inputProgress.value = 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(300.dp))
        ExtendedFloatingActionButton(
            onClick = {},
            interactionSource = interaction
        ) {
            Icon(Icons.Sharp.ThumbUp, "Sign Language Input")
            Text("Hold this button down to initiate the sign recognizer.")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignPreview() {
    Sign({})
}