
package com.almadistefano.finantrack.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.almadistefano.finantrack.data.dao.CategoriaDao
import com.almadistefano.finantrack.data.dao.CuentaDao
import com.almadistefano.finantrack.data.dao.PresupuestoDao
import com.almadistefano.finantrack.data.dao.TransaccionDao
import com.almadistefano.finantrack.data.dao.UsuarioDao
import com.almadistefano.finantrack.model.Categoria
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.Presupuesto
import com.almadistefano.finantrack.model.PresupuestoConCategoria
import com.almadistefano.finantrack.model.Transaccion
import com.almadistefano.finantrack.model.Usuario
import kotlinx.coroutines.flow.Flow


class LocalDataSource(
    private val cuentaDao: CuentaDao,
    private val categoriaDao: CategoriaDao,
    private val presupuestoDao: PresupuestoDao,
    private val transaccionDao: TransaccionDao,
    private val usuarioDao: UsuarioDao,
) {

    suspend fun insertCuentas(cuentas: List<Cuenta>) = cuentaDao.insertCuenta(cuentas)
    fun getAllCuentas(): Flow<List<Cuenta>> = cuentaDao.getAllCuentas()

    suspend fun insertCategorias(categorias: List<Categoria>) = categoriaDao.insertAll(categorias)
    fun getAllCategorias(): Flow<List<Categoria>> = categoriaDao.getAll()

    suspend fun insertPresupuestos(presupuestos: List<Presupuesto>) = presupuestoDao.insertAll(presupuestos)
    fun getAllPresupuestos(): Flow<List<Presupuesto>> = presupuestoDao.getAll()
    val presupuestosConCategoria: LiveData<List<PresupuestoConCategoria>> = presupuestoDao.obtenerPresupuestosConCategoria().asLiveData()


    suspend fun insertTransacciones(transacciones: List<Transaccion>) = transaccionDao.insertAll(transacciones)
    fun getAllTransacciones(): Flow<List<Transaccion>> = transaccionDao.getAll()

    suspend fun saveUsuario(usuario: Usuario) = usuarioDao.insertUsuario(usuario)
    fun getUsuarioActual(): Flow<Usuario?> = usuarioDao.getUsuarioActual()
    suspend fun borrarUsuarios() = usuarioDao.borrarUsuarios()
}
