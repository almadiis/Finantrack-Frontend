
package com.almadistefano.finantrack.ui.fragments.transacciones

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Transaccion
import com.almadistefano.finantrack.model.TransaccionConCuentaYCategoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransaccionesViewModel(
    private val repository: Repository,
    private val cuentaId: Int
) : ViewModel() {

    private val _transacciones = MutableStateFlow<List<TransaccionConCuentaYCategoria>>(emptyList())
    val transacciones: StateFlow<List<TransaccionConCuentaYCategoria>> = _transacciones

    init {
        fetchTransacciones()
    }

    private fun fetchTransacciones() {
        viewModelScope.launch {
            try {
                val transaccionesConCategoria = repository.getTransaccionesDeLaCuenta(cuentaId)
                _transacciones.value = transaccionesConCategoria
            } catch (e: Exception) {
                Log.e("TransaccionesViewModel", "Error al obtener transacciones con categoría: ${e.message}")
            }
        }
    }
    fun recargar() {
        fetchTransacciones()
    }

}

@Suppress("UNCHECKED_CAST")
class TransaccionesViewModelFactory(
    private val repository: Repository,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransaccionesViewModel::class.java)) {
            return TransaccionesViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
