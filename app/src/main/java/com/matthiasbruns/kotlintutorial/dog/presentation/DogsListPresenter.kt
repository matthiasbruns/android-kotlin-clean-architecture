package com.matthiasbruns.kotlintutorial.dog.presentation

import com.matthiasbruns.kotlintutorial.config.PresenterConfig
import com.matthiasbruns.kotlintutorial.dog.data.Dog
import com.matthiasbruns.kotlintutorial.dog.repository.DogRepository
import com.matthiasbruns.kotlintutorial.dog.view.DogsListView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import net.grandcentrix.thirtyinch.TiPresenter
import net.grandcentrix.thirtyinch.rx2.RxTiPresenterDisposableHandler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Bruns on 18.07.2017.
 */
class DogsListPresenter : TiPresenter<DogsListView>() {

    @Inject lateinit var presenterConfig: PresenterConfig

    @Inject lateinit var repository: DogRepository

    private val rxHandler = RxTiPresenterDisposableHandler(this)

    override fun onAttachView(view: DogsListView) {
        super.onAttachView(view)

        subscribeToView(view)
        loadDogs(view)
    }

    private fun subscribeToView(view: DogsListView) {
        view.onReloadClick()
                .debounce(presenterConfig.debounce, TimeUnit.MILLISECONDS)
                .subscribe({ loadDogs(view) })
    }

    private fun createDogLoader(): Single<List<Dog>> {
        return Single.fromCallable { view!!.getViewModel().setLoading(true) }
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap<List<Dog>> { s -> repository.getRandomDogs(10) }
    }

    private fun loadDogs(view: DogsListView) {
        rxHandler.manageDisposable(createDogLoader()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { dogs ->
                    val viewModel = view.getViewModel()
                    viewModel.setDogs(dogs)
                    viewModel.setLoading(false)
                }
        )
    }
}
