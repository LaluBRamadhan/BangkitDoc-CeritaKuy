package com.code.presubmission.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.presubmission.data.UserRepository
import com.code.presubmission.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {


    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun loginUser(email: String, password:String) = repository.login(email, password)
}