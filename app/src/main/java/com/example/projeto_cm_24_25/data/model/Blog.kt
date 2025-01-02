package com.example.projeto_cm_24_25.data.model

import android.net.Uri

data class Blog(
    val author: String = "",
    val title: String = "",
    val content: String = "",
    val publishedDate: String = "",
    val imageUri: Int = 0
)