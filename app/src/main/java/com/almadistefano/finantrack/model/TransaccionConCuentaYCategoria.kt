package com.almadistefano.finantrack.model

import androidx.room.Embedded
import androidx.room.Relation

data class TransaccionConCuentaYCategoria(
    @Embedded val transaccion: Transaccion,

    @Relation(
        parentColumn = "cuenta_id",
        entityColumn = "id"
    )
    val cuenta: Cuenta,

    @Relation(
        parentColumn = "categoria_id",
        entityColumn = "id"
    )
    val categoria: Categoria
)
