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
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 *
 */
class RxFragment : Fragment() {


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

        getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                return@flatMap getComments(it)
            }
            .toObservable()
            .subscribe(object : Observer<Post> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(post: Post) {
                    Log.d(RxFragment::class.java.canonicalName, post.toString())
                    adapter!!.updatePost(post)
                }

                override fun onError(e: Throwable) {
                    Log.e(RxFragment::class.java.canonicalName, "onError: ", e)
                }
            })
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
                    .delay(1000, TimeUnit.MILLISECONDS)
                    .take(5)
            }
    }

    private fun getComments(post: Post): Flowable<Post> {
        return rxService.getComments(post.id)
            .subscribeOn(Schedulers.io())
            .map {
                /*val delay = (Random().nextInt(5) + 1) * 1000 // sleep thread for x ms
                Thread.sleep(delay.toLong())
                Log.d(
                    RxFragment::class.java.canonicalName,
                    "apply: sleeping thread " + Thread.currentThread().name + " for " + delay.toString() + "ms"
                )*/

                post.comments = it
                return@map post
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
