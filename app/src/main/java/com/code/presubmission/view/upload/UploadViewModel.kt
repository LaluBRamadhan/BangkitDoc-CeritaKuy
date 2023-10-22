package com.code.presubmission.view.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.code.presubmission.data.UserRepository
import com.code.presubmission.data.pref.UserModel
import java.io.File

class UploadViewModel(private val repository: UserRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun upload(token: String, uploadFile: File, description: String) = repository.upload(token, uploadFile, description)
}