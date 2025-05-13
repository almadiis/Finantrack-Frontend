package com.almadistefano.finantrack.ui.fragments.perfil

import RemoteDataSource
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.databinding.FragmentPerfilBinding
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast


class PerfilFragment : Fragment() {

    private lateinit var repository: Repository
    private lateinit var vm: PerfilViewModel
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

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

        val factory = PerfilViewModelFactory(repository, userId)
        vm = ViewModelProvider(this, factory)[PerfilViewModel::class.java]
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuramos el Spinner para las cuentas
        observeCuentas()

        binding.btnSeleccionarCuenta.setOnClickListener {
            val cuentaSeleccionada = vm.cuentaSeleccionada.value
            if (cuentaSeleccionada != null) {
                // Guardamos el ID de la cuenta seleccionada en SharedPreferences
                val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit {
                    putInt("cuenta_id", cuentaSeleccionada.id)
                }
                // Mostrar mensaje de confirmación
                Toast.makeText(requireContext(), "Cuenta seleccionada: ${cuentaSeleccionada.nombre}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Por favor, selecciona una cuenta.", Toast.LENGTH_SHORT).show()
            }
        }
        // Rellenar campos actuales
        lifecycleScope.launch {
            vm.usuario.collect { usuario ->
                usuario?.let {
                    binding.etNombre.setText(it.nombre)
                    binding.etCorreo.setText(it.correo)
                }
            }
        }

        binding.btnGuardarCambios.setOnClickListener {
            val nombre = binding.etNombre.text.toString()
            val correo = binding.etCorreo.text.toString()


            lifecycleScope.launch {

                val actualizado = repository.actualizarUsuario(requireContext(), nombre, correo)
                if (actualizado) {
                    Toast.makeText(requireContext(), "Datos actualizados", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun observeCuentas() {
        // Usamos collect para obtener los datos del StateFlow
        lifecycleScope.launch {
            vm.cuentas.collect { cuentas ->
                // Creamos un adaptador para el Spinner con la lista de cuentas
                val nombresCuentas = cuentas.map { it.nombre } // Obtenemos los nombres de las cuentas
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresCuentas)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Asignamos el adaptador al Spinner
                binding.spinnerCuentas.adapter = adapter

                // Configuramos el listener para la selección del Spinner
                binding.spinnerCuentas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val cuentaSeleccionada = cuentas[position]
                        vm.seleccionarCuenta(cuentaSeleccionada)
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                        // No hacer nada cuando no se selecciona nada
                    }
                }
            }
        }
    }
}
