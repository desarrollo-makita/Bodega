package com.makita.ubiapp.ui.component.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.makita.ubiapp.ui.component.dao.LoginDao
import com.makita.ubiapp.ui.component.dao.RegistrarUbicacionDao
import com.makita.ubiapp.ui.component.entity.LoginEntity
import com.makita.ubiapp.ui.component.entity.RegistraUbicacionEntity

@Database(entities = [LoginEntity::class , RegistraUbicacionEntity ::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loginDao(): LoginDao
    abstract fun registrarUbicacion(): RegistrarUbicacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}