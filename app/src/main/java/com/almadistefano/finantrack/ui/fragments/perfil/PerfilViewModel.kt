package com.almadistefano.finantrack.ui.fragments.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(private val repository: Repository, private val userId: Int) : ViewModel() {

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
            repository.getUsuarioActual().collect { usuarioActual ->
                _usuario.value = usuarioActual
            }
        }
    }

    private fun fetchCuentas() {
        viewModelScope.launch {
            repository.getCuentas(userId).collect { listaCuentas ->
                _cuentas.value = listaCuentas
            }
        }
    }

    fun seleccionarCuenta(cuenta: Cuenta) {
        _cuentaSeleccionada.value = cuenta
    }

    // Método para obtener el ID de la cuenta seleccionada
    fun getCuentaId(): Int {
        return _cuentaSeleccionada.value?.id ?: -1
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
