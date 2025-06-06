package com.almadistefano.finantrack.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.almadistefano.finantrack.R
import com.almadistefano.finantrack.databinding.ItemTransaccionBinding
import com.almadistefano.finantrack.model.TransaccionConCuentaYCategoria
import com.bumptech.glide.Glide


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
            binding.tvCategoria.text = item.categoria.nombre
            binding.tvDescripcion.text = item.transaccion.descripcion
            binding.tvMonto.text = "%.2f €".format(item.transaccion.monto)

            val colorMonto = if (item.transaccion.tipo == "gasto") {
                android.graphics.Color.RED
            } else {
                android.graphics.Color.rgb(0, 150, 136)
            }
            binding.tvMonto.setTextColor(colorMonto)


            val iconoStr = item.categoria.icono

            if (!iconoStr.isNullOrBlank()) {
                val context = binding.root.context
                val resId = context.resources.getIdentifier(iconoStr, "drawable", context.packageName)
                Log.d("DEBUG_ICONO", "Icono recibido: $iconoStr → recurso encontrado: $resId")

                if (resId != 0) {
                    binding.ivIcono.setImageResource(resId)
                } else {
                    Glide.with(context)
                        .load(iconoStr)
                        .placeholder(R.drawable.ic_categoria_default)
                        .into(binding.ivIcono)
                }
            } else {
                binding.ivIcono.setImageResource(R.drawable.ic_categoria_default)
            }

        }

    }
}
