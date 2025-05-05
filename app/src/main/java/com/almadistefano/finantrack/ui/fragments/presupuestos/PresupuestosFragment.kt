package com.almadistefano.finantrack.ui.fragments.presupuestos

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
import com.almadistefano.finantrack.adapters.PresupuestoAdapter
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.databinding.FragmentPresupuestosBinding
import kotlinx.coroutines.launch

class PresupuestosFragment : Fragment() {

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

    private val vm: PresupuestosViewModel by viewModels {
        PresupuestosViewModelFactory(repository)
    }

    private var _binding: FragmentPresupuestosBinding? = null
    private lateinit var adapter: PresupuestoAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPresupuestosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PresupuestoAdapter(emptyList())
        binding.rvPresupuestos.adapter = adapter
        binding.rvPresupuestos.layoutManager = LinearLayoutManager(context)

        vm.sincronizarPresupuestos()

        observePresupuestos()
    }

    private fun observePresupuestos() {
        lifecycleScope.launch {
            vm.presupuestosConCategoria.observe(viewLifecycleOwner) { lista ->
                adapter.updateData(lista)
            }

        }
    }
}
