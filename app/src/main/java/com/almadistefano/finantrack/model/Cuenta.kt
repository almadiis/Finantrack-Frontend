package com.almadistefano.finantrack.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Entity(tableName = "cuentas")
@Parcelize
data class Cuenta(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val nombre: String,
    val saldo: Double,
    val tipo: String,
    @ColumnInfo(name = "usuario_id")
    val usuarioId: Int
) : Serializable, Parcelable
