package com.almadistefano.finantrack.ui.fragments.perfil

import RemoteDataSource
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.databinding.FragmentPerfilBinding
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.ui.LoginActivity

class PerfilFragment : Fragment() {

    private lateinit var repository: Repository
    private lateinit var vm: PerfilViewModel
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = requireActivity().application as FinantrackApplication

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = getUserId()

        vm = ViewModelProvider(this, PerfilViewModelFactory(repository, userId))[PerfilViewModel::class.java]

        parentFragmentManager.setFragmentResultListener("usuario_actualizado", viewLifecycleOwner) { _, _ ->
            vm.limpiarDatos()
            vm = ViewModelProvider(this, PerfilViewModelFactory(repository, getUserId()))[PerfilViewModel::class.java]
            observeUsuario()
            observeCuentas()
        }

        observeUsuario()
        observeCuentas()

        binding.btnSeleccionarCuenta.setOnClickListener {
            vm.cuentaSeleccionada.value?.let {
                prefs.edit {
                    putInt("cuenta_id", it.id)
                }
                Toast.makeText(context, "Cuenta seleccionada: ${it.nombre}", Toast.LENGTH_SHORT).show()
                parentFragmentManager.setFragmentResult("cuenta_actualizada", Bundle())
            }
        }

        binding.btnCrearCuenta.setOnClickListener {
            CrearCuentaBottomSheet().show(parentFragmentManager, "CrearCuenta")
        }

        binding.btnGuardarCambios.setOnClickListener {
            val nombre = binding.etNombre.text.toString()
            val correo = binding.etCorreo.text.toString()
            val actual = binding.etPasswordActual.text.toString()
            val nueva = binding.etNuevaPassword.text.toString().takeIf { it.isNotBlank() }

            if (nombre.isBlank() || correo.isBlank()) {
                Toast.makeText(context, "Nombre y correo no pueden estar vacíos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val esValida = if (nueva != null) vm.verificarPassword(userId, actual) else true

                if (!esValida) {
                    Toast.makeText(context, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val actualizado = vm.actualizarUsuario(requireContext(), nombre, correo, nueva)
                Toast.makeText(context, if (actualizado) "Actualizado" else "Error", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCerrarSesion.setOnClickListener {
            lifecycleScope.launch {
                repository.limpiarBaseLocal()
                prefs.edit { clear() }
                parentFragmentManager.setFragmentResult("usuario_actualizado", Bundle())
                startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        }

    }

    private fun observeCuentas() {
        lifecycleScope.launch {
            vm.cuentas.collect { cuentas ->
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cuentas.map { it.nombre })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCuentas.adapter = adapter

                val cuentaIdActual = requireContext()
                    .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    .getInt("cuenta_id", -1)

                val indexSeleccionada = cuentas.indexOfFirst { it.id == cuentaIdActual }
                if (indexSeleccionada != -1) {
                    binding.spinnerCuentas.setSelection(indexSeleccionada)
                    vm.seleccionarCuenta(cuentas[indexSeleccionada])
                }

                binding.spinnerCuentas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        vm.seleccionarCuenta(cuentas[position])
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            }
        }
    }

    private fun getUserId(): Int {
        return requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("usuario_id", -1)
    }

    private fun observeUsuario() {
        lifecycleScope.launch {
            vm.usuario.collect {
                binding.etNombre.setText(it?.nombre ?: "")
                binding.etCorreo.setText(it?.correo ?: "")
            }
        }
    }
}
