package com.example.projeto_cm_24_25.data.repository

import android.util.Log
import com.example.projeto_cm_24_25.data.model.AlertData
import com.example.projeto_cm_24_25.data.model.ItemMarker
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class AlertRepository {
    private val database = Firebase.database
    private val alertsRef = database.getReference("alert")

    fun addUserAlert(alertData: AlertData) {
        alertsRef.child(alertData.username)
            .setValue(alertData)
            .addOnSuccessListener {  }
            .addOnFailureListener {  }
    }

    suspend fun fetchAlerts(): List<AlertData> {
        delay(4000)
        return try {
            val snapshot = alertsRef.get().await()
            val alertList = mutableListOf<AlertData>()
            snapshot.children.forEach {
                val alertData = it.getValue(AlertData::class.java)
                if (alertData != null) {
                    alertList.add(alertData)
                }
            }
            alertList
        } catch (e: Exception) {
            emptyList()
        }
    }
}