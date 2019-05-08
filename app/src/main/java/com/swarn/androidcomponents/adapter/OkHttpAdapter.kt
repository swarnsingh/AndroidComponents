package com.swarn.androidcomponents.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.data.Post
import java.util.*


/**
 * @author Swarn Singh.
 */
class OkHttpAdapter(context: Context) : RecyclerView.Adapter<OkHttpAdapter.OkHttpHolder>() {

    private val context = context

    private var posts = ArrayList<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OkHttpHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_list_item, null, false)

        return OkHttpHolder(itemView)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: OkHttpHolder, position: Int) {
        holder.bind(posts[position])
    }

    fun setPosts(posts: List<Post>) {
        this.posts = posts as ArrayList<Post>
        notifyDataSetChanged()
    }

    fun updatePost(post: Post) {
        posts[posts.indexOf(post)] = post
        notifyItemChanged(posts.indexOf(post))
    }

    fun getPosts(): List<Post> {
        return posts
    }


    inner class OkHttpHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title)
        var numComments: TextView = itemView.findViewById(R.id.num_comments)
        var progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)


        fun bind(post: Post) {
            title.text = post.title

            if (post.comments == null) {
                (context as Activity)?.runOnUiThread {
                    showProgressBar(true)
                    numComments.text = ""
                }
            } else {
                (context as Activity)?.runOnUiThread {
                    showProgressBar(false)
                    numComments.setText((post.comments.size))
                }
            }
        }

        private fun showProgressBar(showProgressBar: Boolean) {
            if (showProgressBar) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }
}