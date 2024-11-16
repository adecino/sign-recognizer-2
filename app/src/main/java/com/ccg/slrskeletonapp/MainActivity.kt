    package com.ccg.slrskeletonapp

    import androidx.compose.material3.Typography
    import android.annotation.SuppressLint
    import android.os.Build
    import android.os.Bundle
    import android.view.Gravity
    import android.view.Menu
    import android.view.MenuItem
    import android.widget.Toast
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.compose.foundation.background
    import androidx.compose.foundation.Canvas
    import androidx.compose.foundation.interaction.MutableInteractionSource
    import androidx.compose.foundation.interaction.collectIsPressedAsState
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.isSystemInDarkTheme
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.sharp.ThumbUp
    import androidx.compose.material3.Button
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.ExtendedFloatingActionButton
    import androidx.compose.material3.FabPosition
    import androidx.compose.material3.Icon
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Surface
    import androidx.compose.material3.Text
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.TextField
    import androidx.compose.material3.darkColorScheme
    import androidx.compose.material3.dynamicDarkColorScheme
    import androidx.compose.material3.dynamicLightColorScheme
    import androidx.compose.material3.lightColorScheme
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.compose.*
    import androidx.navigation.NavController
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
    import org.checkerframework.common.subtyping.qual.Bottom


    val Purple80 = Color(0xFFD0BCFF)
    val PurpleGrey80 = Color(0xFFCCC2DC)
    val Pink80 = Color(0xFFEFB8C8)

    val Purple40 = Color(0xFF6650a4)
    val PurpleGrey40 = Color(0xFF625b71)
    val Pink40 = Color(0xFF7D5260)

    private val AppTypography = androidx.compose.material3.Typography()

    private val DarkColorScheme = darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80
    )

    private val LightColorScheme = lightColorScheme(
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40

        /* Other default colors to override
        background = Color(0xFFFFFBFE),
        surface = Color(0xFFFFFBFE),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F),
        */
    )

    @Composable
    fun ASL_DictionaryTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        // Dynamic color is available on Android 12+
        dynamicColor: Boolean = true,
        content: @Composable () -> Unit
    ) {
        val colorScheme = when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }

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
                    // TODO: Add the guessed sign to the Search Input on this page and search for it.
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
                ASL_DictionaryTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Start ()
                    }
                }
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
        fun Start() {
            val navController = rememberNavController()
            val currentScreen = remember { mutableStateOf("Search") }

            val mpResult by this.currResult.collectAsState()
            val interaction = remember { MutableInteractionSource() }
            val isCameraVisible by interaction.collectIsPressedAsState()

            if (isCameraVisible) {
                SLREngine.poll()
            }
            else {
                if (SLREngine.buffer.trigger is NoTrigger)
                    SLREngine.buffer.triggerCallbacks()
                SLREngine.pause()
                inputProgress.value = 0
            }

            Scaffold (
                topBar = {
                            if (!isCameraVisible) {
                                SearchBar(navController)
                            }
                         },
                modifier = Modifier
                    .fillMaxSize(),
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = {},
                        interactionSource = interaction
                    ) {
                        Text("Make a Sign", maxLines = 1, fontSize = 10.sp)
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
                    NavHost(navController = navController, startDestination = "search") {
                        currentScreen.value = "Search"
                        composable("search") {
                            if(!isCameraVisible) {
                                Search(navController)
                            }
                        }
                        composable("result/{param}") {backStackEntry ->
                            currentScreen.value = "Result"
                            val param = backStackEntry.arguments?.getString("param")
                            Result(param = param)
                        }
                    }
                    Content()
                }
            }
        }

        @Composable
        fun SearchBar(navController: NavController) {
            val context = LocalContext.current
            var inputText by remember { mutableStateOf("") }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Search") }
                )
                Button(onClick = {
                    val res = searchByText(inputText)
                    if (res.word.isEmpty()) {
                        Toast.makeText(context, "Not found", Toast.LENGTH_LONG).apply {
                            setGravity(Gravity.CENTER, 0, 0)
                            show()
                        }
                    } else {
                        val param = "${res.word}|${res.imagePath}|${res.videoPath}"
                        navController.navigate("result/$param")
                    }
                }) {
                    Text("Search", maxLines = 1, fontSize = 10.sp)
                }
            }
        }
    }