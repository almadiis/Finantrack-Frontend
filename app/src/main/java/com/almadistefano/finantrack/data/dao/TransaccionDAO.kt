package com.almadistefano.finantrack.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.almadistefano.finantrack.model.Transaccion
import com.almadistefano.finantrack.model.TransaccionConCuentaYCategoria
import com.almadistefano.finantrack.model.UsuarioConCuentas
import kotlinx.coroutines.flow.Flow
@Dao
interface TransaccionDao {
    @Query("SELECT * FROM transacciones WHERE cuenta_id = :cuentaId")
    fun getAll(cuentaId: Int): Flow<List<Transaccion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transacciones: List<Transaccion>)

    @Transaction
    @Query("SELECT * FROM transacciones WHERE cuenta_id = :cuentaId")
    fun getTransaccionesByCuenta(cuentaId: Int): Flow<List<Transaccion>>

    // Obtener las cuentas de un usuario
    @Transaction
    @Query("SELECT * FROM transacciones WHERE cuenta_id = :cuentaId")
    suspend fun obtenerTransaccionesDeLaCuenta(cuentaId: Int): List<TransaccionConCuentaYCategoria>


    // Eliminar todas las transacciones de una cuenta
    @Query("DELETE FROM transacciones WHERE cuenta_id = :cuentaId")
    suspend fun borrarTransaccionesPorCuenta(cuentaId: Int)

    // Eliminar todas las transacciones (opcional, por ejemplo al cerrar sesión)
    @Query("DELETE FROM transacciones")
    suspend fun borrarTodasLasTransacciones()

    @Query("DELETE FROM transacciones")
    suspend fun deleteAll()
}
