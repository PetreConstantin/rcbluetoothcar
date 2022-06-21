package com.example.bluetoothrccar.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Register_users_table")



data class RegisterEntity (
    @PrimaryKey(autoGenerate = true)
    var userId: Int = 0,

    @ColumnInfo(name="user_name")
    var userName: String,

    @ColumnInfo(name="password_text")
    var password: String,

    @ColumnInfo(name="email_text")
    var email: String

)