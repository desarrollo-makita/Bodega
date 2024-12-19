package com.makita.ubiapp.ui.component.entity

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "picking_item_table")
data class PickingItemEntity (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val empresa: String,
    val correlativo: Int,
    val entidad: String,
    val nombrecliente: String,
    val direccion: String,
    val comuna: String,
    val ciudad: String,
    val bodorigen: String,
    val boddestino: String,
    val documentoOrigen: String,
    val correlativoOrigen: String,
    val glosa: String,
    val totalItems: Int,
    val fecha: String,
    val procesado: Int

)
