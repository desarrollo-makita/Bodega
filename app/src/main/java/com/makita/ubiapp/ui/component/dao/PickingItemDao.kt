package com.makita.ubiapp.ui.component.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.makita.ubiapp.ui.component.entity.PickingItemEntity

@Dao
interface PickingItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPickingItems(pickingItems: List<PickingItemEntity>)

    @Query("SELECT * FROM picking_item_table")
    suspend fun obtenerTodosLosPickingItems(): List<PickingItemEntity>

    // Método para borrar todos los datos de la tabla
    @Query("DELETE FROM picking_item_table")
    suspend fun borrarTodosLosPickingItems()
}
