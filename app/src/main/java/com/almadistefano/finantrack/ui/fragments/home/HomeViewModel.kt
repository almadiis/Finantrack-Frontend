package com.almadistefano.finantrack.ui.fragments.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Cuenta
import com.almadistefano.finantrack.model.Transaccion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository, private val userId: Int) : ViewModel() {

    private val _cuentas = MutableStateFlow<List<Cuenta>>(emptyList())
    val cuentas: StateFlow<List<Cuenta>> = _cuentas

    private val _transacciones = MutableStateFlow<List<Transaccion>>(emptyList())
    val transacciones: StateFlow<List<Transaccion>> = _transacciones

    private val _categorias = MutableStateFlow<Map<Int, String>>(emptyMap())
    val categorias: StateFlow<Map<Int, String>> = _categorias

    init {
        fetchCuentas()
        fetchCategorias()
    }

    private fun fetchCuentas() {
        viewModelScope.launch {
            try {
                repository.getCuentas(userId).collect {
                    _cuentas.value = it
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching cuentas: ${e.message}")
            }
        }
    }

    fun fetchTransacciones(cuentaId: Int) {
        viewModelScope.launch {
            try {
                repository.getTransacciones(cuentaId).collect {
                    _transacciones.value = it
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching transacciones: ${e.message}")
            }
        }
    }

    private fun fetchCategorias() {
        viewModelScope.launch {
            try {
                repository.getCategorias().collect { lista ->
                    val mapa = lista.associateBy({ it.id }, { it.nombre })
                    _categorias.value = mapa
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching categorias: ${e.message}")
            }
        }
    }
}


@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(
    private val repository: Repository,
    private val userId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository,userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}