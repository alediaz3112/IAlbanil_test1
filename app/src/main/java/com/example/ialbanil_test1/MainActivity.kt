package com.example.ialbanil_test1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.example.ialbanil_test1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mostrar selector de motor de análisis antes de cualquier cosa
        com.example.ialbanil_test1.model.showEngineSelector(this) {}

        // Modo inmersivo para pantalla completa
        window.decorView.systemUiVisibility = (
            android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
            or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {
            // Mostrar diálogo para elegir entre cámara y galería
            val options = arrayOf("Abrir cámara", "Importar desde galería")
            android.app.AlertDialog.Builder(this)
                .setTitle("Selecciona una opción")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> {
                            android.util.Log.d("MainActivity", "Opción cámara seleccionada")
                            val currentDestination = navController.currentDestination?.id
                            if (currentDestination == R.id.DisclaimerFragment) {
                                navController.navigate(R.id.action_DisclaimerFragment_to_CameraFragment)
                            } else {
                                android.util.Log.d("MainActivity", "Ya estás en CameraFragment, no se navega")
                            }
                        }
                        1 -> {
                            android.util.Log.d("MainActivity", "Opción galería seleccionada")
                            openGallery()
                        }
                    }
                }
                .show()
        }
    }

    private val PICK_IMAGE_REQUEST = 1001

    private fun openGallery() {
        val intent = android.content.Intent(android.content.Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == android.app.Activity.RESULT_OK) {
            val imageUri = data?.data
            imageUri?.let {
                // Procesar automáticamente la imagen importada
                com.example.ialbanil_test1.model.processImportedImage(it, this)
                android.widget.Toast.makeText(this, "Imagen importada desde galería", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = android.content.Intent(this, com.example.ialbanil_test1.settings.SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}