package com.almadistefano.finantrack.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.almadistefano.finantrack.model.Usuario
import kotlinx.coroutines.flow.Flow


@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios LIMIT 1")
    fun getUsuarioActual(): Flow<Usuario?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: Usuario)

    @Query("DELETE FROM usuarios")
    suspend fun borrarUsuarios() // útil al cerrar sesión
}
