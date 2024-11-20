package com.example.apprecetas

data class Receta(
    val nombre: String,
    val ingredientes: String,
    val pasos: String,
    var imagenUri: String? = null
)