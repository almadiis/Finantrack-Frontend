package com.almadistefano.finantrack.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Entity(tableName = "categorias")
@Parcelize
data class Categoria(
    @PrimaryKey val id: Int,
    val nombre: String,
    val tipo: String
) : Serializable, Parcelable
