package com.almadistefano.finantrack.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.Presupuesto
import com.almadistefano.finantrack.model.PresupuestoConCategoria
import kotlinx.coroutines.flow.Flow


@Dao
interface PresupuestoDao {
    @Query("SELECT * FROM presupuestos")
    fun getAll(): Flow<List<Presupuesto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(presupuestos: List<Presupuesto>)

    @Transaction
    @Query("SELECT * FROM presupuestos")
    fun obtenerPresupuestosConCategoria(): Flow<List<PresupuestoConCategoria>>

}
