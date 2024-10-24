package com.makita.ubiapp.ui.component.entity


import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "registro_ubicacion_table")
data class RegistraUbicacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val timestamp: String,
    val item: String,
    val ubicacionAntigua: String,
    val nuevaUbicacion: String,
    val tipoItem: String
) : Parcelable {
    // Constructor para crear el objeto desde un Parcel
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        username = parcel.readString() ?: "",
        timestamp = parcel.readString() ?: "",
        item = parcel.readString() ?: "",
        ubicacionAntigua = parcel.readString() ?: "",
        nuevaUbicacion = parcel.readString() ?: "",
        tipoItem = parcel.readString() ?: ""
    )

    // Escribir los datos en el Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(username)
        parcel.writeString(timestamp)
        parcel.writeString(item)
        parcel.writeString(ubicacionAntigua)
        parcel.writeString(nuevaUbicacion)
        parcel.writeString(tipoItem)
    }

    // Necesario para implementar Parcelable
    override fun describeContents(): Int {
        return 0
    }

    // Creador para regenerar el objeto desde el Parcel
    companion object CREATOR : Parcelable.Creator<RegistraUbicacionEntity> {
        override fun createFromParcel(parcel: Parcel): RegistraUbicacionEntity {
            return RegistraUbicacionEntity(parcel)
        }

        override fun newArray(size: Int): Array<RegistraUbicacionEntity?> {
            return arrayOfNulls(size)
        }
    }
}