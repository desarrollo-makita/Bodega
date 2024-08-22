package com.makita.ubiapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.makita.ubiapp.entity.RegistraUbicacionEntity



@Dao
interface RegistrarUbicacionDao {
   @Insert
    suspend fun registraUbicacion (registraUbicacion: RegistraUbicacionEntity): Unit

    @Query("SELECT * FROM registro_ubicacion_table ORDER BY timestamp DESC")
    suspend fun getAllData(): List<RegistraUbicacionEntity>

    @Query("DELETE FROM registro_ubicacion_table")
    suspend fun deleteAllData():Unit
}

