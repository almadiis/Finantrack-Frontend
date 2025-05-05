package com.almadistefano.finantrack.data
import RemoteDataSource
import androidx.lifecycle.LiveData
import com.almadistefano.finantrack.model.Categoria
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.Presupuesto
import com.almadistefano.finantrack.model.PresupuestoConCategoria
import com.almadistefano.finantrack.model.Transaccion
import com.almadistefano.finantrack.model.Usuario
import kotlinx.coroutines.flow.Flow


class Repository(
    private val local: LocalDataSource,
    private val remote: RemoteDataSource
) {
    val presupuestosConCategoria: LiveData<List<PresupuestoConCategoria>>
        get() = local.presupuestosConCategoria

    // --- Cuentas ---
    fun getCuentas(): Flow<List<Cuenta>> = local.getAllCuentas()

    suspend fun syncCuentas() {
        remote.getCuentas()?.let { local.insertCuentas(it) }
    }

    suspend fun addCuenta(cuenta: Cuenta) {
        local.insertCuentas(listOf(cuenta))
        remote.postCuenta(cuenta) // Enviar a la API si hay conexión
    }

    // --- Categorías ---
    suspend fun syncCategorias() {
        remote.getCategorias()?.let { local.insertCategorias(it) }
    }

    suspend fun addCategoria(categoria: Categoria) {
        local.insertCategorias(listOf(categoria))
        remote.postCategoria(categoria)
    }

    // --- Presupuestos ---
    suspend fun getPresupuestos(): Flow<List<Presupuesto>> = local.getAllPresupuestos()

    suspend fun syncPresupuestos() {
        remote.getPresupuestos()?.let { local.insertPresupuestos(it) }
    }

    suspend fun addPresupuesto(presupuesto: Presupuesto) {
        local.insertPresupuestos(listOf(presupuesto))
        remote.postPresupuesto(presupuesto)
    }

    // --- Transacciones ---

    suspend fun getTransacciones(): Flow<List<Transaccion>> = local.getAllTransacciones()


    suspend fun syncTransacciones() {
        remote.getTransacciones()?.let { local.insertTransacciones(it) }
    }

    suspend fun addTransaccion(transaccion: Transaccion) {
        local.insertTransacciones(listOf(transaccion))
        remote.postTransaccion(transaccion)
    }

    // --- Usuarios ---
    suspend fun getUsuarioActual(): Flow<Usuario?> = local.getUsuarioActual()
    suspend fun syncUsuarios() {
        remote.getUsuarios()?.let { local.saveUsuario(it[0]) }
    }

    suspend fun addUsuario(usuario: Usuario) {
        local.saveUsuario(listOf(usuario)[0])
        remote.postUsuario(usuario)
    }
}
