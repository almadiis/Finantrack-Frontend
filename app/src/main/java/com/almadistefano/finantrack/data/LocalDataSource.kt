
package com.almadistefano.finantrack.data

import com.almadistefano.finantrack.data.dao.CategoriaDao
import com.almadistefano.finantrack.data.dao.CuentaDao
import com.almadistefano.finantrack.data.dao.PresupuestoDao
import com.almadistefano.finantrack.data.dao.TransaccionDao
import com.almadistefano.finantrack.data.dao.UsuarioDao
import com.almadistefano.finantrack.model.Categoria
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.Presupuesto
import com.almadistefano.finantrack.model.PresupuestoConUsuarioYCategoria
import com.almadistefano.finantrack.model.Transaccion
import com.almadistefano.finantrack.model.TransaccionConCuentaYCategoria
import com.almadistefano.finantrack.model.Usuario
import com.almadistefano.finantrack.model.UsuarioConCuentas
import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val cuentaDao: CuentaDao,
    private val categoriaDao: CategoriaDao,
    private val presupuestoDao: PresupuestoDao,
    private val transaccionDao: TransaccionDao,
    private val usuarioDao: UsuarioDao,
) {

    // --- Cuentas ---
    suspend fun insertCuentas(cuentas: List<Cuenta>) = cuentaDao.insertCuenta(cuentas)
    fun getCuentasByUsuario(usuarioId: Int): Flow<List<Cuenta>> = cuentaDao.getAllCuentas(usuarioId)

    // --- Categorías ---
    suspend fun insertCategorias(categorias: List<Categoria>) = categoriaDao.insertAll(categorias)
    fun getAllCategorias(): Flow<List<Categoria>> = categoriaDao.getAll()

    // --- Presupuestos ---
    suspend fun insertPresupuestos(presupuestos: List<Presupuesto>) = presupuestoDao.insertAll(presupuestos)
    fun getPresupuestosByUsuario(usuarioId: Int): Flow<List<Presupuesto>> = presupuestoDao.getPresupuestosByUsuario(usuarioId)
    fun getPresupuestosConUsuarioYCategoria(): Flow<List<PresupuestoConUsuarioYCategoria>> =
        presupuestoDao.obtenerPresupuestosConUsuarioYCategoria()
    suspend fun eliminarPresupuestos(presupuestoId: Int) = presupuestoDao.eliminarPresupuestos(presupuestoId)

    // --- Transacciones ---
    suspend fun insertTransacciones(transacciones: List<Transaccion>) = transaccionDao.insertAll(transacciones)
    fun getTransaccionesByCuenta(cuentaId: Int): Flow<List<Transaccion>> = transaccionDao.getTransaccionesByCuenta(cuentaId)
    suspend fun getTransaccionesDeLaCuenta(cuentaId: Int): List<TransaccionConCuentaYCategoria> =
        transaccionDao.obtenerTransaccionesDeLaCuenta(cuentaId)
    // --- Usuario ---
    suspend fun saveUsuario(usuario: Usuario) = usuarioDao.insertUsuario(usuario)
    fun getUsuarioActual(): Flow<Usuario?> = usuarioDao.getUsuarioActual()
    suspend fun borrarUsuarios() = usuarioDao.borrarUsuarios()
    suspend fun loginUsuario(username: String, password: String): Usuario? =
        usuarioDao.obtenerUsuarioPorCredenciales(username, password)

    suspend fun getUsuarioByUserName(username: String): Usuario? =
        usuarioDao.getUsuarioByUserName(username)

    suspend fun getUsuarioConCuentas(usuarioId: Int): UsuarioConCuentas =
        usuarioDao.obtenerCuentasDelUsuario(usuarioId)
}

