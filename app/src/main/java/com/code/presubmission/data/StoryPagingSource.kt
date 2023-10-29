package com.code.presubmission.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.code.presubmission.data.pref.UserPreference
import com.code.presubmission.data.response.ListStoryItem
import com.code.presubmission.data.retrofit.ApiService
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class StoryPagingSource(private val userPreference: UserPreference,
                        private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try{
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = userPreference.getSession().first().token
            val responseData = apiService.getStories("Bearer $token", position, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if(position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if(responseData.listStory.isEmpty()) null else position + 1,
            )
        }catch (e: HttpException){
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}