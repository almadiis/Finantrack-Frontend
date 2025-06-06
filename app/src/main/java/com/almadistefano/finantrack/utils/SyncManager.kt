package com.almadistefano.finantrack.utils

import com.almadistefano.finantrack.data.Repository

class SyncManager(private val repository: Repository) {

    suspend fun syncAll(usuarioId: Int) {
        repository.syncUsuarios()
        repository.syncCategorias()
        repository.syncCuentas()
        repository.syncPresupuestos(usuarioId)
        repository.syncTransacciones()
    }
}
