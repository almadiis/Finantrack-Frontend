package com.almadistefano.finantrack.ui.fragments.presupuestos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.PresupuestoConCategoria
import kotlinx.coroutines.launch

class PresupuestosViewModel(private val repository: Repository) : ViewModel() {

    val presupuestosConCategoria: LiveData<List<PresupuestoConCategoria>> = repository.presupuestosConCategoria

    init {
        sincronizarPresupuestos()
    }

    fun sincronizarPresupuestos() {
        viewModelScope.launch {
            try {
                repository.syncPresupuestos()
            } catch (e: Exception) {
                Log.e("PresupuestosViewModel", "Error al sincronizar presupuestos: ${e.message}")
            }
        }
    }
}


@Suppress("UNCHECKED_CAST")
class PresupuestosViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PresupuestosViewModel::class.java)) {
            return PresupuestosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
