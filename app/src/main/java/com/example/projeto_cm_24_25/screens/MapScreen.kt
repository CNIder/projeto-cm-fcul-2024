package com.example.projeto_cm_24_25.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.projeto_cm_24_25.data.MapViewModel
import com.example.projeto_cm_24_25.data.model.ItemMarker
import com.example.projeto_cm_24_25.ui.theme.primaryColor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private lateinit var fusedLocationClient: FusedLocationProviderClient
private lateinit var locationCallback: LocationCallback

@SuppressLint("MissingPermission")
private fun startLocationUpdates() {
    locationCallback?.let {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 100
        ).setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(25000)
            .setMaxUpdateDelayMillis(100)
            .build()

        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            it,
            Looper.getMainLooper()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(modifier: Modifier = Modifier, mapViewModel: MapViewModel) {
    val markerList = mapViewModel.markerData.observeAsState(emptyList())
    val location = LocalContext.current
    var currentLocation = rememberMarkerState(position = LatLng(0.0,0.0))
    val cameraPositionState = rememberCameraPositionState {}
    locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            for (location in p0.locations) {
                Log.d("ACT", location.toString())
            }
        }
    }
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(location)
    LaunchedEffect(true) {
        mapViewModel.getMarkers()
    }
    if (ActivityCompat.checkSelfPermission(
            location,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            location,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {

    }
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                currentLocation.position = LatLng(location.latitude, location.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation.position, 14f)
            }
            Log.d("Teste", "Location:" + location.toString())
            startLocationUpdates()
        }
    /*val properties by remember {
        mutableStateOf(MapProperties(mapStyleOptions = MapStyleOptions(mapStyle)))
    }*/
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TopAppBar(
            title = {
                Text(
                    text = "Hello !",
                    color = Color.White
                )
                    },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = primaryColor
            ),
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {}
        ) {
            Text("Criar Ponto")
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = true),
            //properties = properties
        ) {
            // add marker for current user position
            Marker(
                state = currentLocation
            )
            markerList.value.forEach {
                val distance : Double = haversineDistance(
                    currentLocation.position.latitude,
                    currentLocation.position.longitude,
                    it.latitude,
                    it.longitude
                )
                println("Distance is $distance")
                Marker(
                    state = MarkerState(position = LatLng(it.latitude, it.longitude))
                )
            }
        }
    }
}

fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    // Radius of the Earth in kilometers
    val R = 6371.0

    // Convert degrees to radians
    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)

    // Difference in coordinates
    val deltaLat = lat2Rad - lat1Rad
    val deltaLon = lon2Rad - lon1Rad

    // Haversine formula
    val a = sin(deltaLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    // Distance in kilometers
    val distance = R * c
    return distance
}