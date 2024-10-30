package com.example.imc.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val idade: Int,
    val altura: Double,
    val peso: Double,
    val sexo: String
)
