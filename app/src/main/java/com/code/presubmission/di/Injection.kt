package com.code.presubmission.di

import android.content.Context
import com.code.presubmission.data.UserRepository
import com.code.presubmission.data.pref.UserPreference
import com.code.presubmission.data.pref.dataStore
import com.code.presubmission.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref,apiService)
    }
}