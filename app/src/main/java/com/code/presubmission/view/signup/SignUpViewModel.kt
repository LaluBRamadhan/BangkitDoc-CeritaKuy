package com.code.presubmission.view.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.presubmission.data.UserRepository
import kotlinx.coroutines.launch

class SignUpViewModel(private val repository: UserRepository): ViewModel() {

    fun registerUser(name: String, email: String, password: String) = repository.signup(name,email,password)


}