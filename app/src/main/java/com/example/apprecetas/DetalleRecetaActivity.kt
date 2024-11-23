package com.example.apprecetas

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import android.util.Log
import androidx.activity.enableEdgeToEdge

class DetalleRecetaActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_receta)
        enableEdgeToEdge()
        val nombre = intent.getStringExtra(MainActivity.EXTRA_NOMBRE)
        val ingredientes = intent.getStringExtra(MainActivity.EXTRA_INGREDIENTES)
        val pasos = intent.getStringExtra(MainActivity.EXTRA_PASOS)
        val imagenUriString = intent.getStringExtra(MainActivity.EXTRA_IMAGEN_URI)

        findViewById<TextView>(R.id.tvTituloReceta).text = nombre
        findViewById<TextView>(R.id.tvIngredientes).text = ingredientes
        findViewById<TextView>(R.id.tvPasos).text = pasos

        val ivDetalleReceta = findViewById<ImageView>(R.id.ivDetalleReceta)

        // Display Toast to verify if we are reaching this point
        Toast.makeText(this, "Receta cargada: $nombre", Toast.LENGTH_SHORT).show()

        // Check for permissions if the device is Android 13 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Solicitando permiso para acceder a imágenes", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                Toast.makeText(this, "Permiso ya concedido. Cargando imagen.", Toast.LENGTH_SHORT).show()
                loadImage(imagenUriString, ivDetalleReceta)
            }
        } else {
            Toast.makeText(this, "Android < 13, Cargando imagen.", Toast.LENGTH_SHORT).show()
            loadImage(imagenUriString, ivDetalleReceta)
        }
    }

    private fun loadImage(imagenUriString: String?, ivDetalleReceta: ImageView) {
        imagenUriString?.let { uriString ->
            Log.d("DetalleRecetaActivity", "Cargando imagen desde URI: $uriString")
            Toast.makeText(this, "Cargando imagen desde URI: $uriString", Toast.LENGTH_SHORT).show()
            val uri = Uri.parse(uriString)

            // Usar Glide para cargar la imagen desde el URI
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(ivDetalleReceta)
        } ?: run {
            Toast.makeText(this, "La URI de la imagen está vacía.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido para acceder a imágenes.", Toast.LENGTH_SHORT).show()
                Log.d("DetalleRecetaActivity", "Permiso de acceso a imágenes concedido.")
                // Retry loading the image after permission is granted
                val imagenUriString = intent.getStringExtra(MainActivity.EXTRA_IMAGEN_URI)
                val ivDetalleReceta = findViewById<ImageView>(R.id.ivDetalleReceta)
                loadImage(imagenUriString, ivDetalleReceta)
            } else {
                Toast.makeText(this, "Permiso denegado para acceder a imágenes.", Toast.LENGTH_SHORT).show()
                Log.d("DetalleRecetaActivity", "Permiso de acceso a imágenes denegado.")
            }
        }
    }
}