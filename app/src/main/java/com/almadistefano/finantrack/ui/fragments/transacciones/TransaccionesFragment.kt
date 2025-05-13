package com.almadistefano.finantrack.ui.fragments.transacciones

import RemoteDataSource
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.adapters.TransaccionAdapter
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.databinding.FragmentTransactionsBinding
import com.almadistefano.finantrack.model.TransaccionConCuentaYCategoria
import kotlinx.coroutines.launch

class TransaccionesFragment : Fragment() {

    private lateinit var repository: Repository
    private lateinit var vm: TransaccionesViewModel

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TransaccionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = requireActivity().application as FinantrackApplication

        val cuentaId = requireContext()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("cuenta_id", -1)

        if (cuentaId == -1) {
            Log.e("TransaccionesFragment", "No se ha seleccionado una cuenta.")
            return
        }

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

        val factory = TransaccionesViewModelFactory(repository, cuentaId)
        vm = ViewModelProvider(this, factory)[TransaccionesViewModel::class.java]
    }

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

        observeTransacciones()

        // FAB para añadir nueva transacción
        binding.fabAgregarTransaccion.setOnClickListener {
            val dialog = AddTransaccionBottomSheet()
            dialog.show(parentFragmentManager, "AddTransaccion")
        }

        // Escuchar resultado del diálogo
        parentFragmentManager.setFragmentResultListener("transaccion_guardada", viewLifecycleOwner) { _, _ ->
            vm.recargar()
        }
    }

    private fun observeTransacciones() {
        lifecycleScope.launch {
            vm.transacciones.collect { lista ->
                val agrupados = agruparPorFecha(lista)
                adapter.updateData(agrupados)
            }
        }
    }

    private fun agruparPorFecha(lista: List<TransaccionConCuentaYCategoria>): List<Any> {
        val agrupado = lista.groupBy { it.transaccion.fecha }
        val resultado = mutableListOf<Any>()
        agrupado.toSortedMap().forEach { (fecha, transacciones) ->
            resultado.add(fecha)
            resultado.addAll(transacciones)
        }
        return resultado
    }
}
