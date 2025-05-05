package com.almadistefano.finantrack.ui.fragments.transacciones

import RemoteDataSource
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.adapters.TransaccionAdapter
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.databinding.FragmentTransactionsBinding
import kotlinx.coroutines.launch


class TransaccionesFragment : Fragment() {


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
    private val vm: TransaccionesViewModel by viewModels {
        TransaccionesViewModelFactory(repository)
    }

    private var _binding: FragmentTransactionsBinding? = null
    private lateinit var adapter: TransaccionAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TransaccionAdapter(emptyList())
        binding.rvTransacciones.adapter = adapter
        binding.rvTransacciones.layoutManager = LinearLayoutManager(context)

        vm.sincronizarTransacciones()

        observeTransacciones()
    }

    private fun observeTransacciones() {
        lifecycleScope.launch {
            vm.transacciones.collect { transacciones ->
                Log.d("TransaccionesFragment", "Transacciones recibidas: $transacciones")
                adapter.updateData(transacciones)
            }
        }
    }
}