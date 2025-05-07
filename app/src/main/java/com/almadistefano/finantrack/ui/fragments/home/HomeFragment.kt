package com.almadistefano.finantrack.ui.fragments.home

import RemoteDataSource
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.adapters.CuentaAdapter
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var repository: Repository
    private lateinit var vm: HomeViewModel

    private var _binding: FragmentHomeBinding? = null
    private lateinit var adapter: CuentaAdapter
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = requireActivity().application as FinantrackApplication

        val userId = requireContext()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("usuario_id", -1)

        repository = Repository(
            LocalDataSource(
                app.appDB.cuentasDao(),
                app.appDB.categoriasDao(),
                app.appDB.presupuestosDao(),
                app.appDB.transaccionesDao(),
                app.appDB.usuarioDao()
            ),
            RemoteDataSource()
        )

        val factory = HomeViewModelFactory(repository, userId)
        vm = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CuentaAdapter(emptyList())
        binding.rvCuentas.adapter = adapter

        binding.rvCuentas.layoutManager = LinearLayoutManager(context)
        vm.sincronizarCuentas()

        observeCuentas()
    }
    private fun observeCuentas() {

        lifecycleScope.launch {
            vm.cuentas.collect { cuentas ->
                adapter.updateData(cuentas)
            }
        }
    }
}