package com.makita.ubiapp.ui.component.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.makita.ubiapp.ui.component.entity.LoginEntity
import com.makita.ubiapp.ui.component.entity.RegistraUbicacionEntity



@Dao
interface LoginDao {
    @Insert
    suspend fun insertLogin(login: LoginEntity): Unit

    @Query("SELECT * FROM login_table ORDER BY loginTime DESC")
    suspend fun getAllLogins(): List<LoginEntity>

    @Query("DELETE FROM login_table")
    suspend fun deleteAllLogins():Unit




}

