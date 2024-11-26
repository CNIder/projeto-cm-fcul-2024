package com.example.projeto_cm_24_25.data.repository

import android.util.Log
import com.example.projeto_cm_24_25.data.model.Blog
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class BlogRepository {
    val database = Firebase.database
    val blogsRef = database.getReference("blog")

    suspend fun fetchBlogData() : List<Blog> {
        delay(3000)
        return try {
            val snapshot = blogsRef.get().await()
            val blogList = mutableListOf<Blog>()
            snapshot.children.forEach {
                val blog = it.getValue(Blog::class.java)
                if (blog != null) {
                 blogList.add(blog)
                }
            }
            blogList
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addBlogData(blog: Blog) {
        blogsRef.child(blog.title).setValue(blog).addOnSuccessListener {
            Log.d("FIREBASE", "Created")
        }.addOnFailureListener { error ->
            Log.d("FIREBASE", "Error $error")
        }
    }
}