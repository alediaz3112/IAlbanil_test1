package com.example.ialbanil_test1.model

// Familias de elementos detectados por la cámara
sealed class ElementFamily(val name: String) {
    object Structure : ElementFamily("Estructura") // pared, suelo, techo, aberturas, ladrillos, columnas, vigas
    object Decoration : ElementFamily("Decoración") // pintura, revestimientos, cerámicos, muebles grandes
    object NotRelevant : ElementFamily("No relevante") // personas, animales, objetos pequeños
}

// Tipos de elementos detectables
enum class ElementType(val family: ElementFamily) {
    PARED(ElementFamily.Structure),
    SUELO(ElementFamily.Structure),
    TECHO(ElementFamily.Structure),
    ABERTURA(ElementFamily.Structure),
    LADRILLO(ElementFamily.Structure),
    COLUMNA(ElementFamily.Structure),
    VIGA(ElementFamily.Structure),
    PINTURA(ElementFamily.Decoration),
    REVESTIMIENTO(ElementFamily.Decoration),
    CERAMICO(ElementFamily.Decoration),
    MUEBLE_GRANDE(ElementFamily.Decoration),
    PERSONA(ElementFamily.NotRelevant),
    ANIMAL(ElementFamily.NotRelevant),
    OBJETO_CHICO(ElementFamily.NotRelevant),
    IMAGEN_GALERIA(ElementFamily.NotRelevant) // Nuevo tipo para imágenes importadas
}

// Función para obtener la familia desde el tipo detectado
fun ElementType.getFamilyName(): String {
    return this.family.name
}

// Estructura base para ML Kit
object MLKitAnalyzer {
    fun analyzeImage(uri: android.net.Uri, context: android.content.Context) {
        android.widget.Toast.makeText(context, "Análisis con ML Kit", android.widget.Toast.LENGTH_SHORT).show()
    }
}

// Estructura base para TensorFlow Lite
object TFLiteAnalyzer {
    fun analyzeImage(uri: android.net.Uri, context: android.content.Context) {
        android.widget.Toast.makeText(context, "Análisis con TensorFlow Lite", android.widget.Toast.LENGTH_SHORT).show()
    }
}

// Función para elegir el motor de análisis
enum class AnalyzerEngine {
    ML_KIT, TFLITE
}

private var selectedEngine: AnalyzerEngine? = null

fun getSelectedEngine(context: android.content.Context): AnalyzerEngine {
    val prefs = context.getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
    val value = prefs.getString("engine", null)
    return when (value) {
        "ML_KIT" -> AnalyzerEngine.ML_KIT
        "TFLITE" -> AnalyzerEngine.TFLITE
        else -> AnalyzerEngine.TFLITE // valor por defecto ahora es TensorFlow Lite
    }
}

fun setSelectedEngine(context: android.content.Context, engine: AnalyzerEngine) {
    val prefs = context.getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
    prefs.edit().putString("engine", engine.name).apply()
    selectedEngine = engine
}

fun showEngineSelector(context: android.content.Context, onSelected: (AnalyzerEngine) -> Unit) {
    val options = arrayOf("ML Kit", "TensorFlow Lite")
    android.app.AlertDialog.Builder(context)
        .setTitle("Selecciona motor de análisis")
        .setItems(options) { _, which ->
            val engine = if (which == 0) AnalyzerEngine.ML_KIT else AnalyzerEngine.TFLITE
            setSelectedEngine(context, engine)
            onSelected(engine)
        }
        .setCancelable(false)
        .show()
}

fun processImportedImage(uri: android.net.Uri, context: android.content.Context) {
    val tipo = ElementType.IMAGEN_GALERIA
    val familia = tipo.getFamilyName()
    val engine = getSelectedEngine(context)
    when (engine) {
        AnalyzerEngine.ML_KIT -> MLKitAnalyzer.analyzeImage(uri, context)
        AnalyzerEngine.TFLITE -> TFLiteAnalyzer.analyzeImage(uri, context)
    }
}
