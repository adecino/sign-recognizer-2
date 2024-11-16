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
import androidx.compose.ui.input.nestedscroll.nestedScrollModifierNode

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
        map.put("Dog", SignData("dog.png", "asdf", "Dog"))
        map.put("Arm", SignData("arm.png", "asdf", "Arm"))
        map.put("Bus", SignData("bus.png", "asdf", "Bus"))
        map.put("Car", SignData("car.png", "asdf", "Car"))
        map.put("Cat", SignData("cat.png", "asdf", "Cat"))
        map.put("Dog", SignData("dog.png", "asdf", "Dog"))
        map.put("Egg", SignData("egg.png", "asdf", "Egg"))
        map.put("Hat", SignData("hat.png", "asdf", "Hat"))
        map.put("Jet", SignData("jet.png", "asdf", "Jet"))
        map.put("Keg", SignData("keg.png", "asdf", "Keg"))
        map.put("Kid", SignData("kid.png", "asdf", "Kid"))
        map.put("Man", SignData("man.png", "asdf", "Man"))
        map.put("Nut", SignData("nut.png", "asdf", "Nut"))
        map.put("Paw", SignData("paw.png", "asdf", "Paw"))
        map.put("Pen", SignData("pen.png", "asdf", "Pen"))
        map.put("Pie", SignData("pie.png", "asdf", "Pie"))
        map.put("Pig", SignData("pig.png", "asdf", "Pig"))
        map.put("Sun", SignData("sun.png", "asdf", "Sun"))
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
                userScrollEnabled = true,
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