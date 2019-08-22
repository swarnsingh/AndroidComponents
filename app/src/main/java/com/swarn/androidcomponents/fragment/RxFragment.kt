package com.swarn.androidcomponents.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.adapter.OkHttpAdapter
import com.swarn.androidcomponents.api.RxAPIClient
import com.swarn.androidcomponents.api.RxService
import com.swarn.androidcomponents.data.Post
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class RxFragment : Fragment() {

    private val TAG = RxFragment::class.java.canonicalName
    private var recyclerView: RecyclerView? = null
    private lateinit var rxService: RxService

    // vars
    private val disposables = CompositeDisposable()
    private var adapter: OkHttpAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rx, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView = activity!!.findViewById(R.id.recycler_view);

        adapter = OkHttpAdapter(activity!!)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter

        disposables.addAll(getPosts()
            .subscribeOn(Schedulers.io())
            .flatMap {
                return@flatMap getComments(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(
                    "updatePost -> ${it.id} && ${it.comments.size} -> ",
                    Thread.currentThread().name
                )
                adapter!!.updatePost(it)
            }, {
                Log.e(TAG, it.localizedMessage, it)
            })
        )
    }


    private fun getPosts(): Flowable<Post> {
        rxService = RxAPIClient.createService(RxService::class.java)

        return rxService.getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                adapter?.setPosts(it)
                return@flatMap Flowable.fromIterable(it)
                    .subscribeOn(Schedulers.io())
            }
    }

    private fun getComments(post: Post): Flowable<Post> {
        return rxService.getComments(post.id)
            .subscribeOn(Schedulers.io())
            .delay(5, TimeUnit.MILLISECONDS)
            .doOnError { Log.d("Got Error on ${post.id} -> ", it.localizedMessage) }
            .onErrorReturn { listOf() }
            .map {
                Log.d("getComments -> ${post.id} -> ", Thread.currentThread().name)
                post.comments = it
                return@map post
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
