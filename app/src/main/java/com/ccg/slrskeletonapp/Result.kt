package com.ccg.slrskeletonapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@Composable
fun Result(param: String?) {
    val parts = param?.split("|")
    println(parts)
    val context = LocalContext.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {

            if (parts != null) {
                Image(
                    painter = rememberAsyncImagePainter("file:///android_asset/images/${parts.get(1)}"),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
                Text(
                    text = parts.get(0))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    Result("")
}