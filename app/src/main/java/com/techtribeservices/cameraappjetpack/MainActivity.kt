package com.techtribeservices.cameraappjetpack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.techtribeservices.cameraappjetpack.ui.theme.CameraAppJetPackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var hasCameraPermission by remember {
                mutableStateOf(true)
            }
            CameraAppJetPackTheme {
                Layout()
            }
        }
    }
}

@Composable
fun Layout() {
    var hasPermission by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        println("launched effect ${hasPermission}")
    }

    when(hasPermission) {
        true -> {
            print("camera - ${hasPermission}")
            CameraView()
        }
        false -> {
            print("camera - ${hasPermission}")
            ErrorView()
        }
    }
}

@Composable
fun CameraView() {

}

@Composable
fun ErrorView() {
    Text(text = "Please enable camera permission and try again")
}

// handle camera permission
private fun requestPermission() {

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CameraAppJetPackTheme {
        CameraView()
    }
}