package com.example.ialbanil_test1.tensor

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer

class TFLiteFamilyClassifier(context: Context) {
    private val interpreter: Interpreter
    private val labels: List<String>

    init {
        // Carga el modelo .tflite desde assets (ejemplo: model_families.tflite)
        val modelBuffer = context.assets.open("model_families.tflite").readBytes()
        val byteBuffer = java.nio.ByteBuffer.allocateDirect(modelBuffer.size)
        byteBuffer.order(java.nio.ByteOrder.nativeOrder())
        byteBuffer.put(modelBuffer)
        byteBuffer.rewind()
        interpreter = Interpreter(byteBuffer)
        // Carga las etiquetas desde assets (ejemplo: labels_families.txt)
        labels = context.assets.open("labels_families.txt").bufferedReader().readLines()
    }

    fun classify(bitmap: Bitmap): String {
        // Convierte el Bitmap a un ByteBuffer compatible con TensorFlow Lite
        val input = bitmapToByteBuffer(bitmap)
        val output = Array(1) { FloatArray(labels.size) }
        interpreter.run(input, output)
        val maxIdx = output[0].indices.maxByOrNull { output[0][it] } ?: 0
        return labels[maxIdx]
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): java.nio.ByteBuffer {
        val inputSize = bitmap.width * bitmap.height * 4
        val byteBuffer = java.nio.ByteBuffer.allocateDirect(inputSize)
        byteBuffer.order(java.nio.ByteOrder.nativeOrder())
        val intValues = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (pixelValue in intValues) {
            byteBuffer.putFloat(((pixelValue shr 16) and 0xFF) / 255.0f)
            byteBuffer.putFloat(((pixelValue shr 8) and 0xFF) / 255.0f)
            byteBuffer.putFloat((pixelValue and 0xFF) / 255.0f)
            byteBuffer.putFloat(((pixelValue shr 24) and 0xFF) / 255.0f)
        }
        return byteBuffer
    }

    fun classifyWithLabel(bitmap: Bitmap, extraInfo: String? = null): Bitmap {
        val label = classify(bitmap)
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = android.graphics.Canvas(mutableBitmap)
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.RED
            textSize = 48f
            style = android.graphics.Paint.Style.FILL
            setShadowLayer(5f, 0f, 0f, android.graphics.Color.BLACK)
        }
        canvas.drawText(label, 20f, 60f, paint)
        extraInfo?.let {
            val infoPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLUE
                textSize = 36f
                style = android.graphics.Paint.Style.FILL
                setShadowLayer(3f, 0f, 0f, android.graphics.Color.WHITE)
            }
            canvas.drawText(it, 20f, 110f, infoPaint)
        }
        return mutableBitmap
    }

    fun classifyAndShow(bitmap: Bitmap, imageView: android.widget.ImageView, extraInfo: String? = null) {
        val labeledBitmap = classifyWithLabel(bitmap, extraInfo)
        imageView.setImageBitmap(labeledBitmap)
    }

    fun saveCorrectionToDownload(context: Context, image: Bitmap, label: String, confidence: Float, engine: String) {
        val dir = java.io.File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS), "Albanil/$engine")
        if (!dir.exists()) dir.mkdirs()
        val filename = "correction_${System.currentTimeMillis()}.png"
        val imageFile = java.io.File(dir, filename)
        val fos = java.io.FileOutputStream(imageFile)
        image.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
        // Guarda la info en un archivo de texto
        val infoFile = java.io.File(dir, "corrections.txt")
        infoFile.appendText("$filename,$label,$confidence\n")
        android.util.Log.d("TFLiteFamilyClassifier", "Corrección guardada: ${imageFile.absolutePath}, $label, $confidence")
    }

    fun classifyWithConfidence(bitmap: Bitmap, context: Context, engine: String, onResult: (String, Float) -> Unit) {
        val input = bitmapToByteBuffer(bitmap)
        val output = Array(1) { FloatArray(labels.size) }
        interpreter.run(input, output)
        val maxIdx = output[0].indices.maxByOrNull { output[0][it] } ?: 0
        val confidence = output[0][maxIdx]
        val label = labels[maxIdx]
        android.util.Log.d("TFLiteFamilyClassifier", "Familia detectada: $label, confianza: $confidence")
        // Mostrar datos en la interfaz
        android.widget.Toast.makeText(context, "Familia: $label\nConfianza: ${(confidence * 100).toInt()}%", android.widget.Toast.LENGTH_LONG).show()
        if (confidence < 0.9f) {
            android.app.AlertDialog.Builder(context)
                .setTitle("¿La familia detectada es correcta?")
                .setMessage("Detectado: $label (confianza: ${(confidence * 100).toInt()}%)")
                .setPositiveButton("Sí") { _, _ ->
                    saveCorrectionToDownload(context, bitmap, label, confidence, engine)
                    onResult(label, confidence)
                }
                .setNegativeButton("No") { _, _ ->
                    saveCorrectionToDownload(context, bitmap, "Desconocido", confidence, engine)
                    onResult("Desconocido", confidence)
                }
                .show()
        } else {
            saveCorrectionToDownload(context, bitmap, label, confidence, engine)
            onResult(label, confidence)
        }
    }
}
