package com.example.bluetoothrccar.ui.auth.login

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bluetoothrccar.control.ControlActivity
import com.example.bluetoothrccar.repository.RegisterRepository
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class LoginViewModel (private val repository: RegisterRepository, application: Application)
    :AndroidViewModel(application),Observable
{

    val users = repository.users



    @Bindable
    val inputUsername = MutableLiveData<String>()



    @Bindable
    val inputPassword = MutableLiveData<String>()

    private val viewModelsJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelsJob)

    //Move to layout Register

    private val _navigateToRegister = MutableLiveData<Boolean>()
    val navigateToRegister: LiveData<Boolean>
        get() = _navigateToRegister

    //Move to layout RC Car Controller

    private val _navigateToControl = MutableLiveData<Boolean>()
    val navigateToControl: LiveData<Boolean>
        get() = _navigateToControl

    //Error display

    private val _errorToast = MutableLiveData<Boolean>()
    val errorToast: LiveData<Boolean>
        get() = _errorToast

    //Error display for invalid username

    private val _errorToastUsername = MutableLiveData<Boolean>()
    val errorToastUsername: LiveData<Boolean>
        get() = _errorToastUsername

    //Error display for invalid password

    private val _errorToastPassword = MutableLiveData<Boolean>()
    val errorToastPassword: LiveData<Boolean>
        get() = _errorToastPassword




    // Function triggered when the Login Button is Clicked via Binding

    fun singUp()
    {
        _navigateToRegister.value=true
    }

    fun loginButton()
    {
        if(inputUsername.value==null || inputPassword.value ==null)
        {
            _errorToast.value = true
        }
        else
        {
            uiScope.launch{
                val usersNames = repository.getUserName((inputUsername.value!!))
                if(usersNames!=null) {
                    if (usersNames.password == inputPassword.value) {
                        inputUsername.value = null
                        inputPassword.value = null
                        _navigateToControl.value = true

                    } else {
                        _errorToastPassword.value = true

                    }
                }else{
                    _errorToastUsername.value = true

                }
            }
        }
    }



    fun doneNavigationRegister()
    {
        _navigateToRegister.value = false
    }

    fun doneNavigationControl()
    {
        _navigateToControl.value = false
    }

    fun doneToast()
    {
        _errorToast.value = false
        Log.i("MYTAG", "Done Toasting")
    }

    fun doneToastErrorUsername()
    {
        _errorToastUsername.value = false
        Log.i("MYTAG", "Done Toasting")
    }

    fun doneToastInvalidPassword()
    {
        _errorToastPassword.value = false
        Log.i("MYTAG", "Done Toasting")

    }

      override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    fun getUsername():MutableLiveData<String>
    {
        return inputUsername
    }



}