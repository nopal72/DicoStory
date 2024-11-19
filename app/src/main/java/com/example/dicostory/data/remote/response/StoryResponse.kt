package com.example.dicostory.data.remote.response

data class StoryResponse(
    val listStory: List<ListStoryItem> = emptyList(),
    val error: Boolean,
    val message: String
)

data class ListStoryItem(
	val photoUrl: String,
	val createdAt: String,
	val name: String,
	val description: String,
	val lon: Double? = null,
	val id: String,
	val lat: Double? = null
)

