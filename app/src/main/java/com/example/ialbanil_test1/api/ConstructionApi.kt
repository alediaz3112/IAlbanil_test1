package com.example.ialbanil_test1.api

// Base para consumir APIs de construcción agrupadas por familias

interface ConstructionApi {
    suspend fun fetchData(query: String): Any
}

class StructureApi : ConstructionApi {
    override suspend fun fetchData(query: String): Any {
        // Implementación futura para pisos, paredes, techo, aberturas, ladrillos, columnas, vigas
        return Any()
    }
}

class DecorationApi : ConstructionApi {
    override suspend fun fetchData(query: String): Any {
        // Implementación futura para pintura, revestimientos, cerámicos, muebles grandes
        return Any()
    }
}

class MaterialCalculatorApi : ConstructionApi {
    override suspend fun fetchData(query: String): Any {
        // Implementación futura para cálculos de materiales y medidas
        return Any()
    }
}

