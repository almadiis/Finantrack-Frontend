
package com.almadistefano.finantrack.ui.fragments.transacciones

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Transaccion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransaccionesViewModel(private val repository: Repository) : ViewModel() {

    private val _transacciones = MutableStateFlow<List<Transaccion>>(emptyList())
    val transacciones: StateFlow<List<Transaccion>> = _transacciones

    init {
        sincronizarTransacciones()
    }

    private fun fetchTransacciones() {
        viewModelScope.launch {
            try {
                repository.getTransacciones().collect { transaccionesList ->
                    _transacciones.value = transaccionesList
                }
            } catch (e: Exception) {
                Log.e("TransaccionesViewModel", "Error al obtener transacciones locales: ${e.message}")
            }
        }
    }

    fun sincronizarTransacciones() {
        viewModelScope.launch {
            try {
                repository.syncTransacciones()
            } catch (e: Exception) {
                Log.e("TransaccionesViewModel", "Error al sincronizar transacciones: ${e.message}")
            }
            fetchTransacciones()
        }
    }
}

@Suppress("UNCHECKED_CAST")
class TransaccionesViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransaccionesViewModel::class.java)) {
            return TransaccionesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
