package com.code.presubmission.data
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.code.presubmission.data.pref.ResultState
import com.code.presubmission.data.pref.UserModel
import com.code.presubmission.data.pref.UserPreference
import com.code.presubmission.data.response.DetailStoryResponse
import com.code.presubmission.data.response.LoginResponse
import com.code.presubmission.data.response.RegisterResponse
import com.code.presubmission.data.response.StoryResponse
import com.code.presubmission.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import retrofit2.HttpException
import java.io.File


class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {


    private val _isLoading = MutableLiveData<Boolean>()
    val Loading: LiveData<Boolean> = _isLoading


    fun signup(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val success = apiService.register(name, email, password)
            emit(ResultState.Success(success))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val error = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(error.message?.let { ResultState.Error(it) })
        }

    }

    fun login(email:String, password:String) = liveData {
        emit(ResultState.Loading)
        try {
            val success = apiService.login(email,password)
            emit(ResultState.Success(success))
        }catch (e: HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            val error = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(error.message?.let { ResultState.Error(it) })
        }
    }

    fun getStory(token:String) = liveData {
        emit(ResultState.Loading)
        try {
            val success = apiService.getStories("Bearer $token")
            emit(ResultState.Success(success))
        }catch (e: HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            val error = Gson().fromJson(errorBody, StoryResponse::class.java)
            emit(error.message?.let { ResultState.Error(it)})
        }
    }

    fun detailStory(token: String, id: String): LiveData<DetailStoryResponse> = liveData{
        try {
            val success = apiService.getDetailStories("Bearer $token", id)
            emit(success)
        }catch (e: HttpException){
            Log.e("Failed", "detailStory: ${e.message}", )
        }
    }

    fun upload(token: String, imageFile: File, description: String) = liveData {
        emit(ResultState.Loading)
        val reqImage = imageFile.asRequestBody("image/jpeg".toMediaType())
        val reqBody = description.toRequestBody("text/plain".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            reqImage
        )
        try {
            val success = apiService.uploadImage("Bearer $token", multipartBody, reqBody)
            emit(ResultState.Success(success))
        }catch (e: HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            val error = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(error.message?.let { ResultState.Error(it) })
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}