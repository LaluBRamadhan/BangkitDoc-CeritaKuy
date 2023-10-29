package com.code.presubmission.view.signup

import androidx.lifecycle.ViewModel
import com.code.presubmission.data.UserRepository

class SignUpViewModel(private val repository: UserRepository): ViewModel() {

    fun registerUser(name: String, email: String, password: String) = repository.signup(name,email,password)


}