package com.almadistefano.finantrack.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.almadistefano.finantrack.R
import com.almadistefano.finantrack.model.Categoria
import com.bumptech.glide.Glide

class CategoriaAdapter(
    private var lista: List<Categoria>,
    private val onDelete: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val ivIcono: ImageView = itemView.findViewById(R.id.ivIcono)
        val viewColor: View = itemView.findViewById(R.id.viewColor)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = lista[position]
        holder.tvNombre.text = categoria.nombre

        Glide.with(holder.itemView)
            .load(categoria.icono)
            .placeholder(R.drawable.ic_categoria)
            .into(holder.ivIcono)

        holder.viewColor.setBackgroundColor(Color.parseColor(categoria.colorHex ?: "#000000"))

        holder.btnEliminar.setOnClickListener {
            onDelete(categoria)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun update(newList: List<Categoria>) {
        lista = newList
        notifyDataSetChanged()
    }
}



