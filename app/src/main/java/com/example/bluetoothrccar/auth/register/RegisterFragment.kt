package com.example.bluetoothrccar.ui.auth.register

import android.annotation.SuppressLint
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
import com.example.bluetoothrccar.databinding.FragmentRegisterBinding
import com.example.bluetoothrccar.repository.RegisterRepository
import com.example.bluetoothrccar.ui.auth.login.LoginFragmentDirections


class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel

    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRegisterBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_register, container, false
        )

        val application = requireNotNull(this.activity).application

        val dao = RegisterDatabase.getInstance(application).registerDatabaseDao

        val repository = RegisterRepository(dao)

        val factory = RegisterViewModelFactory(repository,application)

        registerViewModel = ViewModelProvider(this,factory).get(RegisterViewModel::class.java)

        binding.myRegisterViewModel = registerViewModel

        binding.lifecycleOwner = this

        registerViewModel.navigateTo.observe(this, Observer { hasFinished->
            if(hasFinished ==true){
                Log.i("MYTAG", "Inside Observer")
                displayControl() //
                registerViewModel.doneNavigating()
            }
        })

        registerViewModel.userDetailsLiveData.observe(this, Observer {
            Log.i("MYTAG",it.toString())
        })

        registerViewModel.errorToast.observe(this, Observer { hasError->
            if(hasError == true)
            {
                Toast.makeText(requireContext(), "Please fill all fields ", Toast.LENGTH_SHORT)
                    .show()
                registerViewModel.doneToast()
            }
        })

        registerViewModel.errorToastUsername.observe(this, Observer { hasError->
            if(hasError == true) {
                Toast.makeText(requireContext(), "Username already taken", Toast.LENGTH_SHORT)
                    .show()
                registerViewModel.doneToastUsername()
            }
        })

        return binding.root
    }

    private fun displayControl() {
            Log.i("MYTAG","insidisplayUsersList")
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            NavHostFragment.findNavController(this).navigate(action)

    }

}