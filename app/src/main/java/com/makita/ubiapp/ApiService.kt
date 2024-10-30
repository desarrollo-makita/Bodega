package com.makita.ubiapp

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Query
import com.google.type.DateTime
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Ubicacion)
        parcel.writeString(descripcion)
        parcel.writeString(item)
        parcel.writeString(tipoItem)
        parcel.writeString(nuevaUbicacion)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UbicacionResponse> {
        override fun createFromParcel(parcel: Parcel): UbicacionResponse {
            return UbicacionResponse(parcel)
        }

        override fun newArray(size: Int): Array<UbicacionResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class ActualizaUbicacionRequest(
    val nuevaUbicacion: String,
    val empresa: String,
    val item: String,
    val tipoItem: String
)

data class RegistraBitacoraRequest(
    val usuario : String,
    val item : String,
    val fechaCambio : String,
    val tipoItem : String,
    val ubicacionAntigua : String,
    val nuevaUbicacion : String
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
    val vigencia: Long,
    val recuperarClave: Int,
    val actividades: List<ActividadItem>
)


data class MenuItem(
    val MenuID: Int,
    val Nombre: String,
    val Ruta: String,
    val Icono: String,
    val Clase: String
)

data class ActividadItem(
    val id: Int,
    val nombreUsuario: String,
    val usuarioId: String,
    val nombreActividad: String,
    val actividadId: String,
    val ruta: String,
    val icono: String
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


data class RecuperarRequest(
    val usuario: String,
)
data class RecuperarResponse(
   val mensaje :  dataRecuperarResponse
)

data class dataRecuperarResponse(
    val status: Int,
    val mensaje: String,
    val existe : Boolean,
    val email : String,
    val idUsuario: Int
)
data class ReplaceClaveRequest(
    val data: Data
)

data class Data(
    val idUsuario: Int,
    val password: String
)

data class DataDispositivo(
     val usuario: String,
     val modelo : String,
     val fabricante : String,
     val sistemaOperativo : String,
     val numeroSerie : String,
     val idAndroid : String,
)

data class DataDispositivoRequest(
    val dataDispositivo: DataDispositivo
)
data class DispositivoResponse(
    val message : String
)

data class PickingItem(
    val empresa: String,
    val correlativo: Int,
    val entidad: String,
    val nombrecliente: String,
    val Direccion: String,
    val comuna: String,
    val Ciudad: String,
    val Bodorigen: String,
    val Boddestino: String,
    val DocumentoOrigen: String,
    val CorrelativoOrigen: Int,
    val glosa: String,
    val totalItems: Int,
    val Fecha: String

)

data class PickingResponse(
    val status: Int,
    val data: List<PickingItem>
)
data class PickingFolioResponse(
    val status: Int,
    val data: PickingItem
)

interface ApiService {

    @GET("api/obtener-ubicacion/{ubicacion}")
    suspend fun obtenerUbicacion(@Path("ubicacion") ubicacion: String) : List<UbicacionResponse>


    @PUT("api/actualiza-ubicacion")
    suspend fun actualizaUbicacion(@Body request: ActualizaUbicacionRequest): Response<Unit>

    @POST("api/insertar-registro-ubicacion")
    suspend fun insertaBitacoraUbicacion(@Body request: RegistraBitacoraRequest): Response<Unit>

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Nuevo método para validar la clave actual
    @POST("api/valida-clave-actual")
    suspend fun validarClaveActual(@Body request: CambioClaveRequest): Response<CambioClaveResponse>

    @PUT("api/editar-usuarios-id")
    suspend fun editarClave(@Header("Authorization") token: String, @Body request: CambioClaveRequest): Response<CambioClaveResponse>

    @POST("api/recuperar-password")
    suspend fun recuperarPassword(@Body request: RecuperarRequest): Response<RecuperarResponse>

    @PUT("api/replace-password-id")
    suspend fun replaceClave(@Body request: ReplaceClaveRequest): Response<CambioClaveResponse>

    @POST("api/insertar-info-dispositivo")
    suspend fun insertarInfoDspositivo(@Body request: DataDispositivoRequest): Response<DispositivoResponse>

    @GET("api/get-all-pickingList")
    suspend fun obtenerPickinglist(): Response<PickingResponse>

    @GET("api/get-picking-folio/{folio}")
    suspend fun obtenerPickingFolio(@Path("folio") folio: String):Response <PickingFolioResponse>
}
