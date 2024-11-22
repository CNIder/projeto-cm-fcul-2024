package com.example.projeto_cm_24_25.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(navController: NavHostController) {
    Column {
        val atasehir = LatLng(38.717391774845176, -9.1411938)
        val atasehir2 = LatLng(40.20310934822949, -8.428699742033594)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(atasehir, 15f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = atasehir),
                title = "One Marker"
            )
            Marker(
                state = MarkerState(position = atasehir2),
                title = "One Marker"
            )
        }
    }
}