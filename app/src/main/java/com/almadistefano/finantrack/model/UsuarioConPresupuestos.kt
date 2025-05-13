package com.almadistefano.finantrack.model

import androidx.room.Embedded
import androidx.room.Relation

data class UsuarioConPresupuestos(
    @Embedded val usuario: Usuario,

    @Relation(
        parentColumn = "id",
        entityColumn = "id_usuario"
    )
    val presupuestos: List<Presupuesto>
)
