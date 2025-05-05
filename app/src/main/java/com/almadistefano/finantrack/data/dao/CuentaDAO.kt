package com.almadistefano.finantrack.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.almadistefano.finantrack.model.Cuenta
import kotlinx.coroutines.flow.Flow


@Dao
interface CuentaDao {

    @Query("SELECT * FROM cuentas")
    fun getAllCuentas(): Flow<List<Cuenta>>

    @Query("SELECT * FROM cuentas WHERE id = :id")
    suspend fun getCuentaByUser(id: Int): Cuenta?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCuenta(cuenta: List<Cuenta>)

}
