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
    @Query("SELECT * FROM categorias WHERE usuarioId = :usuarioId OR usuarioId IS NULL")
    fun getCategoriasFiltradas(usuarioId: Int): Flow<List<Categoria>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categorias: List<Categoria>)

    @Query("DELETE FROM categorias WHERE id = :categoriaId")
    suspend fun eliminarCategorias(categoriaId: Int)

    @Query("DELETE FROM categorias")
    suspend fun deleteAll()
}
