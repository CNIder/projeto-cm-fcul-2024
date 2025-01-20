package com.example.projeto_cm_24_25.utils

data class ImgBBResponse(
    val data: ImgBBData,
    val success: Boolean,
    val status: Int
)

data class ImgBBData(
    val id: String,
    val url: String,
    val delete_url: String
)
