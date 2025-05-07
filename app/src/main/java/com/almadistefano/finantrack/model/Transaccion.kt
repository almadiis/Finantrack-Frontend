package com.almadistefano.finantrack.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Entity(tableName = "transacciones")
@Parcelize
data class Transaccion(
    @PrimaryKey val id: Int,
    val monto: Double,
    val fecha: String,
    val descripcion: String,
    val tipo: String,
    val cuentaNombre: String,
    val categoriaNombre: String,
    val usuarioId: Int // 🔑

) : Serializable, Parcelable
