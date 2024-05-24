package com.techtribeservices.cameraappjetpack

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
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
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.techtribeservices.cameraappjetpack.ui.theme.CameraAppJetPackTheme
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.launch
import java.io.File

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
    modifier: Modifier = Modifier,
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
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {LifecycleCameraController(context)}
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState()}
    val source = remember {
        mutableStateOf(false)
    }
    var sourceImg: Bitmap? = null 

    Scaffold(
        modifier = Modifier,
        snackbarHost =  {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {

                    val file = File.createTempFile("img_",".jpg")
                    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

                    val mainExecutor = ContextCompat.getMainExecutor(context)
                    cameraController.takePicture(
                        outputFileOptions,
                        mainExecutor,
                        object : ImageCapture.OnImageSavedCallback {


                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                println("URI - ${outputFileResults.savedUri}")
                            }

                            override fun onError(exception: ImageCaptureException) {

                            }
                        })
//                        object : ImageCapture.OnImageCapturedCallback() {
//                            // @androidx.annotation.OptIn(ExperimentalGetImage::class)
//                            override fun onCaptureSuccess(image: ImageProxy) {
//                                super.onCaptureSuccess(image)
//                               val correctedBitmap: Bitmap = image
//                                   .toBitmap()
//                                   // .rotateBitmap(image.imageInfo.rotationDegrees)
//
//                                image.close()
////                                sourceImg = correctedBitmap
////                                source.value = true
//                                println(correctedBitmap)
//                            }
//
//                            override fun onError(exception: ImageCaptureException) {
//                                super.onError(exception)
//                                scope.launch {
//                                    snackbarHostState.showSnackbar("Error capturing image ${exception.toString()}")
//                                }
//                            }
//                        })

                }) {
                Text(text = "Capture")
            }
        }
    ) {paddingValues: PaddingValues ->
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(paddingValues),
            factory = {context ->
                PreviewView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setBackgroundColor(1)
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }.also {previewView ->
                    previewView.controller = cameraController
                    cameraController.bindToLifecycle(lifeCycleOwner)
                }
            })

        when(source.value) {
            true -> Image(bitmap = sourceImg as ImageBitmap, contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop)
            false -> Text(text = "No captured image")
        }
    }


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