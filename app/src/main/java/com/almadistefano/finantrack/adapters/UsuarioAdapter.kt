package com.almadistefano.finantrack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.almadistefano.finantrack.databinding.ItemUsuarioBinding
import com.almadistefano.finantrack.model.Usuario

class UsuarioAdapter(private var usuarios: List<Usuario>) :
    RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = ItemUsuarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsuarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.bind(usuario)
    }

    override fun getItemCount(): Int = usuarios.size

    inner class UsuarioViewHolder(private val binding: ItemUsuarioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(usuario: Usuario) {
            binding.nombreUsuario.text = usuario.nombre
            binding.correoUsuario.text = usuario.correo
        }
    }

    fun updateData(newUsuarios: List<Usuario>) {
        usuarios = newUsuarios
        notifyDataSetChanged()
    }
}
