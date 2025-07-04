package com.almadistefano.finantrack.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.almadistefano.finantrack.model.Cuenta
import kotlinx.coroutines.flow.Flow


@Dao
interface CuentaDao {

    @Query("SELECT * FROM cuentas WHERE usuario_id = :id")
    fun getAllCuentas(id: Int): Flow<List<Cuenta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCuenta(cuenta: List<Cuenta>)

    @Query("SELECT * FROM cuentas WHERE usuario_id = :usuarioId LIMIT 1")
    suspend fun getPrimeraCuentaPorUsuario(usuarioId: Int): Cuenta?
    @Query("DELETE FROM cuentas")
    suspend fun deleteAll()
}
