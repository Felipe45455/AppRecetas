package com.example.apprecetas

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

class DetalleRecetaActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_receta)
        enableEdgeToEdge()

        // Configurar Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarDetalle)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val nombre = intent.getStringExtra(MainActivity.EXTRA_NOMBRE)
        val ingredientes = intent.getStringExtra(MainActivity.EXTRA_INGREDIENTES)
        val pasos = intent.getStringExtra(MainActivity.EXTRA_PASOS)
        val imagenUriString = intent.getStringExtra(MainActivity.EXTRA_IMAGEN_URI)

        findViewById<TextView>(R.id.tvTituloReceta).text = nombre
        findViewById<TextView>(R.id.tvIngredientes).text = ingredientes
        findViewById<TextView>(R.id.tvPasos).text = pasos

        val ivDetalleReceta = findViewById<ImageView>(R.id.ivDetalleReceta)

        // Comprobación de permisos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                loadImage(imagenUriString, ivDetalleReceta)
            }
        } else {
            loadImage(imagenUriString, ivDetalleReceta)
        }
    }

    private fun loadImage(imagenUriString: String?, ivDetalleReceta: ImageView) {
        imagenUriString?.let { uriString ->
            val uri = Uri.parse(uriString)
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(ivDetalleReceta)
        } ?: Toast.makeText(this, "La URI de la imagen está vacía.", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detalle, menu)
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val imagenUriString = intent.getStringExtra(MainActivity.EXTRA_IMAGEN_URI)
                val ivDetalleReceta = findViewById<ImageView>(R.id.ivDetalleReceta)
                loadImage(imagenUriString, ivDetalleReceta)
            } else {
                Toast.makeText(this, "Permiso denegado para acceder a imágenes.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Finalizar la actividad y regresar a la anterior
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

