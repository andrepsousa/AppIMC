package com.example.imc

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.imc.model.Usuario

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario)

    @Query("SELECT * FROM usuarios")
    suspend fun getAllUsuarios(): List<Usuario>

    @Query("DELETE FROM usuarios WHERE id = :usuarioId")
    suspend fun deleteUsuario(usuarioId: Long)
}
