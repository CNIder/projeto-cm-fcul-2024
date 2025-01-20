package com.example.projeto_cm_24_25.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.viewbinding.BuildConfig
import coil3.compose.AsyncImage
import com.example.projeto_cm_24_25.R
import com.example.projeto_cm_24_25.data.MapViewModel
import com.example.projeto_cm_24_25.data.model.ItemMarker
import com.example.projeto_cm_24_25.data.repository.DataStoreRepository
import com.example.projeto_cm_24_25.navigation.Screen
import com.example.projeto_cm_24_25.ui.theme.primaryColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.DragState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapForm(navController: NavHostController, mapViewModel: MapViewModel) {
    val context = LocalContext.current

    // List dos tipos de lugares
    val placeTypes = listOf("Safe Zone", "Supply Zone", "Infected Zone")

    // Variaveis para Drop Down
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    var textfieldSize by remember { mutableStateOf(Size.Zero)}
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.ArrowDropDown

    // Posicao inicial do marker
    val initialPosition = LatLng(38.7223, -9.1393)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
    }
    var uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
    }
    // Posicao do marker para o utilizador indicar no mapa
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }

    var textInput by remember { mutableStateOf("") }

    // permissao da camera handler
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider", file
    )

    var cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {

        capturedImageUri = uri
    }
    val scrollState = rememberScrollState()

    val dataStore = DataStoreRepository(context)
    val userName = dataStore.getUserName.collectAsState(initial = "").value

    Box(
        modifier = Modifier.fillMaxSize()
        )    {

        Image(
            painter = painterResource(R.drawable.login_3),
            contentDescription = "background image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
                .verticalScroll(scrollState)
        ) {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(25.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,


                        ) {

                        Text(
                            text = "Report",
                            color = Color.White,
                            modifier = Modifier.weight(1f), // Centraliza o texto
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp
                        )

                        // Botão no lado direito
                        ExtendedFloatingActionButton(
                            onClick = { },
                            modifier = Modifier.size(50.dp), // Tamanho ajustado
                            containerColor = Color(238, 31, 39)
                        ) {
                            Text(
                                text = userName?.firstOrNull()?.uppercase()?.toString() ?: "?",
                                fontSize = 16.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Voltar para o ecra da lista dos blogs
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back to map screen"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ){


            TextField(
                value = textInput,
                onValueChange = { newValue -> textInput = newValue },
                label = {
                    Text(
                        "Zone name",
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    )
                },
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                placeholder = {
                    Text("e.g, FCUL, safe place")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .border(0.5.dp, Color.White, RoundedCornerShape(15.dp)),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedPlaceholderColor = Color.White,
                    unfocusedPlaceholderColor = Color.White,
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = Color.White
                )
            )

            Box {
                OutlinedTextField(
                    value = selectedText,
                    onValueChange = { selectedText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .background(Color.Black, shape = RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .border(0.5.dp, Color.White, RoundedCornerShape(12.dp))
                        .onGloballyPositioned { coordinates ->
                            textfieldSize = coordinates.size.toSize()
                        },
                    label = { Text("Zone type") },
                    trailingIcon = {
                        Icon(icon, "contentDescription",
                            Modifier.clickable { expanded = !expanded })
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedPlaceholderColor = Color.White,
                        unfocusedPlaceholderColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Black,
                        unfocusedContainerColor = Color.Black,
                        cursorColor = Color.White
                    )
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { textfieldSize.width.toDp() })
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


            val properties by remember {
                mutableStateOf(
                    MapProperties(
                        mapStyleOptions = MapStyleOptions(
                            """
            [
              {
                "elementType": "geometry",
                "stylers": [
                  {
                    "color": "#212121"
                  }
                ]
              },
              {
                "elementType": "labels.icon",
                "stylers": [
                  {
                    "visibility": "off"
                  }
                ]
              },
              {
                "elementType": "labels.text.fill",
                "stylers": [
                  {
                    "color": "#757575"
                  }
                ]
              },
              {
                "elementType": "labels.text.stroke",
                "stylers": [
                  {
                    "color": "#212121"
                  }
                ]
              },
              {
                "featureType": "administrative",
                "elementType": "geometry",
                "stylers": [
                  {
                    "color": "#757575"
                  }
                ]
              },
              {
                "featureType": "administrative.country",
                "elementType": "labels.text.fill",
                "stylers": [
                  {
                    "color": "#9e9e9e"
                  }
                ]
              },
              {
                "featureType": "administrative.land_parcel",
                "stylers": [
                  {
                    "visibility": "off"
                  }
                ]
              },
              {
                "featureType": "administrative.locality",
                "elementType": "labels.text.fill",
                "stylers": [
                  {
                    "color": "#bdbdbd"
                  }
                ]
              },
              {
                "featureType": "poi",
                "elementType": "labels.text.fill",
                "stylers": [
                  {
                    "color": "#757575"
                  }
                ]
              },
              {
                "featureType": "poi.park",
                "elementType": "geometry",
                "stylers": [
                  {
                    "color": "#181818"
                  }
                ]
              },
              {
                "featureType": "poi.park",
                "elementType": "labels.text.fill",
                "stylers": [
                  {
                    "color": "#616161"
                  }
                ]
              },
              {
                "featureType": "poi.park",
                "elementType": "labels.text.stroke",
                "stylers": [
                  {
                    "color": "#1b1b1b"
                  }
                ]
              },
              {
                "featureType": "road",
                "elementType": "geometry.fill",
                "stylers": [
                  {
                    "color": "#2c2c2c"
                  }
                ]
              },
              {
                "featureType": "road",
                "elementType": "labels.text.fill",
                "stylers": [
                  {
                    "color": "#8a8a8a"
                  }
                ]
              },
              {
                "featureType": "road.arterial",
                "elementType": "geometry",
                "stylers": [
                  {
                    "color": "#373737"
                  }
                ]
              },
              {
                "featureType": "road.highway",
                "elementType": "geometry",
                "stylers": [
                  {
                    "color": "#3c3c3c"
                  }
                ]
              },
              {
                "featureType": "road.highway.controlled_access",
                "elementType": "geometry",
                "stylers": [
                  {
                    "color": "#4e4e4e"
                  }
                ]
              },
              {
                "featureType": "road.local",
                "elementType": "labels.text.fill",
                "stylers": [
                  {
                    "color": "#616161"
                  }
                ]
              },
              {
                "featureType": "transit",
                "elementType": "labels.text.fill",
                "stylers": [
                  {
                    "color": "#757575"
                  }
                ]
              },
              {
                "featureType": "water",
                "elementType": "geometry",
                "stylers": [
                  {
                    "color": "#000000"
                  }
                ]
              },
              {
                "featureType": "water",
                "elementType": "labels.text.fill",
                "stylers": [
                  {
                    "color": "#3d3d3d"
                  }
                ]
              }
            ]
        """.trimIndent()
                        )
                    )
                )
            }

            // Mapa
            val markerState = rememberMarkerState()
            GoogleMap(
                modifier = Modifier.height(300.dp).padding(14.dp)
                  ,
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
                properties = properties,
                onMapClick = {
                    markerState.position = LatLng(it.latitude, it.longitude)
                    markerPosition = markerState.position
                }
            ) {
                markerState?.let {
                    Marker(
                        state = it,
                    )
                }
            }
            // mostra imagem tirada
            if (capturedImageUri.path?.isNotEmpty() == true) {
                AsyncImage(
                    model = capturedImageUri,
                    contentDescription = "photo taked",
                    modifier = Modifier.size(250.dp, 250.dp).align(Alignment.CenterHorizontally)
                )
            }

            // Botao para tirar foto
            Button(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                onClick = {
                    // chamar permissao para camera
                    if (!cameraPermissionState.status.isGranted) {
                        cameraPermissionState.launchPermissionRequest()
                    } else {
                        // tira foto e guarda o URI
                        cameraLauncher.launch(uri)
                    }
                },
                colors = ButtonDefaults.buttonColors(

                    containerColor = Color(238, 31, 39, 200),
                    contentColor = Color.Black
                )
            ) {
                Text("Take photo")
                Image(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Camera Icon",
                    modifier = Modifier.size(24.dp)
                )

            }

            // Botao para submeter formulario
            Button(
                modifier = Modifier.fillMaxWidth().height(80.dp).padding(10.dp).clip(RoundedCornerShape(8.dp)),

                onClick = {
                    // Validar campos
                    if (textInput.isEmpty()) {
                        Toast.makeText(
                            context,
                            "❌ Please provide a location name",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    } else if (selectedText.isEmpty()) {
                        Toast.makeText(
                            context,
                            "❌ Please provide the location type",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    } else if (markerPosition == null) {
                        Toast.makeText(
                            context,
                            "❌ Click on the map to choose location",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    } else if (containsInvalidChars(textInput)) {
                        Toast.makeText(
                            context,
                            "❌ name should not contain '.', '$', '#', '[' or ']' !",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }


                    val itemMarker = ItemMarker(
                        textInput,
                        selectedText,
                        icon = when (selectedText) {
                            "Safe Zone" -> R.drawable.safe_zone_icon.toString()
                            "Infected Zone" -> R.drawable.infected_zone_icon.toString()
                            "Supply Zone" -> R.drawable.supply_zone_icon.toString()
                            else -> ""
                        },
                        markerPosition!!.latitude,
                        markerPosition!!.longitude
                    )
                    mapViewModel.addMarker(itemMarker)
                    Toast.makeText(context, "\uD83D\uDCDD Posted successfully !", Toast.LENGTH_LONG)
                        .show()
                    navController.popBackStack()
                },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(238, 31, 39, 200),
                    contentColor = Color.Black)
            ) {
                Text(text= "+ Report Place", fontWeight = FontWeight.Bold )
            }
        }}
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