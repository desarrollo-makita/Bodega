package com.makita.ubiapp.entity


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "login_table")
data class LoginEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String,
    val loginTime: Long
)