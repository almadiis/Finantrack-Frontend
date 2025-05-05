package com.almadistefano.finantrack.model

import androidx.room.Embedded
import androidx.room.Relation

data class PresupuestoConCategoria(
    @Embedded val presupuesto: Presupuesto,

    @Relation(
        parentColumn = "categoriaId",
        entityColumn = "id"
    )
    val categoria: Categoria?
)
