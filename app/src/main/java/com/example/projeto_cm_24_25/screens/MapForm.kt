package com.example.projeto_cm_24_25.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.example.projeto_cm_24_25.data.MapViewModel
import com.example.projeto_cm_24_25.data.model.ItemMarker
import com.example.projeto_cm_24_25.navigation.Screen
import com.example.projeto_cm_24_25.ui.theme.primaryColor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.DragState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
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

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        CenterAlignedTopAppBar(
            title = { Text("Report Place") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = primaryColor
            ),
            navigationIcon = {
                IconButton(onClick = {
                    // Voltar para o ecra principal
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "go back to map screen"
                    )
                }
            }
        )

        TextField(
            value = textInput,
            onValueChange = { newValue -> textInput = newValue},
            label = {
                Text(
                    "Zone name",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.White
                    )
                )
            },
            placeholder = {
                Text("e.g, FCUL \uD83C\uDFEB safe place")
            },
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
                    .onGloballyPositioned { coordinates ->
                        textfieldSize = coordinates.size.toSize()
                    },
                label = {Text("Zone type")},
                trailingIcon = {
                    Icon(icon,"contentDescription",
                        Modifier.clickable { expanded = !expanded })
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current){textfieldSize.width.toDp()})
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
            modifier = Modifier.fillMaxHeight(0.75f).padding(14.dp).border(width = 2.dp, color = Color.Red),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            onMapClick = {
                markerState.position = LatLng(it.latitude, it.longitude)
                markerPosition = markerState.position
            }
        ){
            markerState?.let {
                Marker(
                    state = it,
                )
            }
        }


        // Botao para submeter formulario
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Validar campos
                if(textInput.isEmpty()) {
                    Toast.makeText(context, "Please provide a location name", Toast.LENGTH_SHORT).show()
                    return@Button
                } else if (selectedText.isEmpty()) {
                    Toast.makeText(context, "Please provide the location type", Toast.LENGTH_SHORT).show()
                    return@Button
                } else if (markerPosition == null) {
                    Toast.makeText(context, "Click on the map to choose location", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val itemMarker = ItemMarker(
                    textInput,
                    selectedText,
                    markerPosition!!.latitude,
                    markerPosition!!.longitude
                )
                //mapViewModel.addMarker(itemMarker)
                Toast.makeText(context, "Adicionado com Sucesso !\uD83E\uDD17", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )
        ) {
            Text("Report Place")
        }
    }
}