package com.example.appsensores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsuariosAdapter(
    private val usuarios: List<GestionUsuarios.Usuario>,
    private val onEditar: (GestionUsuarios.Usuario)->Unit,
) : RecyclerView.Adapter<UsuariosAdapter.UsuarioVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario,parent,false)
        return UsuarioVH(view)
    }

    override fun onBindViewHolder(holder: UsuarioVH, position: Int) {
        val u = usuarios[position]
        holder.tvNombre.text = u.nombre
        holder.tvEmail.text = u.email
        holder.tvRol.text = u.rol
        holder.tvEstado.text = u.estado

        holder.btnEditar.setOnClickListener { onEditar(u) }
    }

    override fun getItemCount(): Int = usuarios.size

    class UsuarioVH(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvRol: TextView = itemView.findViewById(R.id.tvRol)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
    }
}
