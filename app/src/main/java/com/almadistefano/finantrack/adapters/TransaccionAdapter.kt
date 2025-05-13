package com.almadistefano.finantrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.almadistefano.finantrack.R
import com.almadistefano.finantrack.databinding.ItemTransaccionBinding
import com.almadistefano.finantrack.model.TransaccionConCuentaYCategoria


class TransaccionAdapter(
    private var items: List<Any>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_FECHA = 0
        private const val VIEW_TYPE_TRANSACCION = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is String -> VIEW_TYPE_FECHA
            is TransaccionConCuentaYCategoria -> VIEW_TYPE_TRANSACCION
            else -> throw IllegalArgumentException("Tipo desconocido")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_FECHA) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fecha, parent, false)
            FechaViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_transaccion, parent, false)
            TransaccionViewHolder(view)
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is String -> (holder as FechaViewHolder).bind(item)
            is TransaccionConCuentaYCategoria -> (holder as TransaccionViewHolder).bind(item)
        }
    }

    fun updateData(newItems: List<Any>) {
        items = newItems
        notifyDataSetChanged()
    }

    class FechaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(fecha: String) {
            itemView.findViewById<TextView>(R.id.tvFecha).text = fecha
        }
    }

    class TransaccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTransaccionBinding.bind(itemView)

        fun bind(item: TransaccionConCuentaYCategoria) {
            itemView.findViewById<TextView>(R.id.tvCategoria).text = item.categoria.nombre
            itemView.findViewById<TextView>(R.id.tvDescripcion).text = item.transaccion.descripcion
            itemView.findViewById<TextView>(R.id.tvMonto).text = "%.2f €".format(item.transaccion.monto)

            val iconView = itemView.findViewById<ImageView>(R.id.ivIcono)
            iconView.setImageResource(R.drawable.ic_categoria_default) // puedes mejorar esto más adelante
            val categoriaNombre = item.categoria.nombre
            val monto = item.transaccion.monto
            val descripcion = item.transaccion.descripcion
            val tipo = item.transaccion.tipo

            binding.tvCategoria.text = categoriaNombre
            binding.tvDescripcion.text = descripcion
            binding.tvMonto.text = "%.2f €".format(monto)

            // Color según tipo
            val colorMonto = if (tipo == "gasto") {
                android.graphics.Color.RED
            } else {
                android.graphics.Color.rgb(0, 150, 136) // verde azulado
            }
            binding.tvMonto.setTextColor(colorMonto)

            // Asignar icono según categoría
            val iconRes = when (categoriaNombre.lowercase()) {
                "comida" -> R.drawable.dieta
                "ocio" -> R.drawable.actividades_extracurriculares
                "salud" -> R.drawable.salud_mental
                "compras" -> R.drawable.carrito_de_compras
                "gasolina" -> R.drawable.bomba_de_gas
                "gym" -> R.drawable.capacitacion
                "nómina" -> R.drawable.nomina_de_sueldos
                else -> R.drawable.ic_categoria_default
            }
            binding.ivIcono.setImageResource(iconRes)

        }
    }
}
