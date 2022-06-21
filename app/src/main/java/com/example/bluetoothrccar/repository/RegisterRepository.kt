package com.example.bluetoothrccar.repository

import com.example.bluetoothrccar.data.RegisterDatabaseDao
import com.example.bluetoothrccar.data.RegisterEntity

class RegisterRepository(private val dao:RegisterDatabaseDao) {

    val users = dao.getAllUsers()

    suspend fun insert (user: RegisterEntity)
    {
        return dao.insert(user)
    }

    suspend fun getUserName(userName: String) :RegisterEntity?{
        return dao.getUsername(userName)
    }
}