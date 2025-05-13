package com.almadistefano.finantrack.model

import androidx.room.Embedded
import androidx.room.Relation

data class PresupuestoConUsuarioYCategoria(
    @Embedded val presupuesto: Presupuesto,


    @Relation(
        parentColumn = "usuario_id",
        entityColumn = "id"
    )
    val usuario: Usuario?,

    @Relation(
        parentColumn = "categoria_id",
        entityColumn = "id"
    )
    val categoria: Categoria?
)