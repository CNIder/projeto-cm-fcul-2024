package com.example.projeto_cm_24_25.data.repository

import com.example.projeto_cm_24_25.data.model.ItemMarker
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class MapRepository {
    private val database = Firebase.database
    private val itemsRef = database.getReference("map")

    suspend fun fetchMapData() : List<ItemMarker> {
        delay(3000)
        return try {
            val snapshot = itemsRef.get().await()
            val itemList = mutableListOf<ItemMarker>()
            snapshot.children.forEach {
                val mapItem = it.getValue(ItemMarker::class.java)
                if (mapItem != null) {
                    itemList.add(mapItem)
                }
            }
            itemList
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addMapData(marker: ItemMarker) {
        itemsRef.child(marker.name).setValue(marker).addOnSuccessListener {
            //Log.d("FIREBASE", "Created")
        }.addOnFailureListener { error ->
            //Log.d("FIREBASE", "Error $error")
        }
    }
}