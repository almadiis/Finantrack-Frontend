package com.almadistefano.finantrack.ui.fragments.perfil

import RemoteDataSource
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.databinding.FragmentPerfilBinding

class PerfilFragment : Fragment() {

    private val repository: Repository by lazy {
        val app = requireActivity().application as FinantrackApplication
        Repository(
            LocalDataSource(
                app.appDB.cuentasDao(),
                app.appDB.categoriasDao(),
                app.appDB.presupuestosDao(),
                app.appDB.transaccionesDao(),
                app.appDB.usuarioDao()
            ),
            RemoteDataSource()
        )

    }
    private val vm: PerfilViewModel by viewModels {
        PerfilViewModelFactory(repository)
    }

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
