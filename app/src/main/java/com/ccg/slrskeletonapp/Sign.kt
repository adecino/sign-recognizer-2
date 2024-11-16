package com.ccg.slrskeletonapp

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun Sign(navigationToSecondScreen:(String)->Unit) {
    val name = remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(300.dp))
        Button(onClick = {
            navigationToSecondScreen(name.value)
        }) {
            Text("Search")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignPreview() {
    Sign({})
}