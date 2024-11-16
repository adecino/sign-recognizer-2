package com.ccg.slrskeletonapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import android.widget.Toast
import android.content.Context
import android.view.Gravity

class SignData(imagePath: String, videoPath: String, word: String) {
    var imagePath = "not_found.png";
    var videoPath = "";
    var word = "";
    init {
        this.imagePath = imagePath;
        this.videoPath = videoPath;
        this.word = word;
    }
}

class SignDataLocalStore : SignDataInterface  {
    var map: HashMap<String, SignData> = HashMap<String, SignData> ()

    init {
        map.put("Hello", SignData("HelloSign.png", "asdf", "Hello"))
        map.put("Thank You", SignData("ThankYouSign.png", "asdf", "Thank You"))
        map.put("I", SignData("asdf", "asdf", "I"))
        map.put("Me", SignData("asdf", "asdf", "Me"))
        map.put("You", SignData("asdf", "asdf", "You"))
        map.put("He", SignData("asdf", "asdf", "He"))
        map.put("She", SignData("asdf", "asdf", "She"))
        map.put("We", SignData("asdf", "asdf", "We"))
        map.put("Us", SignData("asdf", "asdf", "Us"))
        map.put("It", SignData("asdf", "asdf", "It"))
        map.put("In", SignData("asdf", "asdf", "In"))
        map.put("On", SignData("asdf", "asdf", "On"))
        map.put("To", SignData("asdf", "asdf", "To"))
        map.put("At", SignData("asdf", "asdf", "At"))
        map.put("Be", SignData("asdf", "asdf", "Be"))
        map.put("My", SignData("asdf", "asdf", "My"))
        map.put("Up", SignData("asdf", "asdf", "Up"))
        map.put("Go", SignData("asdf", "asdf", "Go"))
        map.put("Am", SignData("asdf", "asdf", "Am"))
        map.put("Do", SignData("asdf", "asdf", "Do"))
        map.put("Is", SignData("asdf", "asdf", "Is"))
        map.put("So", SignData("asdf", "asdf", "So"))
        map.put("If", SignData("asdf", "asdf", "If"))
        map.put("As", SignData("asdf", "asdf", "As"))
        map.put("An", SignData("asdf", "asdf", "An"))
    }


    override fun getData(): List<SignData> {
        val list = ArrayList<SignData>()
        for ((index, value) in map) {
            list.add(SignData(value.imagePath, value.videoPath, value.word))
        }
        return list;
    }

    override fun getSignDataByText(word: String): SignData {
        return map.getOrElse(word, {SignData("", "", "")});
    }

}

interface SignDataInterface {
    fun getData(): List<SignData>;
    fun getSignDataByText(word: String): SignData;
}

data class SignEntry(val word: String, val imagePath: String)

suspend fun loadSignEntries(context: Context): List<SignData> {
    val sdi: SignDataInterface = SignDataLocalStore();
    val res = sdi.getData();

    return res
}

fun searchByText(text: String): SignData {
    val sdi: SignDataInterface = SignDataLocalStore();
    val res = sdi.getSignDataByText(text);
    return res;
}

@Composable
fun Search(navController: NavController) {
    val context = LocalContext.current
    var signEntries by remember { mutableStateOf(emptyList<SignData>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        signEntries = loadSignEntries(context)
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            Text("Loading...")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(signEntries) { entry ->
                    SignBox(entry) {
                        val param = entry.word + "|" + entry.imagePath + "|" + entry.videoPath
                        navController.navigate("result/$param")
                    }
                }
            }
        }

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
                Text("Search")
            }
        }
    }
}

@Composable
fun SignBox(signEntry: SignData, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter("file:///android_asset/images/${signEntry.imagePath}"),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = signEntry.word)
    }
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    val navController = rememberNavController()
    Search(navController)
}