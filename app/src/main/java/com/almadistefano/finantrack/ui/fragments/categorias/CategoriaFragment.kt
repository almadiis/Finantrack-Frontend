package com.almadistefano.finantrack.ui.fragments.categorias

import RemoteDataSource
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.almadistefano.finantrack.FinantrackApplication
import com.almadistefano.finantrack.R
import com.almadistefano.finantrack.adapters.CategoriaAdapter
import com.almadistefano.finantrack.data.LocalDataSource
import com.almadistefano.finantrack.data.Repository
import com.almadistefano.finantrack.model.Categoria
import kotlinx.coroutines.launch


class CategoriaFragment : Fragment() {

    private lateinit var repository: Repository
    private lateinit var viewModel: CategoriaViewModel
    private lateinit var adapter: CategoriaAdapter

    private var colorSeleccionado: String = "#000000"
    private var iconoSeleccionado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = requireActivity().application as FinantrackApplication
        val userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getInt("usuario_id", -1)

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

        viewModel = ViewModelProvider(this, CategoriaViewModelFactory(repository, userId))[CategoriaViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_categoria, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCategorias)
        val etNombre = view.findViewById<EditText>(R.id.etNombreCategoria)
        val radioGasto = view.findViewById<RadioButton>(R.id.radioGasto)
        val btnIcono = view.findViewById<Button>(R.id.btnSeleccionarIcono)
        val layoutColores = view.findViewById<LinearLayout>(R.id.layoutColores)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)
        val webView = view.findViewById<WebView>(R.id.webViewIconos)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CategoriaAdapter(emptyList()) { categoria ->
            lifecycleScope.launch {
                repository.eliminarCategoria(categoria)
                Toast.makeText(requireContext(), "Eliminada: ${categoria.nombre}", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.categorias.collect {
                adapter.update(it)
            }
        }

        listOf("#E53935", "#FB8C00", "#FDD835", "#43A047", "#1E88E5", "#8E24AA").forEach { hex ->
            val viewColor = View(requireContext()).apply {
                val size = resources.getDimensionPixelSize(R.dimen.color_circle_size)
                layoutParams = LinearLayout.LayoutParams(size, size).apply { setMargins(12, 12, 12, 12) }
                background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(Color.parseColor(hex)) }
                setOnClickListener {
                    colorSeleccionado = hex
                    Toast.makeText(requireContext(), "Color: $hex", Toast.LENGTH_SHORT).show()
                }
            }
            layoutColores.addView(viewColor)
        }

        btnIcono.setOnClickListener {
            webView.visibility = View.VISIBLE
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.addJavascriptInterface(object {
                @JavascriptInterface
                fun onIconSelected(iconName: String) {
                    requireActivity().runOnUiThread {
                        iconoSeleccionado = iconName
                        btnIcono.text = "Icono: $iconoSeleccionado"
                        webView.visibility = View.GONE
                    }
                }
            }, "Android")
            webView.loadUrl("file:///android_asset/icon-picker.html")
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val tipo = if (radioGasto.isChecked) "gasto" else "ingreso"
            val userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getInt("usuario_id", -1)

            if (nombre.isBlank() || iconoSeleccionado.isBlank()) {
                Toast.makeText(requireContext(), "Faltan campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nueva = Categoria(0, nombre, tipo, iconoSeleccionado, colorSeleccionado, userId)
            viewModel.agregarCategoria(nueva)
            Toast.makeText(requireContext(), "Guardado", Toast.LENGTH_SHORT).show()
            etNombre.setText("")
            btnIcono.text = "Seleccionar icono"
            iconoSeleccionado = ""
            colorSeleccionado = "#000000"
            Log.d("CategoriaFragment", "Usuario ID actual: $userId")



        }
    }
}

