package com.almadistefano.finantrack.ui.fragments.transacciones

import RemoteDataSource
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.R
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Categoria
import com.almadistefano.finantrack.model.Transaccion
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.setFragmentResult
import com.almadistefano.finantrack.data.FinantrackAPI.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class AddTransaccionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var repository: Repository
    private var fechaSeleccionada: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private var listaCategorias: List<Categoria> = emptyList()

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
        return inflater.inflate(R.layout.bottomsheet_add_transaccion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tipoGroup = view.findViewById<RadioGroup>(R.id.rgTipo)
        val montoEdit = view.findViewById<EditText>(R.id.etMonto)
        val descEdit = view.findViewById<EditText>(R.id.etDescripcion)
        val fechaBtn = view.findViewById<Button>(R.id.btnFecha)
        val guardarBtn = view.findViewById<Button>(R.id.btnGuardar)
        val spinnerCategoria = view.findViewById<Spinner>(R.id.spinnerCategoria)

        // Obtener categorías y llenar spinner
        lifecycleScope.launch {
            repository.getCategorias().collect { categorias ->
                listaCategorias = categorias
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categorias.map { it.nombre })
                spinnerCategoria.adapter = adapter
            }
        }

        // Fecha
        fechaBtn.text = fechaSeleccionada
        fechaBtn.setOnClickListener {
            val calendario = Calendar.getInstance()
            val picker = DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    val mes = m + 1
                    fechaSeleccionada = String.format("%04d-%02d-%02d", y, mes, d)
                    fechaBtn.text = fechaSeleccionada
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            picker.show()
        }

        guardarBtn.setOnClickListener {
            val tipo = if (tipoGroup.checkedRadioButtonId == R.id.rbGasto) "gasto" else "ingreso"
            val monto = montoEdit.text.toString().toDoubleOrNull()
            val descripcion = descEdit.text.toString().trim()
            val categoriaSeleccionada = listaCategorias.getOrNull(spinnerCategoria.selectedItemPosition)
            val categoriaId = categoriaSeleccionada?.id

            val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val cuentaId = prefs.getInt("cuenta_id", -1)

            if (monto == null || descripcion.isBlank() || categoriaId == null || cuentaId == -1) {
                Toast.makeText(requireContext(), "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transaccion = Transaccion(
                id = 0, // autoGenerate si Room está configurado así
                monto = monto,
                fecha = fechaSeleccionada,
                descripcion = descripcion,
                tipo = tipo,
                categoriaId = categoriaId,
                cuentaId = cuentaId
            )


            lifecycleScope.launch {
                repository.addTransaccion(transaccion)
                withContext(Dispatchers.Main) {
                    setFragmentResult("transaccion_guardada", Bundle())
                    dismiss()
                }
            }
        }
    }
}
