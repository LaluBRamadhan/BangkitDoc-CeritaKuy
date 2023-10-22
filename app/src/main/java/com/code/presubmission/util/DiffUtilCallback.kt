package com.code.presubmission.util

import androidx.recyclerview.widget.DiffUtil
import com.code.presubmission.data.response.ListStoryItem

class DiffUtilCallback : DiffUtil.ItemCallback<ListStoryItem>() {
    override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem == newItem
    }
}