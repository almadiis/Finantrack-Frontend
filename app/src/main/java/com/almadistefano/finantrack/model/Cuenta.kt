package com.almadistefano.finantrack.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Entity(tableName = "cuentas")
@Parcelize
data class Cuenta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val saldo: Double,
    val tipo: String
) : Serializable, Parcelable
