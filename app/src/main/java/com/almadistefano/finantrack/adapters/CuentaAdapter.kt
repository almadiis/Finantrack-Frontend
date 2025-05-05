package com.almadistefano.finantrack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.almadistefano.finantrack.databinding.ItemCuentaBinding
import com.almadistefano.finantrack.model.Cuenta


class CuentaAdapter(private var cuentas: List<Cuenta>) :
    RecyclerView.Adapter<CuentaAdapter.CuentaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuentaViewHolder {
        val binding = ItemCuentaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CuentaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CuentaViewHolder, position: Int) {
        val cuenta = cuentas[position]
        holder.bind(cuenta)
    }

    override fun getItemCount(): Int = cuentas.size

    inner class CuentaViewHolder(private val binding: ItemCuentaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cuenta: Cuenta) {
            binding.nombreCuenta.text = cuenta.nombre
            binding.saldoCuenta.text = cuenta.saldo.toString()
        }
    }

    fun updateData(newCuentas: List<Cuenta>) {
        val diffCallback = CuentaDiffCallback(cuentas, newCuentas)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        cuentas = newCuentas
        diffResult.dispatchUpdatesTo(this)
    }
}
class CuentaDiffCallback(
    private val oldList: List<Cuenta>,
    private val newList: List<Cuenta>
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