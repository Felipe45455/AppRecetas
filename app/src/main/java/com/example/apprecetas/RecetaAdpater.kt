package com.example.apprecetas

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecetaAdapter(
    private val recetas: List<Receta>,
    private val onItemClick: (Receta) -> Unit
) : RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder>() {

    class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.tvNombreReceta)
        val imagenReceta: ImageView = itemView.findViewById(R.id.ivReceta)
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
            // Agregar depuración para verificar la URI
            Log.d("RecetaAdapter", "Cargando imagen con URI: $uri")

            try {
                // Intentar cargar la imagen con Glide
                Glide.with(holder.imagenReceta.context)
                    .load(uri)
                    .centerCrop()
                    .into(holder.imagenReceta)
                Log.d("RecetaAdapter", "Imagen cargada exitosamente con Glide")
            } catch (e: Exception) {
                Log.e("RecetaAdapter", "Error al cargar la imagen con Glide: ${e.message}")
            }
        } ?: Log.d("RecetaAdapter", "No hay URI de imagen para esta receta")

        // Configuración para el clic en el ítem
        holder.itemView.setOnClickListener { onItemClick(receta) }
    }

    override fun getItemCount() = recetas.size
}
