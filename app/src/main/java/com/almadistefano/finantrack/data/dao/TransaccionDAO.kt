package com.almadistefano.finantrack.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.almadistefano.finantrack.model.Transaccion
import kotlinx.coroutines.flow.Flow
@Dao
interface TransaccionDao {
    @Query("SELECT * FROM transacciones WHERE usuarioId = :usuarioId")
    fun getAll(usuarioId: Int): Flow<List<Transaccion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transacciones: List<Transaccion>)

    @Query("SELECT * FROM transacciones WHERE usuarioId = :usuarioId")
    fun getTransaccionesByUsuario(usuarioId: Int): Flow<List<Transaccion>>

}
