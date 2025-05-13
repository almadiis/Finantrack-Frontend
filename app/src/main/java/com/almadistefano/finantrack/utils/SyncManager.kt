package com.almadistefano.finantrack.utils

import android.view.ViewGroup
import com.almadistefano.finantrack.data.Repository
import kotlin.collections.List

class SyncManager(private val repository: Repository) {

    suspend fun syncAll(usuarioId: Int) {
        repository.syncUsuarios()
        repository.syncCategorias()
        repository.syncCuentas()
        repository.syncPresupuestos(usuarioId)
        repository.syncTransacciones()
    }
}
