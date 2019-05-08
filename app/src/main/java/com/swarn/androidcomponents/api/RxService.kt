package com.swarn.androidcomponents.api

import com.swarn.androidcomponents.data.Comment
import com.swarn.androidcomponents.data.Post
import io.reactivex.Flowable
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * @author Swarn Singh.
 */
interface RxService {
    @GET("posts")
    fun getPosts(): Flowable<List<Post>>

    @GET("posts/{id}/comments")
    fun getComments(@Path("id") id: Int): Flowable<List<Comment>>
}