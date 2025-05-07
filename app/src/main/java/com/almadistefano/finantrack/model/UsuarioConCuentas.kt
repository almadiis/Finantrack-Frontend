package com.almadistefano.finantrack.model

import androidx.room.Embedded
import androidx.room.Relation

data class UsuarioConCuentas(
    @Embedded val usuario: Usuario,

    @Relation(
        parentColumn = "id",
        entityColumn = "usuario_id"
    )
    val cuentas: List<Cuenta>
)
