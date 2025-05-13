package com.almadistefano.finantrack.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import com.google.gson.annotations.SerializedName

@Entity(tableName = "transacciones")
@Parcelize
data class Transaccion(
    @PrimaryKey val id: Int,
    val monto: Double,
    val fecha: String,
    val descripcion: String,
    val tipo: String,

    @SerializedName("id_categoria")
    @ColumnInfo(name = "categoria_id")
    val categoriaId: Int,

    @SerializedName("id_cuenta")
    @ColumnInfo(name = "cuenta_id")
    val cuentaId: Int
) : Serializable, Parcelable
