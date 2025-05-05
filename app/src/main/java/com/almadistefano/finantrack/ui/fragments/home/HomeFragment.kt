package com.almadistefano.finantrack.ui.fragments.home

import RemoteDataSource
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.adapters.CuentaAdapter
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

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

    private val vm: HomeViewModel by viewModels {
        HomeViewModelFactory(repository)
    }

    private var _binding: FragmentHomeBinding? = null
    private lateinit var adapter: CuentaAdapter
    private val binding get() = _binding!!

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
                Log.d("HomeFragment", "Cuentas recibidas: $cuentas")

                adapter.updateData(cuentas)
//                binding.tvNoInfo.visibility = if (motos.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

}