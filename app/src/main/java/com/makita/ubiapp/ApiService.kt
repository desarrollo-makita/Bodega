package com.makita.ubiapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

data class UbicacionResponse(
    var Ubicacion: String,
    var descripcion: String,
    var item: String,
    var tipoItem: String,
    var nuevaUbicacion: String
)

data class ActualizaUbicacionRequest(
    val nuevaUbicacion: String,
    val empresa: String,
    val item: String,
    val tipoItem: String
)

data class RegistroUbicacionRequest(
    val nuevaUbicacion: String,
    val empresa: String,
    val item: String,
    val tipoItem: String
)

interface ApiService {

    @GET("api/obtener-ubicacion/{ubicacion}")
    suspend fun obtenerUbicacion(@Path("ubicacion") ubicacion: String) : List<UbicacionResponse>


    @PUT("api/actualiza-ubicacion")
    suspend fun actualizaUbicacion(@Body request: ActualizaUbicacionRequest): Response<Unit>
}
