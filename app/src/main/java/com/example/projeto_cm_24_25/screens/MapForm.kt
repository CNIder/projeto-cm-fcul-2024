package com.example.projeto_cm_24_25.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.viewbinding.BuildConfig
import coil3.compose.AsyncImage
import com.example.projeto_cm_24_25.R
import com.example.projeto_cm_24_25.data.MapViewModel
import com.example.projeto_cm_24_25.data.model.ItemMarker
import com.example.projeto_cm_24_25.navigation.Screen
import com.example.projeto_cm_24_25.ui.theme.primaryColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.DragState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapForm(navController: NavHostController, mapViewModel: MapViewModel) {
    val context = LocalContext.current

    // Lista de tipos de lugares
    val placeTypes = listOf("Safe Zone", "Supply Zone", "Infected Zone")

    // Variáveis para Dropdown
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    var textfieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown

    // Posição inicial do marcador
    val initialPosition = LatLng(38.7223, -9.1393)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
    }

    var uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var textInput by remember { mutableStateOf("") }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedImageUri = uri
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        CenterAlignedTopAppBar(
            title = { Text("Report Place") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = primaryColor,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            ),
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to map screen"
                    )
                }
            }
        )

        // Mostra a imagem capturada
        capturedImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Captured photo",
                modifier = Modifier.size(100.dp).padding(8.dp)
            )
        }

        TextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Zone name", style = TextStyle(fontSize = 15.sp, color = Color.White)) },
            textStyle = TextStyle(fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold),
            placeholder = { Text("e.g, FCUL \uD83C\uDFEB safe place") },
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedPlaceholderColor = Color.White,
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Box {
            OutlinedTextField(
                value = selectedText,
                onValueChange = { selectedText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .onGloballyPositioned { coordinates -> textfieldSize = coordinates.size.toSize() },
                label = { Text("Zone type") },
                trailingIcon = {
                    Icon(icon, "contentDescription", Modifier.clickable { expanded = !expanded })
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(with(LocalDensity.current) { textfieldSize.width.toDp() })
            ) {
                placeTypes.forEach { label ->
                    DropdownMenuItem(
                        onClick = {
                            selectedText = label
                            expanded = false
                        },
                        text = { Text(label) }
                    )
                }
            }
        }

        // Mapa
        val markerState = rememberMarkerState()
        GoogleMap(
            modifier = Modifier.fillMaxHeight(0.75f).padding(14.dp).border(2.dp, Color.Red),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            onMapClick = {
                markerState.position = it
                markerPosition = it
            }
        ) {
            markerState?.let { Marker(state = it) }
        }

        // Botão para tirar foto
        Button(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            onClick = {
                if (!cameraPermissionState.status.isGranted) {
                    cameraPermissionState.launchPermissionRequest()
                } else {
                    cameraLauncher.launch(uri)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Take Photo")
        }

        // Botão para fazer upload e reportar
        Button(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            onClick = {
                if (textInput.isEmpty() || selectedText.isEmpty() || markerPosition == null || capturedImageUri == null) {
                    Toast.makeText(context, "❌ All fields are required!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isUploading = true
                val base64Image = capturedImageUri?.let { ImgBBApi.convertImageToBase64(context, it) }

                if (base64Image != null) {
                    ImgBBApi.uploadImage(
                        context = context,
                        base64Image = base64Image,
                        onSuccess = { imageUrl ->
                            val itemMarker = ItemMarker(
                                name = textInput,
                                type = selectedText,
                                icon = imageUrl, // URL retornada pelo ImgBB
                                latitude = markerPosition!!.latitude,
                                longitude = markerPosition!!.longitude
                            )
                            mapViewModel.addMarker(itemMarker) // Adiciona o marcador no Firebase
                            isUploading = false
                            Toast.makeText(context, "\uD83D\uDCDD Report submitted successfully!", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        },
                        onError = { error ->
                            isUploading = false
                            Toast.makeText(context, "❌ Upload failed: $error", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    isUploading = false
                    Toast.makeText(context, "❌ Failed to process image!", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isUploading,
            colors = ButtonDefaults.buttonColors(containerColor = if (isUploading) Color.Gray else Color.Red)
        ) {
            Text(if (isUploading) "Uploading..." else "Report \uD83C\uDF0D")
        }
    }
}

fun containsInvalidChars(name: String): Boolean {
    return name.contains(".") || name.contains("#") ||
            name.contains("\$") || name.contains("[") ||
            name.contains("[")
}


fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}