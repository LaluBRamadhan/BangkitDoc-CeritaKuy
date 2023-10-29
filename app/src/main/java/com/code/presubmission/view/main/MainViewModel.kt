package com.code.presubmission.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.code.presubmission.data.UserRepository
import com.code.presubmission.data.pref.UserModel
import com.code.presubmission.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStory(token: String):LiveData<PagingData<ListStoryItem>> = repository.getStory(token).cachedIn(viewModelScope)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}