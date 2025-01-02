package com.example.projeto_cm_24_25.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AccerelometerViewModel(context: Context): ViewModel(), SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    var acceleration = mutableStateOf(0f)

    fun startListening() {
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            acceleration.value = it.values[0] // X-axis acceleration
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}