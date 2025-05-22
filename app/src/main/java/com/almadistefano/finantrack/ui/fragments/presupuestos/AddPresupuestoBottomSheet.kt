package com.almadistefano.finantrack.ui.fragments.presupuestos

import RemoteDataSource
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.R
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Categoria
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.setFragmentResult
import com.almadistefano.finantrack.model.Presupuesto

class AddPresupuestoBottomSheet : BottomSheetDialogFragment() {

    private lateinit var repository: Repository
    private var listaCategorias: List<Categoria> = emptyList()
    private var fechaInicio: Date? = null

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
        return inflater.inflate(R.layout.bottom_sheet_add_presupuesto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etMonto = view.findViewById<EditText>(R.id.etMontoMaximo)
        val spinnerCategoria = view.findViewById<Spinner>(R.id.spinnerCategoria)
        val spinnerPeriodo = view.findViewById<Spinner>(R.id.spinnerPeriodo)
        val btnFechaInicio = view.findViewById<Button>(R.id.btnFechaInicio)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)
        val userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getInt("usuario_id", -1)

        lifecycleScope.launch {
            repository.getCategoriasFiltradas(userId).collect { categorias ->
                listaCategorias = categorias
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categorias.map { it.nombre })
                spinnerCategoria.adapter = adapter
            }
        }

        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        btnFechaInicio.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                fechaInicio = GregorianCalendar(y, m, d).time
                btnFechaInicio.text = "Inicio: ${formato.format(fechaInicio!!)}"
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnGuardar.setOnClickListener {
            val monto = etMonto.text.toString().toDoubleOrNull()
            val categoria = listaCategorias.getOrNull(spinnerCategoria.selectedItemPosition)
            val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val usuarioId = prefs.getInt("usuario_id", -1)
            val periodoSeleccionado = spinnerPeriodo.selectedItem.toString().lowercase(Locale.ROOT)


            if (monto == null || categoria == null || fechaInicio == null || usuarioId == -1) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (fechaInicio == null) {
                Toast.makeText(requireContext(), "Selecciona una fecha de inicio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calcular fecha fin según período
            val calendar = Calendar.getInstance().apply { time = fechaInicio }
            when (periodoSeleccionado.lowercase(Locale.ROOT)) {
                "semanal" -> calendar.add(Calendar.DAY_OF_YEAR, 7)
                "mensual" -> calendar.add(Calendar.MONTH, 1)
                "anual" -> calendar.add(Calendar.YEAR, 1)
            }
            val fechaFin = calendar.time

            val dto = Presupuesto(
                id = 0,
                montoMaximo = monto,
                periodo = periodoSeleccionado,
                fechaInicio = formato.format(fechaInicio!!),
                fechaFin = formato.format(fechaFin),
                categoriaId = categoria.id,
                usuarioId = usuarioId
            )

            lifecycleScope.launch {
                Log.d("DEBUG", "DTO enviado: $dto")

                repository.addPresupuestoRemoto(dto)
                setFragmentResult("presupuesto_guardado", Bundle())
                dismiss()
            }
        }
    }

}
