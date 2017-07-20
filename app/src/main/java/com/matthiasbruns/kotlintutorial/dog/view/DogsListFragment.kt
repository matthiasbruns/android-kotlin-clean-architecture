package com.matthiasbruns.kotlintutorial.dog.view

import android.app.Dialog
import android.app.ProgressDialog
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.widget.*
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
 * This fragments displays dogs in a RecyclerView.
 * It also injects required objects into its presenter.
 * The user can issue a reload of the dogs content by pressing the reload button in the ToolBar
 *
 * The DogsListFragment implements the DogsListView and LifecycleRegistryOwner.
 *
 * The view is required to communicate with the DogsListPresenter.
 * The LifecycleRegistryOwner is required by the ViewModel to observe the owner and its lifecycle.
 */
class DogsListFragment : CompositeFragment(), DogsListView, LifecycleRegistryOwner {

    /**
     * The DogsAdapter stores the displayed dogs and injects item views per row
     */
    private val adapter = DogsAdapter()

    /**
     * The DogsListPresenter holds the presentation logic for this view
     */
    private val presenter = DogsListPresenter()

    /**
     * The LifecycleRegistry is required to allow the ViewModel to observe the lifecycle of this fragment
     */
    private val lifecycleRegistry = LifecycleRegistry(this)

    /**
     * This subjects emits items when the user presses the reload button in the OptionsMenu
     */
    private lateinit var onReloadClickSubject: PublishSubject<Any>

    /**
     * The DogsListViewModel stores view and domain data in a lifecycle-persistent way
     */
    private lateinit var viewModel: DogsListViewModel

    /**
     * Stores the currently displayed Dialog
     */
    private var dialog: Dialog? = null

    init {
        // Adds the MVP framework to our fragment - we could also extend TiFragment, but I am no fan of inheritance
        addPlugin(TiFragmentPlugin<DogsListPresenter, DogsListView>(TiPresenterProvider { presenter }))
    }

    /**
     * This components provides injection interfaces for certain classes.
     * It will be initialized on the first access.
     */
    val component: DogComponent by lazy {
        DaggerDogComponent.builder()
                .appModule(KotlinApplication.module())
                .dogModule(DogModule())
                .networkModule(NetworkModule())
                .build()
    }

    /**
     * Returns the Lifecycle of the provider.
     *
     * @return The lifecycle of the provider.
     */
    override fun getLifecycle(): LifecycleRegistry {
        return lifecycleRegistry
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables the fragment to inflate menus
        setHasOptionsMenu(true)

        // Create the Subject in onCreate - and complete in onDestroy
        onReloadClickSubject = PublishSubject.create()

        // Inject components we need
        component.inject(this)

        // Inject components the presenter needs
        component.inject(presenter)

        // Get the viewmodel from the ViewModelProviders
        viewModel = ViewModelProviders.of(this).get(DogsListViewModel::class.java)

        // Observe the viewmodel
        subscribeToViewModel()
    }

    /**
     * Subscribes to the DogsListViewModel data.
     */
    private fun subscribeToViewModel() {
        // React to data changes on the dogs property
        viewModel.getDogs().observe(this, Observer { dogs ->
            // Update the adapter, when the dogs list changes
            adapter.dogs = dogs
        })

        // Reacts on loading changes
        viewModel.isLoading().observe(this, Observer { loading ->
            // Ignore null params
            if (loading == null) return@Observer

            dogs_recycler_view.visibility = if (loading) View.GONE else View.VISIBLE

            if (dialog != null) {
                // Reset dialog
                dialog?.dismiss()
                dialog = null
            }

            if (loading) {
                // Show loading dialog
                dialog = ProgressDialog.show(context, getString(R.string.loading_title), getString(R.string.loading_message), true)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the view - why should I check, id the inflater is null?
        return inflater!!.inflate(R.layout.fragment_dogs_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View injected by kotlin-android-extentions
        dogs_recycler_view.adapter = adapter

        val layoutManager: RecyclerView.LayoutManager
        // set the layout manager and some props on the RecyclerView
        if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // create a layout manager
            layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
        } else {
            layoutManager = GridLayoutManager(context, 2)
            dogs_recycler_view.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        }

        dogs_recycler_view.layoutManager = layoutManager
        dogs_recycler_view.setHasFixedSize(true)

        // Improve scrolling with a SnapHelper
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(dogs_recycler_view)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        // Inject out menu
        inflater!!.inflate(R.menu.dog_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_reload -> {
                // Sends the click event through the onReloadClickSubject to the subscribing presenter
                onReloadClickSubject.onNext(Object())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        // Prevent possible leaks by unsubscribing all subscribers
        onReloadClickSubject.onComplete()
        super.onDestroy()
    }

    /**
     * Emits items when the user clicks the reload button
     */
    override fun onReloadClick(): Observable<Any> {
        return onReloadClickSubject
    }

    /**
     * Provides the viewmodel for the presenter
     */
    override fun getViewModel(): DogsListViewModel {
        return viewModel
    }
}