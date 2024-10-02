package com.makita.ubiapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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

data class LoginRequest(
    val nombreUsuario: String,
    val clave: String,
 )

data class LoginResponse(
    val status: Int,
    val message: String,
    val data: UserData
)

data class UserData(
    val UsuarioID: Int,
    val Nombre: String,
    val Apellido: String,
    val Email: String,
    val Area: String,
    val Rol: String,
    val Estado: String,
    val FechaInicio: String,
    val FechaFin: String,
    val NombreUsuario: String,
    val Actividad: String,
    val menu: List<MenuItem>,
    val token: String,
    val vigencia: Long
)


data class MenuItem(
    val MenuID: Int,
    val Nombre: String,
    val Ruta: String,
    val Icono: String,
    val Clase: String
)

data class CambioClaveRequest(
    val nombreUsuario: String,
    val password: String,
    val idUsuario: Int,
    val token: String
)
data class CambioClaveResponse(
    val status: Int,
    val message: String,
)

interface ApiService {

    @GET("api/obtener-ubicacion/{ubicacion}")
    suspend fun obtenerUbicacion(@Path("ubicacion") ubicacion: String) : List<UbicacionResponse>


    @PUT("api/actualiza-ubicacion")
    suspend fun actualizaUbicacion(@Body request: ActualizaUbicacionRequest): Response<Unit>

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Nuevo método para validar la clave actual
    @POST("api/valida-clave-actual")
    suspend fun validarClaveActual(@Body request: CambioClaveRequest): Response<CambioClaveResponse>

    @PUT("api/editar-usuarios-id")
    suspend fun editarClave(
        @Header("Authorization") token: String, // Aquí pasas el token
        @Body request: CambioClaveRequest): Response<CambioClaveResponse>
}
