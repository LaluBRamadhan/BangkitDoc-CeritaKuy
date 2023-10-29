package com.code.presubmission.data
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.code.presubmission.data.pref.ResultState
import com.code.presubmission.data.pref.UserModel
import com.code.presubmission.data.pref.UserPreference
import com.code.presubmission.data.response.DetailStoryResponse
import com.code.presubmission.data.response.ListStoryItem
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File


class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {


    private val _isLoading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _isLoading

    private val _listStoryLocation = MutableLiveData<StoryResponse>()
    val listStoryLocation: LiveData<StoryResponse> = _listStoryLocation


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

    fun getStory(token:String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(userPreference,apiService)
            }
        ).liveData
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

    fun getStoryLocation(token: String){
        _isLoading.value = true

        val client = apiService.getStoriesWithLocation("Bearer $token")
        client.enqueue(object: Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _listStoryLocation.value = response.body()
                } else {
                    Log.e(
                        "getStory",
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e("getStory", "onFailure: ${t.message.toString()}")
            }

        })
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