package com.example.projeto_cm_24_25.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
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
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            acceleration.value = magnitude

            if (magnitude > 12f) {
                // Dispara a notificação
                Log.d("Acelerometro", "Aceleração detectada: $magnitude m/s² - Notificação disparada devido a aceleração alta!")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}