package com.almadistefano.finantrack.ui.fragments.transacciones

import RemoteDataSource
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import java.text.SimpleDateFormat
import java.util.*

class TransaccionesFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TransaccionAdapter

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

        val cuentaId = requireContext()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("cuenta_id", -1)

        if (cuentaId == -1) {
            Log.e("TransaccionesFragment", "No se ha seleccionado una cuenta.")
            binding.tvSinTransacciones.visibility = View.VISIBLE
            binding.tvSinTransacciones.text = "No hay cuenta seleccionada. Selecciona una desde el perfil."
            return
        }

        val app = requireActivity().application as FinantrackApplication
        val repository = Repository(
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
        val vm = ViewModelProvider(this, factory)[TransaccionesViewModel::class.java]

        adapter = TransaccionAdapter(emptyList())
        binding.rvTransacciones.adapter = adapter
        binding.rvTransacciones.layoutManager = LinearLayoutManager(context)

        observeTransacciones(vm)

        val userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("usuario_id", -1)

        lifecycleScope.launch {
            repository.getCategoriasFiltradas(userId).collect { categorias ->
                val nombres = listOf("Todas") + categorias.map { it.nombre }
                val categoriaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombres)
                categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategoria.adapter = categoriaAdapter

                binding.spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        val selectedCategoria = parent.getItemAtPosition(position).toString()
                        vm.aplicarFiltros(
                            categoria = if (selectedCategoria == "Todas") null else selectedCategoria,
                            tipo = if (binding.spinnerTipo.selectedItem.toString() == "Todos") null else binding.spinnerTipo.selectedItem.toString(),
                            fecha = binding.etFechaFiltro.text.toString()
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            }
        }

        binding.spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                vm.aplicarFiltros(
                    categoria = if (binding.spinnerCategoria.selectedItem.toString() == "Todas") null else binding.spinnerCategoria.selectedItem.toString(),
                    tipo = if (parent.getItemAtPosition(position).toString() == "Todos") null else parent.getItemAtPosition(position).toString(),
                    fecha = binding.etFechaFiltro.text.toString()
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.etFechaFiltro.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val fecha = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                binding.etFechaFiltro.setText(fecha)
                vm.aplicarFiltros(
                    categoria = if (binding.spinnerCategoria.selectedItem.toString() == "Todas") null else binding.spinnerCategoria.selectedItem.toString(),
                    tipo = if (binding.spinnerTipo.selectedItem.toString() == "Todos") null else binding.spinnerTipo.selectedItem.toString(),
                    fecha = fecha
                )
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        binding.fabAgregarTransaccion.setOnClickListener {
            val dialog = AddTransaccionBottomSheet()
            dialog.show(parentFragmentManager, "AddTransaccion")
        }
        binding.btnLimpiarFecha.setOnClickListener {
            binding.etFechaFiltro.setText("")
            vm.aplicarFiltros(
                categoria = binding.spinnerCategoria.selectedItem?.toString().orEmpty(),
                tipo = binding.spinnerTipo.selectedItem?.toString()?.takeIf { it != "Todos" },
                fecha = null // limpia el filtro de fecha
            )
        }


        parentFragmentManager.setFragmentResultListener("transaccion_guardada", viewLifecycleOwner) { _, _ ->
            vm.recargar()
        }

        parentFragmentManager.setFragmentResultListener("cuenta_actualizada", viewLifecycleOwner) { _, _ ->
            val nuevaCuentaId = requireContext()
                .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getInt("cuenta_id", -1)
            vm.actualizarCuenta(nuevaCuentaId)
        }
    }

    private fun observeTransacciones(vm: TransaccionesViewModel) {
        lifecycleScope.launch {
            vm.transacciones.collect { lista ->
                val agrupados = agruparPorFecha(lista)
                adapter.updateData(agrupados)

                binding.tvSinTransacciones.visibility =
                    if (lista.isEmpty()) View.VISIBLE else View.GONE
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
