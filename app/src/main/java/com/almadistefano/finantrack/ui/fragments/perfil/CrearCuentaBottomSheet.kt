package com.almadistefano.finantrack.ui.fragments.perfil

import RemoteDataSource
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.R
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Cuenta
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch


class CrearCuentaBottomSheet : BottomSheetDialogFragment() {

    private lateinit var repository: Repository

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): android.view.View {
        return inflater.inflate(R.layout.bottom_sheet_crear_cuenta, container, false)
    }
    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        val etNombre = view.findViewById<EditText>(R.id.etNombreCuenta)
        val etSaldo = view.findViewById<EditText>(R.id.etSaldoInicial)
        val btnCrear = view.findViewById<Button>(R.id.btnCrearCuenta)

        btnCrear.setOnClickListener {
            val nombre = etNombre.text.toString()
            val saldo = etSaldo.text.toString().toDoubleOrNull() ?: 0.0
            val tipo = if (view.findViewById<RadioButton>(R.id.rbBanco).isChecked) "banco" else "efectivo"

            if (nombre.isBlank()) {
                Toast.makeText(context, "Nombre requerido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val userId = prefs.getInt("usuario_id", -1)

                if (userId == -1) {
                    Toast.makeText(context, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val nuevaCuenta = Cuenta(
                    id = 0,
                    nombre = nombre,
                    saldo = saldo,
                    tipo = tipo,
                    usuarioId = userId
                )
                repository.addCuenta(nuevaCuenta)
                dismiss()
            }
        }
    }
}
