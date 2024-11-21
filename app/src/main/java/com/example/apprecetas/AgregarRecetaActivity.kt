package com.example.apprecetas

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AgregarRecetaActivity : AppCompatActivity() {

    private lateinit var imagenReceta: ImageView
    private lateinit var etNombreReceta: TextInputEditText
    private lateinit var etIngredientes: TextInputEditText
    private lateinit var etPasos: TextInputEditText
    private var imagenUri: Uri? = null
    private var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_receta)
        enableEdgeToEdge()
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        imagenReceta = findViewById(R.id.ivImagenReceta)
        etNombreReceta = findViewById(R.id.etNombreReceta)
        etIngredientes = findViewById(R.id.etIngredientes)
        etPasos = findViewById(R.id.etPasos)
    }

    private fun setupListeners() {
        findViewById<Button>(R.id.btnSeleccionarImagen).setOnClickListener {
            if (checkStoragePermission()) {
                abrirGaleria()
            }
        }

        findViewById<Button>(R.id.btnTomarFoto).setOnClickListener {
            if (checkCameraPermission()) {
                tomarFoto()
            }
        }

        findViewById<Button>(R.id.btnGuardarReceta).setOnClickListener {
            guardarReceta()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> true
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                    Toast.makeText(
                        this,
                        "Se necesita acceso a las imágenes para seleccionar fotos de recetas",
                        Toast.LENGTH_LONG
                    ).show()
                    false
                }
                else -> {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        PERMISSION_STORAGE
                    )
                    false
                }
            }
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> true
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    Toast.makeText(
                        this,
                        "Se necesita acceso al almacenamiento para seleccionar fotos de recetas",
                        Toast.LENGTH_LONG
                    ).show()
                    false
                }
                else -> {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_STORAGE
                    )
                    false
                }
            }
        }
    }

    private fun checkCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CAMERA
            )
            return false
        }
        return true
    }

    private fun abrirGaleria() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        try {
            startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), REQUEST_GALLERY)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "No se pudo abrir la galería: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun tomarFoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                photoFile = createImageFile()
                if (photoFile != null) {
                    imagenUri = FileProvider.getUriForFile(
                        this,
                        "${applicationContext.packageName}.provider",
                        photoFile!!
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri)
                    startActivityForResult(intent, REQUEST_CAMERA)
                } else {
                    Toast.makeText(this, "No se pudo crear el archivo para la foto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_$timeStamp"
            val storageDir = getExternalFilesDir(null)
            File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (e: Exception) {
            Toast.makeText(this, "Error al crear archivo de imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun guardarReceta() {
        val nombre = etNombreReceta.text.toString()
        val ingredientes = etIngredientes.text.toString()
        val pasos = etPasos.text.toString()

        if (nombre.isEmpty() || ingredientes.isEmpty() || pasos.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        imagenUri?.let {
            android.util.Log.d("AgregarRecetaActivity", "URL de la imagen seleccionada: $it")
        } ?: android.util.Log.d("AgregarRecetaActivity", "No se seleccionó ninguna imagen.")

        val resultIntent = Intent().apply {
            putExtra(MainActivity.EXTRA_NOMBRE, nombre)
            putExtra(MainActivity.EXTRA_INGREDIENTES, ingredientes)
            putExtra(MainActivity.EXTRA_PASOS, pasos)
            putExtra(MainActivity.EXTRA_IMAGEN_URI, imagenUri?.toString())
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun resizeImage(originalFile: File?): Uri? {
        if (originalFile == null || !originalFile.exists()) {
            Toast.makeText(this, "Error: Archivo de imagen no encontrado", Toast.LENGTH_SHORT).show()
            return null
        }

        val bitmap = BitmapFactory.decodeFile(originalFile.path)
        if (bitmap == null) {
            Toast.makeText(this, "Error: No se pudo decodificar la imagen", Toast.LENGTH_SHORT).show()
            return null
        }

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true)
        val resizedFile = File(originalFile.parent, "resized_${originalFile.name}")

        return try {
            val outputStream = FileOutputStream(resizedFile)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            outputStream.flush()
            outputStream.close()
            Uri.fromFile(resizedFile)
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar imagen redimensionada: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            try {
                when (requestCode) {
                    REQUEST_GALLERY -> {
                        data?.data?.let { uri ->
                            imagenUri = saveImageToInternalStorage(uri)
                            imagenReceta.setImageURI(imagenUri)
                        } ?: run {
                            Toast.makeText(this, "No se pudo obtener la imagen", Toast.LENGTH_SHORT).show()
                        }
                    }
                    REQUEST_CAMERA -> {
                        if (photoFile != null) {
                            val resizedImageUri = resizeImage(photoFile)
                            if (resizedImageUri != null) {
                                imagenUri = resizedImageUri
                                imagenReceta.setImageURI(imagenUri)
                            } else {
                                Toast.makeText(this, "Error al redimensionar la imagen", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Error al procesar la imagen: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun saveImageToInternalStorage(uri: Uri): Uri? {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        // Generar un nombre único para la imagen
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "receta_imagen_$timeStamp.jpg"

        val file = File(getExternalFilesDir(null), fileName)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return Uri.fromFile(file)
    }


    companion object {
        private const val PERMISSION_STORAGE = 1
        private const val PERMISSION_CAMERA = 2
        private const val REQUEST_GALLERY = 101
        private const val REQUEST_CAMERA = 102
    }
}