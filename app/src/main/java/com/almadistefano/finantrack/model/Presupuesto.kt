package com.almadistefano.finantrack.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.almadistefano.finantrack.utils.DateConverter
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.Date

@Entity(tableName = "presupuestos")
@Parcelize
@TypeConverters(DateConverter::class)
data class Presupuesto(
    @PrimaryKey val id: Int,
    val montoMaximo: Double,
    val periodo: String,
    val fechaInicio: Date,
    val fechaFin: Date,
    val categoriaId: Int,
    val usuarioId: Int // 🔑

) : Serializable, Parcelable
