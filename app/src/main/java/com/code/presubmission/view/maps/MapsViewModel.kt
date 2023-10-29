package com.code.presubmission.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.code.presubmission.data.UserRepository
import com.code.presubmission.data.pref.UserModel
import com.code.presubmission.data.response.StoryResponse

class MapsViewModel(private val repository: UserRepository): ViewModel() {
    val listStoryLocation: LiveData<StoryResponse> = repository.listStoryLocation

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStoriesWithLocation(token:String) {
        repository.getStoryLocation(token)
    }
}