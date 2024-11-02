package com.example.imc.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var nome: String,
    var idade: Long,
    var altura: Double,
    var peso: Double,
    var sexo: String,
    var imc: Double = 0.0,
    var classificacao: String = ""
)
