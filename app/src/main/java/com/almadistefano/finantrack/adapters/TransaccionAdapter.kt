package com.almadistefano.finantrack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.almadistefano.finantrack.databinding.ItemTransaccionBinding
import com.almadistefano.finantrack.model.Transaccion

class TransaccionAdapter(private var transacciones: List<Transaccion>) :
    RecyclerView.Adapter<TransaccionAdapter.TransaccionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val binding = ItemTransaccionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransaccionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        val transaccion = transacciones[position]
        holder.bind(transaccion)
    }

    override fun getItemCount(): Int = transacciones.size

    inner class TransaccionViewHolder(private val binding: ItemTransaccionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaccion: Transaccion) {
            binding.descripcionTransaccion.text = transaccion.descripcion
            binding.montoTransaccion.text = transaccion.monto.toString()
            binding.fechaTransaccion.text = transaccion.fecha.toString()
        }
    }

    fun updateData(newTransacciones: List<Transaccion>) {
        val diffCallback = TransaccionDiffCallback(transacciones, newTransacciones)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        transacciones = newTransacciones
        diffResult.dispatchUpdatesTo(this)
    }
}

class TransaccionDiffCallback(
    private val oldList: List<Transaccion>,
    private val newList: List<Transaccion>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
