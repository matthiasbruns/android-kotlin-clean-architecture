package com.matthiasbruns.kotlintutorial.dog.view

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.view.*
import com.matthiasbruns.kotlintutorial.KotlinApplication
import com.matthiasbruns.kotlintutorial.R
import com.matthiasbruns.kotlintutorial.dog.data.DogsAdapter
import com.matthiasbruns.kotlintutorial.dog.injection.DaggerDogComponent
import com.matthiasbruns.kotlintutorial.dog.injection.DogComponent
import com.matthiasbruns.kotlintutorial.dog.injection.DogModule
import com.matthiasbruns.kotlintutorial.dog.presentation.DogsListPresenter
import com.matthiasbruns.kotlintutorial.dog.viewmodel.DogsListViewModel
import com.matthiasbruns.kotlintutorial.injection.NetworkModule
import com.pascalwelsch.compositeandroid.fragment.CompositeFragment
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_dogs_list.*
import net.grandcentrix.thirtyinch.internal.TiPresenterProvider
import net.grandcentrix.thirtyinch.plugin.TiFragmentPlugin

/**
 * Created by Bruns on 18.07.2017.
 */
class DogsListFragment : CompositeFragment(), DogsListView, LifecycleRegistryOwner {

    private val adapter = DogsAdapter()
    private val presenter = DogsListPresenter()
    private val lifecycleRegistry = LifecycleRegistry(this)

    private lateinit var onReloadClickSubject: PublishSubject<Any>
    private lateinit var viewModel: DogsListViewModel

    init {
        addPlugin(TiFragmentPlugin<DogsListPresenter, DogsListView>(TiPresenterProvider { presenter }))
    }

    /**
     * Lazy initialized DogComponent
     */
    val component: DogComponent by lazy {
        DaggerDogComponent.builder()
                .appModule(KotlinApplication.module())
                .dogModule(DogModule())
                .networkModule(NetworkModule())
                .build()
    }

    override fun getLifecycle(): LifecycleRegistry {
        return lifecycleRegistry
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        onReloadClickSubject = PublishSubject.create()

        component.inject(this)
        component.inject(presenter)

        viewModel = ViewModelProviders.of(this).get(DogsListViewModel::class.java)
        subscribeToViewModel()
    }

    fun subscribeToViewModel() {
        viewModel.getDogs().observe(this, Observer { dogs ->
            adapter.dogs = dogs
        })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_dogs_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dogs_recycler_view.adapter = adapter
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        dogs_recycler_view.layoutManager = layoutManager
        dogs_recycler_view.setHasFixedSize(true)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(dogs_recycler_view)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.dog_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_reload -> {
                onReloadClickSubject.onNext(Object())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        onReloadClickSubject.onComplete()
        super.onDestroy()
    }

    override fun onReloadClick(): Observable<Any> {
        return onReloadClickSubject
    }

    override fun getViewModel(): DogsListViewModel {
        return viewModel
    }
}