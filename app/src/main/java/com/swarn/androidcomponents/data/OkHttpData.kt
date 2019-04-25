package com.swarn.androidcomponents.data

import com.google.gson.annotations.SerializedName


/**
 * @author Swarn Singh.
 */

data class OkHttpData(
    @SerializedName("user")
    val user: User,
    @SerializedName("videos")
    val videos: List<Video>
) {
    data class Video(
        @SerializedName("channel")
        val channel: Channel,
        @SerializedName("id")
        val id: Int,
        @SerializedName("imageUrl")
        val imageUrl: String,
        @SerializedName("link")
        val link: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("numberOfViews")
        val numberOfViews: Int
    ) {
        data class Channel(
            @SerializedName("name")
            val name: String,
            @SerializedName("numberOfSubscribers")
            val numberOfSubscribers: Int,
            @SerializedName("profileImageUrl")
            val profileImageUrl: String
        )
    }

    data class User(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("username")
        val username: String
    )
}