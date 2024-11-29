package com.example.projeto_cm_24_25.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.example.projeto_cm_24_25.data.MapViewModel
import com.example.projeto_cm_24_25.data.model.ItemMarker
import com.example.projeto_cm_24_25.navigation.Screen
import com.example.projeto_cm_24_25.ui.theme.primaryColor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapForm(navController: NavHostController, mapViewModel: MapViewModel,) {
    // list that contains types of places
    val placeTypes = listOf("Safe Zone", "Hiding Zone", "Supply Zone", "Infected Zone")

    // variables for drop down
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    var textfieldSize by remember { mutableStateOf(Size.Zero)}
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.ArrowDropDown

    // initial position of marker
    val initialPosition = LatLng(38.7223, -9.1393)

    // remember the camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 6f)
    }
    var uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
    }
    // state for the draggable marker position
    var markerPosition by remember { mutableStateOf(initialPosition) }

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
                    // navigate to map screen
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "go back to map screen"
                    )
                }
            }
        )
        // place report form
        TextField(
            value = textInput,
            onValueChange = { newValue -> textInput = newValue},
            label = { Text("Type your name") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                focusedPlaceholderColor = Color.Black,
            )
        )
        // drop down box
        Box {
            OutlinedTextField(
                value = selectedText,
                onValueChange = { selectedText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        //This value is used to assign to the DropDown the same width
                        textfieldSize = coordinates.size.toSize()
                    },
                label = {Text("Label")},
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

        // google maps for location choosing
        GoogleMap(
            modifier = Modifier.fillMaxHeight(0.75f),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings
        ){
            Marker(
                state = rememberMarkerState(position = markerPosition),
                title = "Drag me",
                draggable = true,
                onClick = {
                    markerPosition = it.position
                    Log.d("MAP FORM", it.position.toString())
                    false
                }
            )
        }

        // submit button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // get field data
                Log.d("MAP FORM", textInput)
                Log.d("MAP FORM", selectedText)
                Log.d("MAP FORM", markerPosition.toString())
                val itemMarker = ItemMarker(textInput, selectedText, markerPosition.latitude, markerPosition.longitude)
                mapViewModel.addMarker(itemMarker)
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