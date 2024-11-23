package com.example.apprecetas

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecetaAdapter
    private val listaRecetas = mutableListOf<Receta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        recyclerView = findViewById(R.id.rvRecetas)
        val fabAgregarReceta = findViewById<FloatingActionButton>(R.id.fabAgregarReceta)

        setupRecyclerView()

        fabAgregarReceta.setOnClickListener {
            startActivityForResult(
                Intent(this, AgregarRecetaActivity::class.java),
                AGREGAR_RECETA_REQUEST
            )
        }
    }

    private fun setupRecyclerView() {
        adapter = RecetaAdapter(listaRecetas) { receta ->
            val intent = Intent(this, DetalleRecetaActivity::class.java).apply {
                putExtra(EXTRA_NOMBRE, receta.nombre)
                putExtra(EXTRA_INGREDIENTES, receta.ingredientes)
                putExtra(EXTRA_PASOS, receta.pasos)
                putExtra(EXTRA_IMAGEN_URI, receta.imagenUri)
            }
            startActivity(intent)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = this@MainActivity.adapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AGREGAR_RECETA_REQUEST && resultCode == RESULT_OK) {
            data?.let { intent ->
                val nombre = intent.getStringExtra(EXTRA_NOMBRE) ?: return@let
                val ingredientes = intent.getStringExtra(EXTRA_INGREDIENTES) ?: return@let
                val pasos = intent.getStringExtra(EXTRA_PASOS) ?: return@let
                val imagenUri = intent.getStringExtra(EXTRA_IMAGEN_URI)

                val nuevaReceta = Receta(nombre, ingredientes, pasos, imagenUri)
                listaRecetas.add(nuevaReceta)
                adapter.notifyItemInserted(listaRecetas.size - 1)
            }
        }
    }

    companion object {
        const val AGREGAR_RECETA_REQUEST = 1
        const val EXTRA_NOMBRE = "extra_nombre"
        const val EXTRA_INGREDIENTES = "extra_ingredientes"
        const val EXTRA_PASOS = "extra_pasos"
        const val EXTRA_IMAGEN_URI = "extra_imagen_uri"
    }
}