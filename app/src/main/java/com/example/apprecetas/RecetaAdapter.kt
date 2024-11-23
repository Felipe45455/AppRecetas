package com.example.apprecetas

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecetaAdapter(
    private val recetas: MutableList<Receta>,
    private val onItemClick: (Receta) -> Unit,
    private val onEditClick: (Receta) -> Unit,
    private val onDeleteClick: (Receta) -> Unit
) : RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder>() {

    class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.tvNombreReceta)
        val imagenReceta: ImageView = itemView.findViewById(R.id.ivReceta)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receta, parent, false)
        return RecetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        val receta = recetas[position]
        holder.nombreTextView.text = receta.nombre

        receta.imagenUri?.let { uri ->
            Glide.with(holder.imagenReceta.context)
                .load(uri)
                .centerCrop()
                .into(holder.imagenReceta)
        }

        holder.itemView.setOnClickListener { onItemClick(receta) }

        holder.btnEliminar.setOnClickListener {
            onDeleteClick(receta)
        }
    }

    override fun getItemCount() = recetas.size

    fun removeReceta(receta: Receta) {
        val position = recetas.indexOf(receta)
        if (position != -1) {
            recetas.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateReceta(receta: Receta) {
        val position = recetas.indexOf(receta)
        if (position != -1) {
            recetas[position] = receta
            notifyItemChanged(position)
        }
    }
}


