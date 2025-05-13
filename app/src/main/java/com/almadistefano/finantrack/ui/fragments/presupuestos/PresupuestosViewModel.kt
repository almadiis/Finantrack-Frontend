package com.almadistefano.finantrack.ui.fragments.presupuestos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.PresupuestoConUsuarioYCategoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PresupuestosViewModel(
    private val repository: Repository,
    private val userId: Int
) : ViewModel() {

    private val _presupuestos = MutableStateFlow<List<PresupuestoConUsuarioYCategoria>>(emptyList())
    val presupuestos: StateFlow<List<PresupuestoConUsuarioYCategoria>> = _presupuestos

    init {
        fetchPresupuestos()
    }

    private fun fetchPresupuestos() {
        viewModelScope.launch {
            try {
                repository.presupuestosConUsuarioYCategoria.collect { lista ->
                    _presupuestos.value = lista.filter { it.presupuesto.usuarioId == userId }
                }
            } catch (e: Exception) {
                Log.e("PresupuestosViewModel", "Error al obtener presupuestos: ${e.message}")
            }
        }
    }

    fun sincronizarPresupuestos() {
        viewModelScope.launch {
            try {
                repository.syncPresupuestos(userId)
                fetchPresupuestos()
            } catch (e: Exception) {
                Log.e("PresupuestosViewModel", "Error al sincronizar presupuestos: ${e.message}")
            }
        }
    }
}


class PresupuestosViewModelFactory(
    private val repository: Repository,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PresupuestosViewModel::class.java)) {
            return PresupuestosViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
