package com.almadistefano.finantrack.ui.fragments.categorias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Categoria
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoriaViewModel(private val repository: Repository, usuarioId: Int) : ViewModel() {
    val categorias: StateFlow<List<Categoria>> = repository.getCategoriasFiltradas(usuarioId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun agregarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            repository.addCategoria(categoria)
        }
    }
}

class CategoriaViewModelFactory(private val repository: Repository, private val usuarioId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoriaViewModel(
            repository, usuarioId) as T
    }
}
