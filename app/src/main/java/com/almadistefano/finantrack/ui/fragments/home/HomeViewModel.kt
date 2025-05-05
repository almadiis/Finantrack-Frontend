package com.almadistefano.finantrack.ui.fragments.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Cuenta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(repository: Repository) : ViewModel() {

    val repository: Repository = repository
    private val _cuentas = MutableStateFlow<List<Cuenta>>(emptyList())
    var cuentas: StateFlow<List<Cuenta>> = _cuentas
    private var _fragmentShowed : String? = null
    val fragmentShowed : String?
        get() = _fragmentShowed

    /**
     * Establece el fragmento actualmente mostrado.
     *
     * @param fragmentShowed Nombre del fragmento mostrado.
     */
    fun setFragmentShowed(fragmentShowed:String){
        _fragmentShowed = fragmentShowed
    }

    init {
        sincronizarCuentas()
        Log.d("HomeViewModel, init", "Cuentas recibidas: $_cuentas")
    }

    private fun fetchCuentas() {
        viewModelScope.launch {
            try {
                // Primero, intenta obtener las cuentas de la base local
                repository.getCuentas().collect {
                    _cuentas.value = it
                }
            } catch (e: Exception) {
                // Maneja excepciones si la base de datos local falla
            }
        }
    }

    // Sincronizar con la API remota y guardar en local
    fun sincronizarCuentas() {
        viewModelScope.launch {
            repository.syncCuentas()
            fetchCuentas()  // Recargar las cuentas después de sincronizar
        }
    }
}


@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}