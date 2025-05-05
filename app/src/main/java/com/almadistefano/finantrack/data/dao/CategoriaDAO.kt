package com.almadistefano.finantrack.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.almadistefano.finantrack.model.Categoria
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias")
    fun getAll(): Flow<List<Categoria>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categorias: List<Categoria>)
}
