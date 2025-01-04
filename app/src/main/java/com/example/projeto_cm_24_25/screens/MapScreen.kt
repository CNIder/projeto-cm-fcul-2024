package com.example.projeto_cm_24_25.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.projeto_cm_24_25.R
import com.example.projeto_cm_24_25.data.AccerelometerViewModel
import com.example.projeto_cm_24_25.data.MapViewModel
import com.example.projeto_cm_24_25.data.model.ItemMarker
import com.example.projeto_cm_24_25.data.repository.DataStoreRepository
import com.example.projeto_cm_24_25.navigation.Screen
import com.example.projeto_cm_24_25.ui.theme.primaryColor
import com.example.projeto_cm_24_25.utils.NotificationService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlin.math.atan2
import kotlin.math.ceil
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

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    mapViewModel: MapViewModel,
    navController: NavHostController
) {
    val markerList = mapViewModel.markerData.observeAsState(emptyList())
    val location = LocalContext.current
    var currentLocation = rememberMarkerState(position = LatLng(0.0,0.0))
    val cameraPositionState = rememberCameraPositionState {}
    val notificationService = NotificationService(location)

    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )
    )
    val sensorViewModel = remember { AccerelometerViewModel(location) }
    val showDialog = remember { mutableStateOf(false) }
    val showAlert = remember { mutableStateOf(true) }
    val insertUserPosition = remember { mutableStateOf(true) }

    val dataStore = DataStoreRepository(location)
    val userName = dataStore.getUserName.collectAsState(initial = "").value

    LaunchedEffect(true) {
        // Permissao para aceder a localizacao atual
        locationPermissionState.launchMultiplePermissionRequest()
        mapViewModel.getMarkers()
    }


    DisposableEffect(Unit){
        sensorViewModel.startListening()
        onDispose {
            sensorViewModel.stopListening()
        }
    }

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(location)

    locationPermissionState.permissions.forEach { permissionState ->
        if (permissionState.status.isGranted) {
            if (ActivityCompat.checkSelfPermission(
                    location,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    location,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                location?.let {
                    Log.d("ACT", location.toString())
                    currentLocation.position = LatLng(location.latitude, location.longitude)
                    // Guardar a posicao no firebase
                    if (userName != null) {
                        if(userName.isNotEmpty()) {
                            // Zoom para localizacao atual
                            if (insertUserPosition.value) {
                                cameraPositionState.position =
                                    CameraPosition.fromLatLngZoom(currentLocation.position, 10f)
                                insertUserPosition.value = false
                            }

                            mapViewModel.addMarker(
                                ItemMarker(
                                    name = userName,
                                    type = "User",
                                    icon = R.drawable.user_zone_icon.toString(),
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                            )
                        }
                    }
                }
            }.addOnFailureListener {}
        }
    }

    val properties by remember {
        mutableStateOf(MapProperties(mapStyleOptions = MapStyleOptions("""
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
        """.trimIndent())))
    }

    // Definir animacoes nas cores
    val transition = rememberInfiniteTransition(label = "infinite")

    val colorInfected by transition.animateColor(
        initialValue = Color(0xB9C01616),
        targetValue = Color(0x9FF6F2F2),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )

    val colorSafe by transition.animateColor(
        initialValue = Color(0xB91CC016),
        targetValue = Color(0x9FF6F2F2),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        if(sensorViewModel.acceleration.value > 6f) {
            showDialog.value = true
        }

        // Se o valor x do accerolometro for elevado,
        // foi detetado movimento brusco com o telemovel
        if(showDialog.value) {
            AlertDialog(
                onDismissRequest = {showDialog.value = false},
                title = { Text("\uD83D\uDEA8 Alert \uD83D\uDEA8") },
                text = { Text("Were you in danger ?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            Toast.makeText(location, "Go to a safe location", Toast.LENGTH_LONG).show()
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {showDialog.value = false}
                    ) {
                        Text("No")
                    }
                }
            )
        }

        TopAppBar(
            title = {
                Text(
                    text = "Howdy $userName \uD83D\uDE0B",
                    color = Color.White
                )
                    },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = primaryColor
            ),
        )
        Box{
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true),
                properties = properties
            ) {

                // Marker para posicao atual
                Marker(
                    state = currentLocation,
                    title = "You are here \uD83D\uDE01"
                )
                // Mostra os markers com os pontos registados e a localizacao dos utilizadores
                markerList.value.filter { it.name != userName }.forEach {
                    val distance : Double = haversineDistance(
                        currentLocation.position.latitude,
                        currentLocation.position.longitude,
                        it.latitude,
                        it.longitude
                    )

                    // Alerta utilizador para proximidade de zona perigosa
                    if (distance < 5 && it.type == "Infected Zone") {
                        if(showAlert.value) {
                            notificationService.showNotification(
                                "⚠\uFE0F Alert ⚠\uFE0F",
                                "You are near a zombie area. Go away \uD83D\uDE0E"
                            )
                            showAlert.value = !showAlert.value
                        }
                    }

                    // Se a area for infetada mostra o circulo
                    if(it.type == "Infected Zone") {
                        Circle(
                            center = LatLng(it.latitude, it.longitude),
                            radius = 70.0,
                            strokeColor = primaryColor,
                            fillColor = colorInfected,
                            strokeWidth = 2f
                        )
                    } else {
                        Circle(
                            center = LatLng(it.latitude, it.longitude),
                            radius = 50.0,
                            strokeColor = primaryColor,
                            fillColor = colorSafe,
                            strokeWidth = 2f
                        )
                    }
                    MarkerComposable(
                        state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                        title = it.name,
                        snippet = "${it.type} at ${ceil(distance)} km",
                        onClick = {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(it.position, 18f)
                            false
                        }
                    ){
                        Image(
                            painter = painterResource(it.icon.toInt()),
                            contentDescription = "image on marker"
                        )
                    }
                }
            }

            ExtendedFloatingActionButton(
                onClick = {
                    // Navegar para o ecra do formulario do mapa
                    navController.navigate(Screen.MapForm.route)
                },
                containerColor = primaryColor,
                contentColor = Color.White,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
            ) {
                Icon(Icons.Filled.Add, "Floating button")
                Text(text = "Report \uD83C\uDF0D")
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