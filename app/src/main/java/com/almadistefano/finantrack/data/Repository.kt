package com.almadistefano.finantrack.data
import RemoteDataSource
import androidx.lifecycle.LiveData
import com.almadistefano.finantrack.model.Categoria
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.Presupuesto
import com.almadistefano.finantrack.model.PresupuestoConCategoria
import com.almadistefano.finantrack.model.Transaccion
import com.almadistefano.finantrack.model.Usuario
import com.almadistefano.finantrack.model.UsuarioConCuentas
import kotlinx.coroutines.flow.Flow


class Repository(
    var local: LocalDataSource,
    var remote: RemoteDataSource
) {
    val presupuestosConCategoria: LiveData<List<PresupuestoConCategoria>>
        get() = local.presupuestosConCategoria

    // --- Cuentas ---
    fun getCuentas(usuarioId: Int): Flow<List<Cuenta>> = local.getCuentasByUsuario(usuarioId)

    suspend fun syncCuentas(userId: Int) {
        val cuentas = remote.getCuentas()
        val cuentasConUsuario = cuentas?.map { it.copy(usuarioId = userId) } ?: emptyList()
        local.insertCuentas(cuentasConUsuario)
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

    fun getTransacciones(userId : Int): Flow<List<Transaccion>> = local.getAllTransacciones(userId)
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
    suspend fun loginUsuario(username: String, password: String): Usuario? {
        return local.loginUsuario(username, password)
    }
    suspend fun registrarUsuario(usuario: Usuario) {
        local.saveUsuario(usuario)
        remote.postUsuario(usuario)
    }
    suspend fun obtenerCuentasDeUsuario(usuarioId: Int): UsuarioConCuentas {
        return local.obtenerCuentasDelUsuario(usuarioId)
    }

}
