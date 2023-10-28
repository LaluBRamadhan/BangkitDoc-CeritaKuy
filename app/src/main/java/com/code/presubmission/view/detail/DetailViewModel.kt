package com.code.presubmission.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.code.presubmission.data.UserRepository
import com.code.presubmission.data.pref.UserModel

class DetailViewModel(private val repository: UserRepository): ViewModel() {
    val Loading = repository.loading
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getDetail(token: String, id: String) = repository.detailStory(token, id)
}