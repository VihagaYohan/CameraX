package com.techtribeservices.cameraappjetpack

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.techtribeservices.cameraappjetpack.ui.theme.CameraAppJetPackTheme
import com.google.common.util.concurrent.ListenableFuture
// import com.sun.istack.Builder

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var hasCameraPermission by remember {
                mutableStateOf(true)
            }
            CameraAppJetPackTheme {
                Scaffold(
                    topBar = {
                        AppBar()
                    }
                ) {innerPadding ->
                    Layout(
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar() {
    androidx.compose.material3.TopAppBar(title = {
        Text(text = "Camera X")
    })
}

@Composable
fun Layout(
    modifier: Modifier = Modifier
) {
    var hasPermission by remember {
        mutableStateOf(false)
    }
    val localContext = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(Unit) {
        val result = ContextCompat.checkSelfPermission(
            localContext,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        hasPermission = result

        if(!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
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
    lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    val localContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK
    val preview: androidx.camera.core.Preview = androidx.camera.core.Preview.Builder().build()
//    val previewView = remember {
//        previewView(localContext)
//    }
    val previewView = remember {
        PreviewView(localContext)
    }

    var cameraSelector: CameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    LaunchedEffect(Unit) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(localContext)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
            preview.setSurfaceProvider(previewView.surfaceProvider)
            // bind preview
            //bindPreview(cameraProvider, lifecycle)
        }, ContextCompat.getMainExecutor(localContext))
    }

    Column (
        modifier = Modifier
            .fillMaxSize(),
        // verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .width(250.dp)
                .height(250.dp))

        Spacer(modifier = Modifier.height(50.dp))
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Capture")
        }
    }
}

fun bindPreview(cameraProvider: ProcessCameraProvider,
                lifecycle: LifecycleOwner) {
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK
    val preview: androidx.camera.core.Preview = androidx.camera.core.Preview.Builder().build()
    var cameraSelector: CameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
}

@Composable
fun ErrorView() {
    Column(
        modifier =  Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Please enable camera permission and try again")
    }
}

// handle camera permission
private fun requestPermission(context: Context) {
    val result = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CameraAppJetPackTheme {
        CameraView()
    }
}