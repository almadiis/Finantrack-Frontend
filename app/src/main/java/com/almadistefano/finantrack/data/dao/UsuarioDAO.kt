package com.almadistefano.finantrack.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.almadistefano.finantrack.model.Usuario
import com.almadistefano.finantrack.model.UsuarioConCuentas
import kotlinx.coroutines.flow.Flow


@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios LIMIT 1")
    fun getUsuarioActual(): Flow<Usuario?>

    @Query("SELECT * FROM usuarios WHERE id = :usuarioId LIMIT 1")
    fun getUsuarioById(usuarioId: Int): Flow<Usuario?>


    @Query("SELECT * FROM usuarios WHERE nombre = :username")
    suspend fun getUsuarioByUserName(username: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: Usuario)

    // Verificar si un usuario existe (para login)
    @Query("SELECT * FROM usuarios WHERE nombre = :username AND password = :password LIMIT 1")
    suspend fun obtenerUsuarioPorCredenciales(username: String, password: String): Usuario?

    // Obtener las cuentas de un usuario
    @Transaction
    @Query("SELECT * FROM usuarios WHERE id = :usuarioId")
    suspend fun obtenerCuentasDelUsuario(usuarioId: Int): UsuarioConCuentas

    @Query("DELETE FROM usuarios")
    suspend fun borrarUsuarios() // útil al cerrar sesión

    @Query("DELETE FROM usuarios")
    suspend fun deleteAll()
}
