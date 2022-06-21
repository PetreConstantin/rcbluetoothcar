package com.example.bluetoothrccar.ui.auth.register

import android.app.Application
import android.provider.SyncStateContract.Helpers.insert
import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bluetoothrccar.data.RegisterEntity
import com.example.bluetoothrccar.repository.RegisterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.databinding.Observable
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class RegisterViewModel (private val repository: RegisterRepository, application: Application) :
    AndroidViewModel(application), Observable {

    init {
        Log.i("MYTAG","init")
    }

    private var userdata: String? = null

    var userDetailsLiveData = MutableLiveData<Array<RegisterEntity>>()

    @Bindable
    val inputUsername = MutableLiveData<String>()

    @Bindable
    val inputPassword = MutableLiveData<String>()

    @Bindable
    val inputEmail = MutableLiveData<String>()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    //navigate function
    private val _navigateTo = MutableLiveData<Boolean>()
    val navigateTo: LiveData<Boolean>
        get() = _navigateTo


    //error function
    private val _errorToast = MutableLiveData<Boolean>()
    val errorToast: LiveData<Boolean>
        get() = _errorToast

    // username error function

    private val _errorToastUsername = MutableLiveData<Boolean>()
    val errorToastUsername: LiveData<Boolean>
        get() = _errorToastUsername

    fun submitButton()
    {
        Log.i("MYTAG","Inside submit button")
        if(inputEmail.value == null || inputUsername.value == null || inputPassword.value ==null)
        {
            _errorToast.value = true
        }
        else{
            uiScope.launch{
                val usersNames = repository.getUserName(inputUsername.value!!)
                Log.i("MYTAG",usersNames.toString())
                if(usersNames!=null){
                    _errorToastUsername.value=true
                    Log.i("MYTAG","Inside if not null")
                }else{
                    Log.i("MYTAG","Ok, im in")
                    val email=inputEmail.value!!
                    val username = inputUsername.value!!
                    val password = inputPassword.value!!
                    Log.i("MYTAG","Inside Submit")
                    insert(RegisterEntity(0,username,password,email))
                    inputUsername.value = null
                    inputPassword.value = null
                    inputEmail.value = null
                    _navigateTo.value = true
                }
            }

        }

    }

    override fun onCleared() {
        super.onCleared()
    }



    fun doneNavigating(){
        _navigateTo.value = false
        Log.i("MYTAG","Done Navigating")
    }

    fun doneToast()
    {
        _errorToast.value = false
        Log.i("MYTAG", "Done Toasting")
    }

    fun doneToastUsername()
    {
        _errorToastUsername.value = false
        Log.i("MYTAG","Done Toasting username")
    }

    private fun insert(user: RegisterEntity): Job = viewModelScope.launch {
        repository.insert(user)
    }

    override fun removeOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback?) {
    }

    override fun addOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback?) {
    }



}