package com.example.projeto_cm_24_25.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.core.text.isDigitsOnly
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.projeto_cm_24_25.screens.getMarkerIcon

import com.example.projeto_cm_24_25.utils.getDarkMapStyle
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerInfoWindowContent

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

    LaunchedEffect(Unit) {
        // Verificar se as permissões já estão concedidas
        if (locationPermissionState.allPermissionsGranted) {
            // Inicializar a busca dos dados dos markers e alertas
            mapViewModel.getMarkers()
            mapViewModel.getAlerts()
        } else {
            // Pedir permissões se ainda não estiverem concedidas
            locationPermissionState.launchMultiplePermissionRequest()
        }
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
                    if (currentLocation.position != LatLng(location.latitude, location.longitude)) {
                        currentLocation.position = LatLng(location.latitude, location.longitude)
                    }
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
                                        icon = "",
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
        mutableStateOf(MapProperties(mapStyleOptions = getDarkMapStyle()))
    }

    // Definir animacoes nas cores
    val transition = rememberInfiniteTransition(label = "infinite")

    val colorInfected by transition.animateColor(
        initialValue = Color(0xB9C01616),
        targetValue = Color(0x9FF6F2F2),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
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

        Box {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true),
                properties = properties
            ) {
                markerList.value.filter { it.name != userName }.forEach { marker ->
                    val distance = haversineDistance(
                        currentLocation.position.latitude,
                        currentLocation.position.longitude,
                        marker.latitude,
                        marker.longitude
                    )

                    customMarker(marker)
                }

                // Marcador para a posição atual
                Marker(
                    state = currentLocation,
                    title = "Você está aqui",
                    icon = resizeIcon(
                        R.drawable.user_location,
                        location,
                        90,
                        90
                    ),
                    rotation = (azimuth + 180) % 360, // Inverte o sentido
                    anchor = Offset(0.5f, 0.5f)
                )

                // Notificações para utilizadores em perigo
                userAlertList.value.forEach { alert ->
                    if (alert.safe == "false" && showUserNotification.value) {
                        notificationService.showNotification(
                            "⚠\uFE0F Alert ⚠\uFE0F",
                            "${alert.username} is in danger. Help!"
                        )
                        mapViewModel.disableUserNotification()
                    }
                }
            }

            // Botão flutuante superior (perfil do usuário)
            ExtendedFloatingActionButton(
                onClick = { },
                modifier = Modifier.size(100.dp).align(Alignment.TopEnd).padding(20.dp),
                containerColor = Color(238, 31, 39)
            ) {
                Text(
                    text = userName?.firstOrNull()?.uppercase()?.toString() ?: "?",
                    fontSize = 20.sp
                )
            }

            // Botão flutuante inferior para reportar lugar
            ExtendedFloatingActionButton(
                onClick = {
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

@Composable
fun customMarker(marker: ItemMarker) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(marker.icon)
            .allowHardware(false)
            .build()
    )
    var expandMarker = remember {
        mutableStateOf(false)
    }

    if(expandMarker.value) {
        Dialog(
            onDismissRequest = {expandMarker.value = false},
            content = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Box(
                        modifier = Modifier
                            .background(Color(25,25,25,180), shape = RoundedCornerShape(16.dp))  // Cor de fundo e cantos arredondados
                            .padding(16.dp)

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Coluna com os textos alinhados à esquerda
                            Column(
                             ) {
                                Text(text = "${marker.type}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(238, 31, 39))
                                Text(text = "${marker.name}", fontSize = 15.sp)
                                Text(text = "Position:" , fontWeight = FontWeight.Bold, fontSize = 15.sp   )
                                Text(text = "Lat: ${convertToDMS(marker.latitude)}", fontSize = 12.sp)  // Converter latitude
                                Text(text = "Lng: ${convertToDMS(marker.longitude)}", fontSize = 12.sp)  // Longitude norma
                            }

                            // Exibir imagem à direita se a condição for atendida
                            if (marker.type != "User" && marker.icon.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(marker.icon),
                                    contentDescription = "Marker Image",
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clip(RoundedCornerShape(12.dp))  // Imagem com cantos arredondados
                                )
                            }
                        }
                    }

                }
            }
        )
    }

    MarkerComposable(
        keys = arrayOf(marker, painter.state, expandMarker),
        state = MarkerState(position = LatLng(marker.latitude, marker.longitude)),
        title = marker.name,
        snippet = marker.type,
        anchor = Offset(0.5f, 1f),
        onClick = {
            expandMarker.value = true
            false
        }
    ) {
        Image(
            painter = painterResource(getMarkerIcon(marker.type)),
            contentDescription = "",
            modifier = Modifier.size(50.dp, 50.dp)
        )
    }
    }

fun getMarkerIcon(type: String): Int {
    return when (type) {
        "Safe Zone" -> R.drawable.safe_zone_icon2
        "Infected Zone" -> R.drawable.infected_zone_icon2
        "Supply Zone" -> R.drawable.supply_zone_icon2
        else -> R.drawable.user_zone_icon2
    }
}

fun convertToDMS(coordinate: Double): String {
    val degrees = coordinate.toInt()
    val minutesDecimal = (coordinate - degrees) * 60
    val minutes = minutesDecimal.toInt()
    val secondsDecimal = (minutesDecimal - minutes) * 60
    return String.format("%dº %d' %.1f''", degrees, minutes, secondsDecimal)
}
