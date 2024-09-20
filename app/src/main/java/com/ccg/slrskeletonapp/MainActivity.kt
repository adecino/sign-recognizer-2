package com.ccg.slrskeletonapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ccg.slrcore.common.Empties
import com.ccg.slrcore.common.FocusSublistFilter
import com.ccg.slrcore.common.ImageMPResultWrapper
import com.ccg.slrcore.common.Thresholder
import com.ccg.slrcore.engine.SimpleExecutionEngine
import com.ccg.slrcore.preview.ComposeCanvasPainterInterface
import com.ccg.slrcore.preview.HandPreviewPainter
import com.ccg.slrcore.preview.PainterMode
import com.ccg.slrcore.system.NoTrigger
import com.ccg.slrcore.system.SlidingWindowFill
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlinx.coroutines.flow.MutableStateFlow


class MainActivity : ComponentActivity() {

    lateinit var SLREngine: SimpleExecutionEngine

    private val currResult = MutableStateFlow (
        ImageMPResultWrapper(Empties.EMPTY_HANDMARKER_RESULTS, Empties.EMPTY_BITMAP)
    )

    private val inputProgress = MutableStateFlow(0)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.interpolationSwitch) {
            item.isChecked = !item.isChecked
            SLREngine.isInterpolating = item.isChecked;
            return true
        }

        return false;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SLREngine = SimpleExecutionEngine(this, {
            this.signPredictor.outputFilters.clear()
            this.signPredictor.outputFilters.add(FocusSublistFilter(listOf("yellow", "hot", "dance")))
            this.signPredictor.outputFilters.add(Thresholder(0.8F))
        }) { sign ->
            runOnUiThread {
                Toast.makeText(this, "Guessed: ${sign} ", Toast.LENGTH_SHORT).show()
            }
        }

        SLREngine.posePredictor.addCallback("preview_update") {
                mpResult -> currResult.value = mpResult
        }
        SLREngine.buffer.filler = object: SlidingWindowFill<HandLandmarkerResult>(60) {
            override fun fill(
                buffer: MutableList<HandLandmarkerResult>,
                elem: HandLandmarkerResult,
                triggered: Boolean
            ) {
                super.fill(buffer, elem, triggered)
                inputProgress.value = buffer.size
            }
        }
        SLREngine.buffer.trigger = NoTrigger()

        setContent {
            SLRView ()
        }
    }

    @Composable
    fun Content() {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val progress by inputProgress.collectAsState()
            CircularProgressIndicator(
                progress = progress / 60.0F,
                modifier = Modifier.width(64.dp),
                color = Color.Yellow
            )
        }
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun SLRView() {
        val mpResult by this.currResult.collectAsState()
        val interaction = remember { MutableInteractionSource() }
        val isCameraVisible by interaction.collectIsPressedAsState()

        if (isCameraVisible) {
            SLREngine.poll()
        } else {
            if (SLREngine.buffer.trigger is NoTrigger)
                SLREngine.buffer.triggerCallbacks()
            SLREngine.pause()
            inputProgress.value = 0
        }

        Scaffold (
            modifier = Modifier
                .fillMaxSize(),
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {},
                    interactionSource = interaction
                ) {
                    Icon(Icons.Sharp.ThumbUp, "Sign Language Input")
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ){
                padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (isCameraVisible) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize(),
                        onDraw = {
                            if (mpResult.image != Empties.EMPTY_BITMAP) {
                                val img = mpResult.getBitmap(size)
                                HandPreviewPainter(
                                    ComposeCanvasPainterInterface(
                                        this
                                    ),
                                    PainterMode.IMAGE_AND_SKELETON
                                ).paint(
                                    img,
                                    mpResult.result,
                                    img.width.toFloat(),
                                    img.height.toFloat()
                                )
                            }
                        }
                    )
                }
                Content()
            }
        }
    }
}