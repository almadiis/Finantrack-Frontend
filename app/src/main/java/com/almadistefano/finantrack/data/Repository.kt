package com.almadistefano.finantrack.data

import RemoteDataSource
import android.content.Context
import android.util.Log
import com.almadistefano.finantrack.model.Categoria
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.LoginRequest
import com.almadistefano.finantrack.model.Presupuesto
import com.almadistefano.finantrack.model.PresupuestoConUsuarioYCategoria
import com.almadistefano.finantrack.model.Transaccion
import com.almadistefano.finantrack.model.TransaccionConCuentaYCategoria
import com.almadistefano.finantrack.model.Usuario
import com.almadistefano.finantrack.model.UsuarioConCuentas
import kotlinx.coroutines.flow.Flow

class Repository(
    private val local: LocalDataSource,
    private val remote: RemoteDataSource
) {

    val presupuestosConUsuarioYCategoria: Flow<List<PresupuestoConUsuarioYCategoria>>
        get() = local.getPresupuestosConUsuarioYCategoria()


    // --- Cuentas ---
    fun getCuentas(usuarioId: Int): Flow<List<Cuenta>> = local.getCuentasByUsuario(usuarioId)
    suspend fun syncCuentas() {
        remote.getCuentas()?.let { local.insertCuentas(it) }
    }
    suspend fun getPrimeraCuenta(usuarioId: Int): Cuenta? {
        return local.getPrimeraCuentaPorUsuario(usuarioId)
    }

    suspend fun addCuenta(cuenta: Cuenta) {
        remote.postCuenta(cuenta)
        local.insertCuentas(listOf(cuenta))
    }


    // Verifica la contraseña actual del usuario
    suspend fun verificarPassword(userId: Int, password: String): Boolean {
        return remote.verificarPassword(userId, password)
    }


    // --- Categorías ---
    fun getCategorias(): Flow<List<Categoria>> = local.getAllCategorias()
    suspend fun syncCategorias() {
        remote.getCategorias()?.let { local.insertCategorias(it) }
    }
    suspend fun addCategoria(categoria: Categoria) {
        val creada = remote.postCategoria(categoria)
        local.insertCategorias(listOf(creada))
    }
    suspend fun eliminarCategoria(categoria: Categoria) {
        local.eliminarCategorias(categoria.id)
        remote.eliminarCategoria(categoria.id)
    }
    fun getCategoriasFiltradas(usuarioId: Int): Flow<List<Categoria>> =
        local.getCategoriasFiltradas(usuarioId)


    // --- Presupuestos ---
    fun getPresupuestos(usuarioId: Int): Flow<List<Presupuesto>> = local.getPresupuestosByUsuario(usuarioId)
    suspend fun syncPresupuestos(usuarioId: Int) {
        remote.getPresupuestos()
            ?.filter { it.usuarioId == usuarioId }
            ?.let { local.insertPresupuestos(it) }
    }

    suspend fun addPresupuesto(presupuesto: Presupuesto) {
        remote.postPresupuesto(presupuesto)
        local.insertPresupuestos(listOf(presupuesto))
    }
    suspend fun addPresupuestoRemoto(presupuesto: Presupuesto) {
        remote.postPresupuesto(presupuesto)
    }

    suspend fun postPresupuesto(presupuesto: Presupuesto) {
        remote.postPresupuesto(presupuesto)
        local.insertPresupuestos(listOf(presupuesto))
    }
    suspend fun eliminarPresupuesto(p: Presupuesto) {
        local.eliminarPresupuestos(p.id)
        remote.eliminarPresupuesto(p.id)
    }


    // --- Transacciones ---
    fun getTransacciones(cuentaId: Int): Flow<List<Transaccion>> = local.getTransaccionesByCuenta(cuentaId)
    suspend fun syncTransacciones() {
        val transacciones: List<Transaccion>? = remote.getTransacciones()
        transacciones?.let {
            local.insertTransacciones(it)
        }
    }

    suspend fun getTransaccionesDeLaCuenta(cuentaId: Int): List<TransaccionConCuentaYCategoria> =
        local.getTransaccionesDeLaCuenta(cuentaId)

    suspend fun addTransaccion(transaccion: Transaccion) {
        remote.postTransaccion(transaccion)
        local.insertTransacciones(listOf(transaccion))
    }

    // --- Usuarios ---
    fun getUsuarioActual(userId: Int): Flow<Usuario?> {
        return local.getUsuarioActual(userId)
    }

    suspend fun loginUsuario(username: String, password: String): Usuario? {
        // Hacemos la llamada al backend (API remota) para autenticar al usuario
        val remoteUser = remote.loginUsuario(LoginRequest(username, password))
        Log.d("Repository", "Usuario remoto: $remoteUser")
        return if (remoteUser != null) {

            local.saveUsuario(remoteUser)

            remoteUser
        } else {
            null
        }
    }

    suspend fun registrarUsuario(usuario: Usuario) {
        remote.postUsuario(usuario)
        local.saveUsuario(usuario)
    }

    suspend fun getUsuarioConCuentas(usuarioId: Int): UsuarioConCuentas = local.getUsuarioConCuentas(usuarioId)
    suspend fun syncUsuarios() {
        remote.getUsuarios()?.firstOrNull()?.let { local.saveUsuario(it) }
    }

    suspend fun actualizarUsuario(
        context: Context,
        nombre: String,
        correo: String,
        nuevaPassword: String?
    ): Boolean {
        return remote.actualizarUsuario(context, nombre, correo, nuevaPassword)
    }
    suspend fun limpiarBaseLocal() {
        local.clearAllData()
    }


}

