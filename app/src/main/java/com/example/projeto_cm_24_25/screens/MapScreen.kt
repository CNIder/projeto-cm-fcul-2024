package com.example.projeto_cm_24_25.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.projeto_cm_24_25.R
import com.example.projeto_cm_24_25.data.AccerelometerViewModel
import com.example.projeto_cm_24_25.data.MapViewModel
import com.example.projeto_cm_24_25.data.model.AlertData
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
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.ui.geometry.Offset
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun resizeIcon(resourceId: Int, context: Context, width: Int, height: Int): BitmapDescriptor {
    val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
    return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
}

@Composable
fun rememberDeviceOrientation(): Float {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(SensorManager::class.java)

    val azimuth = remember { mutableStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorEventListener = object : SensorEventListener {
            val accelerometerValues = FloatArray(3)
            val magnetometerValues = FloatArray(3)

            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    when (event.sensor.type) {
                        Sensor.TYPE_ACCELEROMETER -> {
                            System.arraycopy(event.values, 0, accelerometerValues, 0, accelerometerValues.size)
                        }
                        Sensor.TYPE_MAGNETIC_FIELD -> {
                            System.arraycopy(event.values, 0, magnetometerValues, 0, magnetometerValues.size)
                        }
                    }

                    val rotationMatrix = FloatArray(9)
                    val orientationAngles = FloatArray(3)

                    if (SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magnetometerValues)) {
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)
                        azimuth.value = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    return azimuth.value
}

private lateinit var fusedLocationClient: FusedLocationProviderClient


@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    mapViewModel: MapViewModel,
    navController: NavHostController
) {
    val markerList = mapViewModel.markerData.observeAsState(emptyList())
    val userAlertList = mapViewModel.userAlerts.observeAsState(emptyList())
    val location = LocalContext.current
    var currentLocation = rememberMarkerState(position = LatLng(0.0,0.0))
    val azimuth = rememberDeviceOrientation()
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
    val showUserNotification = mapViewModel.showAlertNotification.observeAsState(true)
    val insertUserPosition = remember { mutableStateOf(true) }

    val dataStore = DataStoreRepository(location)
    val userName = dataStore.getUserName.collectAsState(initial = "").value

    LaunchedEffect(true) {
        // Permissao para aceder a localizacao atual
        locationPermissionState.launchMultiplePermissionRequest()
        // inicializar a busca dos dados dos markers
        mapViewModel.getMarkers()
        mapViewModel.getAlerts()
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
                    //Log.d("ACT", location.toString())
                    currentLocation.position = LatLng(location.latitude, location.longitude)
                    // Guardar a posicao no firebase
                    if (userName != null) {
                        if(userName.isNotEmpty()) {
                            // Zoom para localizacao atual
                            if (insertUserPosition.value) {
                                cameraPositionState.position =
                                    CameraPosition.fromLatLngZoom(currentLocation.position, 10f)
                                insertUserPosition.value = false

                                mapViewModel.addMarker(
                                    ItemMarker(
                                        name = userName,
                                        type = "User",
                                        icon = R.drawable.user_zone_icon2.toString(),
                                        latitude = location.latitude,
                                        longitude = location.longitude
                                    )
                                )
                            }
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
        if(sensorViewModel.acceleration.value > 30f) {
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
                            // Utilizador nao esta seguro
                            if(!userName.isNullOrEmpty()) {
                                mapViewModel.addUserAlert(
                                    AlertData(
                                        username = userName,
                                        safe = "false"
                                    )
                                )
                            }
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
                    title = "Você está aqui",
                    icon = resizeIcon(R.drawable.user_location, location, 64, 64), // Use um ícone com seta para direção
                    rotation = (azimuth + 180) % 360, // Inverte o sentido
                    anchor = Offset(0.5f, 0.5f)
                )

                userAlertList.value.forEach {
                    // se utilizador nao estiver seguro emite notificacao
                    if(it.safe.equals("false")) {
                        if(showUserNotification.value) {
                            notificationService.showNotification(
                                "⚠\uFE0F Alert ⚠\uFE0F",
                                "${it.username} is in danger. Help!"
                            )
                            mapViewModel.disableUserNotification()
                        }
                    }
                }

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
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(it.position, 17f)
                            false
                        }
                    ){
                        when(it.type) {
                            "Safe Zone" -> {
                                Image(
                                    painter = painterResource(R.drawable.safe_zone_icon2),
                                    contentDescription = "image on marker",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            "Infected Zone" -> {
                                Image(
                                    painter = painterResource(R.drawable.infected_zone_icon2),
                                    contentDescription = "image on marker",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            "Supply Zone" -> {
                                Image(
                                    painter = painterResource(R.drawable.supply_zone_icon2),
                                    contentDescription = "image on marker",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            else -> {
                                Image(
                                    painter = painterResource(R.drawable.user_zone_icon2),
                                    contentDescription = "image on marker",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                }
            }

            ExtendedFloatingActionButton(
                onClick = { },
                modifier = Modifier.size(100.dp).align(Alignment.TopEnd).padding(20.dp) ,
                containerColor = Color(238, 31, 39)

            ) {
                Text(text = userName?.firstOrNull()?.uppercase()?.toString() ?: "?",
                    fontSize = 20.sp)
            }

            ExtendedFloatingActionButton(
                onClick = {
                    // Navegar para o ecra do formulario do mapa
                    navController.navigate(Screen.MapForm.route)
                },
                containerColor = Color(238, 31, 39, 200),
                contentColor = Color.Black,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .width(250.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Image(
                        painter = painterResource(R.drawable.bio_hazzard),
                        contentDescription = "Left image",
                        modifier = Modifier.size(20.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Floating button icon")
                        Text(
                            text = "Report Place",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Image(
                        painter = painterResource(R.drawable.bio_hazzard),
                        contentDescription = "Right image",
                        modifier = Modifier.size(20.dp)
                    )
                }
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