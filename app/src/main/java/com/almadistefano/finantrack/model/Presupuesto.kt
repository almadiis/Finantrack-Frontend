package com.almadistefano.finantrack.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.Date


@Parcelize
@Entity(tableName = "presupuestos")
@TypeConverters(DateConverter::class)
data class Presupuesto(
    @PrimaryKey val id: Int,
    val montoMaximo: Double,
    val periodo: String,
    val fechaInicio: String,
    val fechaFin: String,

    @SerializedName("id_categoria")
    @ColumnInfo(name = "categoria_id")
    val categoriaId: Int,

    @SerializedName("id_usuario")
    @ColumnInfo(name = "usuario_id")
    val usuarioId: Int
) : Serializable, Parcelable
