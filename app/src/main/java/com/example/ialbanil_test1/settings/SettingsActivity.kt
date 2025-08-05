package com.example.ialbanil_test1.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ialbanil_test1.model.AnalyzerEngine
import com.example.ialbanil_test1.model.getSelectedEngine
import com.example.ialbanil_test1.model.setSelectedEngine
import com.example.ialbanil_test1.model.showEngineSelector
import android.widget.TextView
import android.widget.Button
import android.widget.LinearLayout
import android.view.ViewGroup
import android.view.Gravity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val engineLabel = TextView(this)
        engineLabel.text = "Motor de análisis actual: ${getSelectedEngine(this)}"
        engineLabel.textSize = 18f
        engineLabel.gravity = Gravity.CENTER
        layout.addView(engineLabel)

        val changeEngineButton = Button(this)
        changeEngineButton.text = "Cambiar motor de análisis"
        changeEngineButton.setOnClickListener {
            showEngineSelector(this) { engine ->
                setSelectedEngine(this, engine)
                engineLabel.text = "Motor de análisis actual: $engine"
            }
        }
        layout.addView(changeEngineButton)

        // Ejemplo de otras opciones de configuración
        val themeLabel = TextView(this)
        themeLabel.text = "Tema: Claro/Oscuro"
        themeLabel.textSize = 16f
        themeLabel.gravity = Gravity.CENTER
        layout.addView(themeLabel)

        val languageLabel = TextView(this)
        languageLabel.text = "Idioma: Español"
        languageLabel.textSize = 16f
        languageLabel.gravity = Gravity.CENTER
        layout.addView(languageLabel)

        val unitsLabel = TextView(this)
        unitsLabel.text = "Unidades: Métrico"
        unitsLabel.textSize = 16f
        unitsLabel.gravity = Gravity.CENTER
        layout.addView(unitsLabel)

        val resetButton = Button(this)
        resetButton.text = "Restablecer configuración"
        resetButton.setOnClickListener {
            setSelectedEngine(this, AnalyzerEngine.ML_KIT)
            engineLabel.text = "Motor de análisis actual: ML_KIT"
            // Aquí podrías restablecer otras preferencias
        }
        layout.addView(resetButton)

        setContentView(layout)
    }
}
