package com.example.bluetoothrccar.ui.auth.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.bluetoothrccar.R
import com.example.bluetoothrccar.data.RegisterDatabase
import com.example.bluetoothrccar.databinding.FragmentLoginBinding
import com.example.bluetoothrccar.repository.RegisterRepository


class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel


    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLoginBinding = DataBindingUtil.inflate (inflater,R.layout.fragment_login,container,false)

        val application = requireNotNull(this.activity).application

        val dao = RegisterDatabase.getInstance(application).registerDatabaseDao

        val repository = RegisterRepository(dao)

        val factory = LoginViewModelFactory(repository,application)


        loginViewModel = ViewModelProvider(this,factory).get(LoginViewModel::class.java)

        binding.myloginViewModel = loginViewModel

        binding.lifecycleOwner = this

        loginViewModel.navigateToRegister.observe(this, Observer { hasFinished->

            if(hasFinished==true)
            {
                Log.i("MYTAG", "Inside Observer")
                displayControl() //
                loginViewModel.doneNavigationRegister()
            }
        })

        loginViewModel.errorToast.observe(this, Observer { hasError->
            if(hasError == true) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                loginViewModel.doneToast()
            }
        })

        loginViewModel.errorToastUsername.observe(this, Observer { hasError->
            if(hasError==true){
                Toast.makeText(requireContext(), "Invalid username . Please verify or register!", Toast.LENGTH_LONG).show()
                loginViewModel.doneToastErrorUsername()
            }
        })

        loginViewModel.errorToastPassword.observe(this, Observer { hasError->
            if(hasError==true) {
                Toast.makeText(requireContext(), "Invalid password .", Toast.LENGTH_LONG).show()
                loginViewModel.doneToastInvalidPassword()
            }
        })

        loginViewModel.navigateToControl.observe(this, Observer { hasFinished->
            if(hasFinished==true)
            {
                Log.i("MYTAG","Inside observer")
                navigateToControls()
                loginViewModel.doneNavigationControl()
            }
        })

        return binding.root

    }
    private fun displayControl() {
           Log.i("MYTAG","insidisplayUsersList")
            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
            NavHostFragment.findNavController(this).navigate(action)

    }

    private fun navigateToControls()
    {
        Log.i("MYTAG","Inside controls view")
        val action = LoginFragmentDirections.actionLoginFragmentToSelectDeviceActivity()
        NavHostFragment.findNavController(this).navigate(action)
    }

}