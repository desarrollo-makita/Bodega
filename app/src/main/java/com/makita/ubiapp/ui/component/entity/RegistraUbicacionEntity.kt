package com.makita.ubiapp.ui.component.entity


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
    val tipoItem:  String
)