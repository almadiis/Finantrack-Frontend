package com.almadistefano.finantrack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.almadistefano.finantrack.R
import com.almadistefano.finantrack.databinding.ItemFechaBinding
import com.almadistefano.finantrack.databinding.ItemPresupuestoBinding
import com.almadistefano.finantrack.model.PresupuestoConUsuarioYCategoria
import java.text.SimpleDateFormat
import java.util.*

class PresupuestoAdapter(
    private var elementos: List<Any>,
    private val onEliminarClick: (PresupuestoConUsuarioYCategoria) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (elementos[position] is String) TYPE_HEADER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val binding = ItemFechaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding)
        } else {
            val binding = ItemPresupuestoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            PresupuestoViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(elementos[position] as String)
        } else if (holder is PresupuestoViewHolder) {
            holder.bind(elementos[position] as PresupuestoConUsuarioYCategoria)
        }
    }

    override fun getItemCount(): Int = elementos.size

    inner class HeaderViewHolder(private val binding: ItemFechaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fecha: String) {
            binding.tvFecha.text = fecha
        }
    }

    inner class PresupuestoViewHolder(private val binding: ItemPresupuestoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(p: PresupuestoConUsuarioYCategoria) {
            val esIngreso = p.categoria?.nombre?.contains("Ingreso", ignoreCase = true) == true ||
                    p.categoria?.nombre?.contains("Nómina", ignoreCase = true) == true

            val fondoResId = if (esIngreso) R.drawable.bg_card_ingreso else R.drawable.bg_card_gasto
            binding.root.setBackgroundResource(fondoResId)

            binding.tvCategoriaPresupuesto.text = p.categoria?.nombre ?: "Sin categoría"
            binding.tvMontoPresupuesto.text = "%.2f €".format(p.presupuesto.montoMaximo)

            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val fechaInicioFormateada = try {
                val fecha = inputFormat.parse(p.presupuesto.fechaInicio as String)
                outputFormat.format(fecha!!)
            } catch (e: Exception) {
                "Fecha inválida"
            }

            val fechaFinFormateada = try {
                val fecha = inputFormat.parse(p.presupuesto.fechaFin as String)
                outputFormat.format(fecha!!)
            } catch (e: Exception) {
                "Fecha inválida"
            }

            binding.tvPeriodoPresupuesto.text = "Del $fechaInicioFormateada al $fechaFinFormateada"
            binding.btnEliminarPresupuesto.setOnClickListener {
                onEliminarClick(p)
            }
        }
    }


    fun updateData(presupuestos: List<PresupuestoConUsuarioYCategoria>) {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val agrupados = presupuestos.groupBy {
            try {
                val date = inputFormat.parse(it.presupuesto.fechaInicio as String)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                "Fecha inválida"
            }
        }

        val lista = mutableListOf<Any>()
        agrupados.toSortedMap().forEach { (fecha, items) ->
            lista.add(fecha)
            lista.addAll(items)
        }
        elementos = lista
        notifyDataSetChanged()
    }
}
