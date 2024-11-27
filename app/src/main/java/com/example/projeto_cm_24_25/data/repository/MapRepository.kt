package com.example.projeto_cm_24_25.data.repository

import android.util.Log
import com.example.projeto_cm_24_25.data.model.Blog
import com.example.projeto_cm_24_25.data.model.ItemMarker
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class MapRepository {
    val database = Firebase.database
    val itemsRef = database.getReference("map")

    suspend fun fetchBlogData() : List<ItemMarker> {
        delay(3000)
        return try {
            val snapshot = itemsRef.get().await()
            val itemList = mutableListOf<ItemMarker>()
            snapshot.children.forEach {
                val blog = it.getValue(ItemMarker::class.java)
                if (blog != null) {
                    itemList.add(blog)
                }
            }
            itemList
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addBlogData(marker: ItemMarker) {
        itemsRef.child("exemplo").setValue(marker).addOnSuccessListener {
            Log.d("FIREBASE", "Created")
        }.addOnFailureListener { error ->
            Log.d("FIREBASE", "Error $error")
        }
    }
}