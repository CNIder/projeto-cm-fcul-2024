package com.example.projeto_cm_24_25.utils

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.projeto_cm_24_25.R
import kotlin.random.Random

class NotificationService(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(context, "SurvivorApp")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.infected_zone_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(Random.nextInt(), notification)
    }
}