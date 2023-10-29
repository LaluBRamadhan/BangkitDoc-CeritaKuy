package com.code.presubmission

import com.code.presubmission.data.response.ListStoryItem
import com.code.presubmission.data.response.StoryResponse

object Dummy {
    fun generateDummyStoryResponse(): StoryResponse {
        val listStory: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val listStoryItem = ListStoryItem(
                createdAt = "2023-10-20T12:26:05Z",
                description = "Description $i",
                id = "id_$i",
                lat = i.toDouble() * 10,
                lon = i.toDouble() * 10,
                name = "Name $i",
                photoUrl = "https://images.unsplash.com/photo-1451976426598-a7593bd6d0b2?auto=format&fit=crop&q=80&w=2070&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
            )
            listStory.add(listStoryItem)
        }
        return StoryResponse(
            error = false, message = "Stories fetched successfully", listStory = listStory
        )
    }
}