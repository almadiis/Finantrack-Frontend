
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
    private val _filtroCategoria = MutableStateFlow<String?>(null)
    private val _filtroTipo = MutableStateFlow<String?>(null)
    private val _filtroFecha = MutableStateFlow<String?>(null) // o usar rango si lo prefieres

    fun aplicarFiltros(
        categoria: String? = null,
        tipo: String? = null,
        fecha: String? = null
    ) {
        _filtroCategoria.value = categoria
        _filtroTipo.value = tipo
        _filtroFecha.value = fecha
        fetchTransacciones()
    }

    init {
        fetchTransacciones()
    }

    private fun fetchTransacciones() {
        viewModelScope.launch {
            try {
                val todas = repository.getTransaccionesDeLaCuenta(cuentaId)

                val filtradas = todas.filter { trans ->
                    val coincideCategoria = _filtroCategoria.value.isNullOrBlank() ||
                            trans.categoria?.nombre?.contains(_filtroCategoria.value!!, ignoreCase = true) == true

                    val coincideTipo = _filtroTipo.value.isNullOrBlank() ||
                            trans.categoria?.tipo.equals(_filtroTipo.value, ignoreCase = true)

                    val coincideFecha = _filtroFecha.value.isNullOrBlank() ||
                            trans.transaccion.fecha.contains(_filtroFecha.value!!) // depende del formato

                    coincideCategoria && coincideTipo && coincideFecha
                }

                _transacciones.value = filtradas
            } catch (e: Exception) {
                Log.e("TransaccionesViewModel", "Error al obtener transacciones con filtros: ${e.message}")
            }
        }
    }

    fun recargar() {
        fetchTransacciones()
    }
    fun actualizarCuenta(nuevoId: Int) {
        viewModelScope.launch {
            try {
                val nuevas = repository.getTransaccionesDeLaCuenta(nuevoId)
                _transacciones.value = nuevas
            } catch (e: Exception) {
                Log.e("TransaccionesViewModel", "Error al cambiar cuenta: ${e.message}")
            }
        }
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
