package com.almadistefano.finantrack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.almadistefano.finantrack.databinding.ItemPresupuestoBinding
import com.almadistefano.finantrack.model.Presupuesto
import com.almadistefano.finantrack.model.PresupuestoConCategoria

class PresupuestoAdapter(private var presupuestos: List<PresupuestoConCategoria>) :
    RecyclerView.Adapter<PresupuestoAdapter.PresupuestoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresupuestoViewHolder {
        val binding = ItemPresupuestoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PresupuestoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PresupuestoViewHolder, position: Int) {
        val presupuesto = presupuestos[position]
        holder.bind(presupuesto)
    }

    override fun getItemCount(): Int = presupuestos.size

    inner class PresupuestoViewHolder(private val binding: ItemPresupuestoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(presupuestoConCategoria: PresupuestoConCategoria) {
            val presupuesto = presupuestoConCategoria.presupuesto
            val categoria = presupuestoConCategoria.categoria

            binding.nombrePresupuesto.text = categoria?.nombre
            binding.montoPresupuesto.text = presupuesto.montoMaximo.toString()
        }

    }

    fun updateData(newPresupuestos: List<PresupuestoConCategoria>) {
        val diffCallback = PresupuestoDiffCallback(presupuestos, newPresupuestos)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        presupuestos = newPresupuestos
        diffResult.dispatchUpdatesTo(this)
    }
}

class PresupuestoDiffCallback(
    private val oldList: List<PresupuestoConCategoria>,
    private val newList: List<PresupuestoConCategoria>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].presupuesto.id == newList[newItemPosition].presupuesto.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}

