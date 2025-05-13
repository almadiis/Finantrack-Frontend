package com.almadistefano.finantrack.ui.fragments.presupuestos

import RemoteDataSource
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.adapters.PresupuestoAdapter
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.databinding.FragmentPresupuestosBinding
import com.almadistefano.finantrack.model.PresupuestoConUsuarioYCategoria
import kotlinx.coroutines.launch

class PresupuestosFragment : Fragment() {

    private lateinit var repository: Repository
    private lateinit var vm: PresupuestosViewModel

    private var _binding: FragmentPresupuestosBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PresupuestoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = requireActivity().application as FinantrackApplication

        val userId = requireContext()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("usuario_id", -1)

        repository = Repository(
            LocalDataSource(
                app.appDB.cuentaDao(),
                app.appDB.categoriaDao(),
                app.appDB.presupuestoDao(),
                app.appDB.transaccionDao(),
                app.appDB.usuarioDao()
            ),
            RemoteDataSource()
        )

        val factory = PresupuestosViewModelFactory(repository, userId)
        vm = ViewModelProvider(this, factory)[PresupuestosViewModel::class.java]
    }

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

        adapter = PresupuestoAdapter(emptyList()) { presupuestoConCategoria ->
            // Confirmación opcional:
            AlertDialog.Builder(requireContext())
                .setTitle("¿Eliminar presupuesto?")
                .setMessage("¿Estás seguro de que deseas eliminar este presupuesto?")
                .setPositiveButton("Sí") { _, _ ->
                    lifecycleScope.launch {
                        repository.eliminarPresupuesto(presupuestoConCategoria.presupuesto)
                        vm.sincronizarPresupuestos()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        binding.rvPresupuestos.adapter = adapter

        binding.rvPresupuestos.adapter = adapter
        binding.rvPresupuestos.layoutManager = LinearLayoutManager(requireContext())

        observePresupuestos()
        binding.fabNuevoPresupuesto.setOnClickListener {
            val bottomSheet = AddPresupuestoBottomSheet()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        parentFragmentManager.setFragmentResultListener("presupuesto_guardado", viewLifecycleOwner) { _, _ ->
            vm.sincronizarPresupuestos()
        }

    }

    private fun observePresupuestos() {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.presupuestos.collect { lista: List<PresupuestoConUsuarioYCategoria> ->
                adapter.updateData(lista)
            }
        }
    }
}
