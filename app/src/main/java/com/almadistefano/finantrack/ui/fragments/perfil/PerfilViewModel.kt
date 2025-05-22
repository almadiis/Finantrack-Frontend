package com.almadistefano.finantrack.ui.fragments.perfil

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repository: Repository,
    private val userId: Int
) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _cuentas = MutableStateFlow<List<Cuenta>>(emptyList())
    val cuentas: StateFlow<List<Cuenta>> = _cuentas

    private val _cuentaSeleccionada = MutableStateFlow<Cuenta?>(null)
    val cuentaSeleccionada: StateFlow<Cuenta?> = _cuentaSeleccionada

    init {
        fetchUsuario()
        fetchCuentas()
    }

    private fun fetchUsuario() {
        viewModelScope.launch {
            repository.getUsuarioActual(userId).collect {
                _usuario.value = it
            }
        }
    }


    private fun fetchCuentas() {
        viewModelScope.launch {
            repository.getCuentas(userId).collect {
                _cuentas.value = it
            }
        }
    }

    fun seleccionarCuenta(cuenta: Cuenta) {
        _cuentaSeleccionada.value = cuenta
    }

    suspend fun actualizarUsuario(context: Context, nombre: String, correo: String, nuevaPassword: String?): Boolean {
        return repository.actualizarUsuario(context, nombre, correo, nuevaPassword)
    }

    suspend fun verificarPassword(userId: Int, password: String): Boolean {
        return repository.verificarPassword(userId, password)
    }

    fun limpiarDatos() {
        _usuario.value = null
        _cuentas.value = emptyList()
        _cuentaSeleccionada.value = null
    }
}


@Suppress("UNCHECKED_CAST")
class PerfilViewModelFactory(
    private val repository: Repository,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            return PerfilViewModel(repository,userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
