package com.almadistefano.finantrack.ui.fragments.home

import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Cuenta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(repository: Repository, userId : Int) : ViewModel() {

    val repository: Repository = repository
    private val _cuentas = MutableStateFlow<List<Cuenta>>(emptyList())
    var cuentas: StateFlow<List<Cuenta>> = _cuentas
    private var _fragmentShowed : String? = null
    val fragmentShowed : String?
        get() = _fragmentShowed
    val userId : Int = userId


    init {
        sincronizarCuentas()
    }

    private fun fetchCuentas() {
        viewModelScope.launch {
            try {
                // Primero, intenta obtener las cuentas de la base local
                repository.getCuentas(userId).collect {
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
            repository.syncCuentas(userId)
            fetchCuentas()  // Recargar las cuentas después de sincronizar
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