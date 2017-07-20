# Android Kotlin Clean-Architecture with MVPVM
This short guide explains how you setup and use Kotlin in a clean-architectural manner.
The example code I use can be found here: 

[https://github.com/matthiasbruns/android-kotlin-clean-architecture](https://github.com/matthiasbruns/android-kotlin-clean-architecture).

If you cannot understand parts of Kotlin, which I do not explain, have a look at their online docs:

[https://kotlinlang.org/docs](https://kotlinlang.org/docs/)


## Project Setup
<u>I use Android Studio 3 Canary 6 in this tutorial.</u>

Create a new project in Android Studio and mark the **Include Kotlin support** checkbox. 
Now we are ready to program in Kotlin.

In this tutorial we will use the following libraries

- Kotlin [https://kotlinlang.org](https://kotlinlang.org)
- Dagger 2 [https://github.com/google/dagger](https://github.com/google/dagger)
- RxAndroid 2 [https://github.com/ReactiveX/RxAndroid](https://github.com/ReactiveX/RxAndroid)
- RxKotlin [https://github.com/ReactiveX/RxKotlin](https://github.com/ReactiveX/RxKotlin)
- ThirtyInch [https://github.com/grandcentrix/ThirtyInch](https://github.com/grandcentrix/ThirtyInch)
- Architecture Components [https://developer.android.com/topic/libraries/architecture/index.html](https://developer.android.com/topic/libraries/architecture/index.html)
- Retrofit 2 [http://square.github.io/retrofit](http://square.github.io/retrofit/)
- CompositeAndroid [https://github.com/passsy/CompositeAndroid](https://github.com/passsy/CompositeAndroid)
- Glide [https://github.com/bumptech/glide](https://github.com/bumptech/glide)

The resulting build.gradle can be found here:

[https://github.com/matthiasbruns/android-kotlin-clean-architecture/blob/master/app/build.gradle](https://github.com/matthiasbruns/android-kotlin-clean-architecture/blob/master/app/build.gradle)

We also have to enable kotlin in the project build.gradle:

````gradle
buildscript {
    ext.kotlin_version = '1.1.3-2'
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.0-alpha7'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}
````

## Dogs API

I picked a useless but funny API which provides images of dogs.
The documentation of the api can be found here:

[http://docs.dogapi1.apiary.io](http://docs.dogapi1.apiary.io/#)

A sample request looks like this:
````
http://thedogapi.co.uk/api/v1/dog?limit=20
````

The api limits the dogs count to 20 per request.

## Dog Model
The dog model class is a simple pojo, which stores the properties of a dog.

````kotlin
/**
 * The model class to store dog data in.
 *
 * @param id the id of the dog image
 * @param url the url to the image
 * @param time the timestamp when the images was shot
 * @param format the format the picture is stored in
 */
data class Dog(val id: String, val url: String, val time: String, val format: String)
````

## Retrofit
If you are interested in how to setup Retrofit with RxAndroid 2 have a look at my other tutorial 
 
[https://medium.com/@mtrax/rxandroid-2-with-retrofit-2-and-gson-3f08d4c2627d](https://medium.com/@mtrax/rxandroid-2-with-retrofit-2-and-gson-3f08d4c2627d)

````kotlin
/**
 * This service is based on the dogs api
 * http://docs.dogapi1.apiary.io/#reference/0/get-dog/get-a-random-dog
 */
interface DogApi {

    /**
     * You can request dogs from there - the limit is maxed to 20 per request
     */
    @GET("/api/v1/dog")
    fun getRandom(@Query("limit") @IntRange(from = 1, to = 20) limit: Int): Single<DogsResponse>
}
````

The DogApi will be implemented by Retrofit. It has one method, which loads a random collection of dogs.
The parameter **max** limits the maximum amount of loaded dogs. It should be between 1 and 20.
The methods returns a rx.Single which emits a DogsResponse object.

````kotlin
/**
 * This POJO holds the response data for the DogApi requests
 *
 * @param data the actual array of dog objects
 * @param count the amount of dog objects in this response
 * @param error an optional error if the service threw one
 */
class DogsResponse(val data: List<Dog>, val count: Int, val error: String?)
````

The DogsResponse is a simple class which holds the response of the api request.

## Dog Repository
The DogRepository implements the Repository Pattern. 
The pattern provides an interface or abstract class which defines the methods 
to interact with the datasource. In our case we have one method to get a random collection of dogs.
Additionally we could have a method, which gets a dog by its id or add a dog to the api.

````kotlin
/**
 * The dog repository provides endpoints for the caller to interact with dog data.
 */
interface DogRepository {
    /**
     * Loads a random collection of dogs.
     * @param max the maximum amount of loaded dogs
     * @return a Single which emits a List of dogs
     */
    fun getRandomDogs(@IntRange(from = 1, to = 20) max: Int): Single<List<Dog>>
}
````

We have one implementation of this repository, which interacts with the DogApi.

````kotlin
/**
 * The dog repository provides endpoints for the caller to interact with dog data.
 * This implementation's data source is a rest service.
 */
class DogRestRepository(private val dogApi: DogApi) : DogRepository {

    /**
     * Loads a random collection of dogs.
     * This implementation loads the dogs from a rest service.
     *
     * @param max the maximum amount of loaded dogs
     * @return a Single which emits a List of dogs
     */
    @SuppressLint("Range")
    override fun getRandomDogs(@IntRange(from = 1, to = 20) max: Int): Single<List<Dog>> {
        // Query the service
        return dogApi.getRandom(max)
                .subscribeOn(Schedulers.io())
                .map { dogsResponse: DogsResponse ->
                    // if there was an error, throw an exception
                    if (dogsResponse.error != null) {
                        throw RuntimeException(dogsResponse.error)
                    }

                    // Return the list of dogs
                    return@map dogsResponse.data
                }
    }
}
````

What does this implementation do?
We inject an implementation of the DogsApi into this class.
When someone calls 

````kotlin
getRandomDogs(@IntRange(from = 1, to = 20) max: Int): Single<List<Dog>>
````

the repository requests dogs from the api. If the response has an error, the error will be thrown.
If we have response data, the data will be returned.

We could add another implementation, which interacts with data in a cache or database,
one which synchronizes data with a cloud storage and another which is a file based repository.
As you can see, we are not limited to one datasource.

## Dagger 2

To keep everything decoupled, we will use Dagger for our dependency injection.
The advantage of this approach is that Dagger takes care about choosing the right implementation for e.g. our repositories.
It also initializes Retrofit and provides resources to our presenter.

### Application Component

````kotlin
/**
 * This component should be used to inject application scoped dependencies.
 */
@ForApplication
@Component(modules = arrayOf(AppModule::class))
interface ApplicationComponent {

    fun inject(application: KotlinApplication)
}

````
This components uses the AppModule, which provides application-wide dependencies like the AppContext.
 
````kotlin
/**
 * This module should be used to inject application scoped dependencies.
 */
@Module
class AppModule(private val application: KotlinApplication) {

    @Singleton
    @Provides fun provideApplication(): KotlinApplication = application

    @Singleton
    @Provides fun provideApplicationContext(): Context {
        return application
    }
}

````

The AppModule knows how to provide the ApplicationContext and the KotlinApplication to injection targets.

The KotlinApplication class creates the component above.

````kotlin
/**
 * Entry point of this app.
 * Initializes dependency injection.
 */
class KotlinApplication : Application() {

    companion object {
        /**
         * The ApplicationComponent for the dependency injection context.
         */
        @JvmStatic private lateinit var appComponent: ApplicationComponent

        /**
         * The AppModule. which was created during the setup
         */
        @JvmStatic private lateinit var appModule: AppModule

        /**
         * Returns the appModule for this application.
         * Use this, if you have a dependency to the AppModule in you Components.
         */
        fun module(): AppModule {
            return appModule
        }

        /**
         * Provides the created ApplicationComponent for this app.
         */
        fun component(): ApplicationComponent {
            return appComponent
        }
    }

    /**
     * Lazy initialized ApplicationComponent
     */
    private val component: ApplicationComponent by lazy {
        appModule = AppModule(this)
        DaggerApplicationComponent.builder()
                .appModule(appModule)
                .build()
    }

    override fun onCreate() {
        super.onCreate()

        // Inject application dependencies
        component.inject(this)
        appComponent = component
    }
}
````

Ignore the **companion object** for now. We start with this snippet:

````kotlin
/**
 * Lazy initialized ApplicationComponent
 */
private val component: ApplicationComponent by lazy {
    appModule = AppModule(this)
    DaggerApplicationComponent.builder()
            .appModule(appModule)
            .build()
}
````

The components is stored in the application class as a val (write-once) property.
The components is created on its first access. This is indicated by **by lazy**.
The code in the curly braces will be called during the initialization, which 
creates an instance of the ApplicationComponents with the Dagger builder.

````kotlin
override fun onCreate() {
    super.onCreate()

    // Inject application dependencies
    component.inject(this)
    appComponent = component
}
````

The Application's onCreate method will be called when the app is created. We access the component, 
which is being created lazily and inject dependencies we might need in the Application class (currently none).
In the next line, we set the property in the companion object.

````kotlin
 companion object {
        /**
         * The ApplicationComponent for the dependency injection context.
         */
        @JvmStatic private lateinit var appComponent: ApplicationComponent

        /**
         * The AppModule. which was created during the setup
         */
        @JvmStatic private lateinit var appModule: AppModule

        /**
         * Returns the appModule for this application.
         * Use this, if you have a dependency to the AppModule in you Components.
         */
        fun module(): AppModule {
            return appModule
        }

        /**
         * Provides the created ApplicationComponent for this app.
         */
        fun component(): ApplicationComponent {
            return appComponent
        }
    }
````

What is a **companion object**? In Kotlin there are no static properties. To emulate this behaviour, Kotlin
has these objects. Properties in this object can be accessed through the class and not an object of the class.
The **@JvmStatic** annotation tells the compiler to mark the properties as static in the JVM.

### Dog Component

This component provides dependencies for the dog domain in this app.

````kotlin
/**
 * This dagger component provides all required dependencies for the dog domain to work.
 */
@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        DogModule::class,
        NetworkModule::class,
        PresentationModule::class
))
interface DogComponent {
    fun inject(activity: DogsListActivity)
    fun inject(fragment: DogsListFragment)
    fun inject(presenter: DogsListPresenter)
}
````

As you can see, this components allows the injection of three classes:

* DogListActivity
* DogListFragment
* DogListPresenter

The DogComponent uses four modules:

* AppModule
* PresentationModule
* NetworkModule
* DogModule

The AppModule was describes above.

### PresentationModule

````kotlin
/**
 * This module provides presenter dependencies.
 */
@Module
class PresentationModule {

    @Provides
    @Singleton
    fun providePresenterConfig(): PresenterConfig {
        return PresenterConfig()
    }
}
````

This module provides the PresenterConfig for this app.

````kotlin
/**
 * App-white presenter configuration for equal behavior in all presenters.
 */
class PresenterConfig {

    /**
     * The click debounce time in ms for all presenters
     */
    val clickDebounce: Long = 150
}
````

The PresenterConfig provides properties for the presenters to act equal. The **clickDebounce** will be used
to debounce user clicks.

### NetworkModule

````kotlin
/**
 * This module provides retrofit and handles its creation.
 */
@Module
class NetworkModule {

    /**
     * Creates an instance of Retrofit for this app
     */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl("http://thedogapi.co.uk")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }
}
````

This module provides a default Retrofit object to create api endpoints with.

### DogModule

````kotlin
/**
 * The DogModule injects a DogRepository implementation into the target.
 */
@Module
class DogModule {

    /**
     * @param retrofit required to create the repository implementation for the DogRepository
     * @return an implementation of the DogRepository
     */
    @Provides
    @Singleton
    fun provideDogsRepository(retrofit: Retrofit): DogRepository {
        // You can decide by whatever params which repo you want to inject

        return DogRestRepository(retrofit.create(DogApi::class.java))
    }
}
````

The DogModule injects a DogRepository into a injection target. 
It requires Retrofit to inject the DogRestRepository.
Retrofit will be injected by the NetworkModule.

## MVPVM

After we went through the whole model part of MVPVM, we can start looking at the view, viewmodel and presenter.

### Adapter and ViewHolder
We need an adapter for the RecyclerView. The DogsAdapter knows how to load the images and display
the dog information in an item view.

````kotlin
/**
 * This adapter displays dogs in a RecyclerView.
 * Use [dogs] to update the data stores in this adapter.
 */
class DogsAdapter : RecyclerView.Adapter<DogsViewHolder>() {

    /**
     * Hidden backing property to store the displayed dog list
     */
    private val _items = mutableListOf<Dog>()

    /**
     * Sets the content of this adapter. The [dogs] list can be null or empty.
     * In that case, the adapter won't render anything.
     */
    var dogs: List<Dog>? get() = _items.toList()
        set(value) {
            // Clear the data
            _items.clear()

            // Set new data is not null
            if (value != null) {
                _items.addAll(value)
            }

            // Notify the adapter
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item...
     */
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DogsViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_dog, parent, false)
        return DogsViewHolder(view)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return _items.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position...
     */

    override fun onBindViewHolder(holder: DogsViewHolder?, position: Int) {
        holder!!.bind(_items[position])
    }
}
````

The only interesting part here are the properties:

````kotlin
/**
 * Hidden backing property to store the displayed dog list
 */
private val _items = mutableListOf<Dog>()

/**
 * Sets the content of this adapter. The [dogs] list can be null or empty.
 * In that case, the adapter won't render anything.
 */
var dogs: List<Dog>? get() = _items.toList()
    set(value) {
        // Clear the data
        _items.clear()

        // Set new data is not null
        if (value != null) {
            _items.addAll(value)
        }

        // Notify the adapter
        notifyDataSetChanged()
    }
````

We store the dog data in an internal MutableList. To allow other classes to change the data,
we provide a property which changes the contents of the internal property.
More about backing properties can be read here:

[https://kotlinlang.org/docs/reference/properties.html#backing-properties](https://kotlinlang.org/docs/reference/properties.html#backing-properties)

The public available data property has a custom getter and setter.
The getter simply returns the contents of the internal dog list.
The setter is a bit more complex. It replaces the contents of the _items list and notifies the 
adapter after that. Since it is possible to send null values into this setter, null will clear the 
_items list which results in an empty RecyclerView.

````kotlin
/**
 * This [DogsViewHolder] binds dog data to the view.
 */
class DogsViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    /**
     * Binds the injected view of this ViewHolder to the Dog object
     */
    fun bind(dog: Dog) {
        itemView.dog_type_view.text = dog.format.toLowerCase()
        itemView.dog_date_view.text = dog.time

        Glide.with(itemView.context)
                .load(dog.url)
                .into(itemView.dog_image_view)

    }
}
````

The DogsViewHolder loads the dog image and sets the TextViews' texts for each item. 

### DogsListViewModel

````kotlin
/**
 * This model stores the data required by the view.
 * The view can observice this viewmodel and its properties and react to changes.
 */
class DogsListViewModel : viewmodel() {

    /**
     * Holds a list of dog data which can be observed and changed
     */
    private val dogsLiveData = MutableLiveData<List<Dog>>()

    /**
     * Holds the state of loading
     */
    private val loadingLiveData = MutableLiveData<Boolean>()

    /**
     * Observable ListData of dogs
     */
    fun getDogs(): LiveData<List<Dog>> {
        return dogsLiveData
    }

    /**
     * Indicator to show or hide loading informations
     */
    fun isLoading(): LiveData<Boolean> {
        return loadingLiveData
    }

    /**
     * Sets the dog list contents
     */
    fun setDogs(
            dogsLiveData: List<Dog>) {
        this.dogsLiveData.value = dogsLiveData
    }

    /**
     * Sets the loading state
     */
    fun setLoading(loadingLiveData: Boolean) {
        this.loadingLiveData.value = loadingLiveData
    }
}
````

The viewmodel is a POJO with LiveData properties to enable the view to observe its properties.
This viewmodel stores two properties:

* dogs - a List of Dog objects
* loading - indicates if the loading dialog should be displayed

### DogsListView

````kotlin
/**
 * This view connects the view implementation with a presenter.
 */
interface DogsListView : TiView {

    /**
     * Emits items when the user clicks the reload button
     */
    fun getViewModel(): DogsListViewModel

    /**
     * Provides the viewmodel for the presenter
     */
    fun onReloadClick(): Observable<Any>
}
````

The view interface is quite simple for this use case.
The presenter should be able to observe to the reload click event and retrieve the viewmodel 
to set values on it.

### DogsListFragment

The fragment manages the UI and has access to the Android classes.
Since we use **kotlin-android-extensions**, we do not need a view injection framework like ButterKnife.
This import statement allows us to access the views by its ids:

````kotlin
import kotlinx.android.synthetic.main.fragment_dogs_list.*
````

````kotlin
class DogsListFragment : CompositeFragment(), DogsListView, LifecycleRegistryOwner
````

Our fragment extends the CompositeFragment provides by the CompositeAndroid library.
It also implements DogListView and LifecycleRegistryOwner. The view interface acts as a bridge 
between the DogListPresenter and this fragment. 
LifecycleRegistryOwner provides lifecycle information for the viewmodel we observe in this class.

````kotlin
/**
 * The DogsAdapter stores the displayed dogs and injects item views per row
 */
private val adapter = DogsAdapter()

/**
 * The DogsListPresenter holds the presentation logic for this view
 */
private val presenter = DogsListPresenter()

/**
 * The LifecycleRegistry is required to allow the viewmodel to observe the lifecycle of this fragment
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
````

The DogsListFragment stores the properties above. The first is the adapter, which stores and fills the item
layouts for the RecyclerView. The second is the presenter, which we use in the MVP pattern.
The third property links the Android Architecture Components Lifecycle feature with our fragment.
The forth emits items to its subscribers, when the user clicks the reload button. 
Dialog is a reference to a displayed dialog. It can be null. The last property
stores the reference to our viewmodel.


**lateinit var** means, that Kotlin expects this properties to be initializes later.

As mentioned in the beginning, I use a composition framework to get rid of deep inheritance graphs.

````kotlin
init {
    // Adds the MVP framework to our fragment - we could also extend TiFragment, but I am no fan of inheritance
    addPlugin(TiFragmentPlugin<DogsListPresenter, DogsListView>(TiPresenterProvider { presenter }))
}
````

To add the MVP framework ThirtyInch to our fragment, we add a plugin in the init method, which links
the DogsListPresenter to our fragment. If you want to know more about composition and the framework,
head to its GitHub repository mentioned in the beginning.

````kotlin
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
````

The same pattern as describes in the KotlinApplication class. We simply create the components we need
to inject dependencies in injection targets for this domain.

````kotlin
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
````

In **onCreate** we enable the fragment to inflate its menu. We also create the onReloadClickSubject,
which can be subscribed to after this line. After the injection lines, we request an instance of 
DogsListViewModel and store it in our fragment. **subscribeToViewModel()** observes changes in the
viewmodel ans updates the UI if required.

````kotlin
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
````

The first observation is linked to the dog list data in the viewmodel. If the content of the list
changes, the viewmodel will notify us. We simply send the changed data to the adapter.

The seconds one observes changes to the loading flag in the viewmodel. If loading is true, a loading
dialog will be shown.

````kotlin
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
````

When the user clicks the reload icon in the ToolBar, onReloadClickSubject.onNext(Object()) emits an
object to its subscribers.

### DogListPresenter
 The presenter loads data from a repository and sets the results in the DogListViewModel.
 It also reacts to user events published through Observables in the view and could also 
 trigger actions on the view (if there were any).
 
 ````kotlin
companion object {
    /**
     * TAG for the Android logger
     */
    @JvmStatic val TAG = DogsListPresenter::class.java.simpleName!!
}

/**
 * Injected
 * A config for this presenter.
 */
@Inject lateinit var presenterConfig: PresenterConfig

/**
 * Injected
 * A DogRepository implementation
 */
@Inject lateinit var repository: DogRepository

/**
 * Unsubscribes rx subscriptions when needed
 */
private val rxHandler = RxTiPresenterDisposableHandler(this)

/**
 * Internal presenter cache to store dog data
 */
private val dogCache: MutableList<Dog> = mutableListOf()
````

The companion object stores the logging tag for this presenter.
The two @Inject properties will be injected by Dagger.
RxTiPresenterDisposableHandler disposes Disposables when the view detaches or the presenter is destroyed.
The dogCache is our runtime cache to store loaded dogs during e.g. rotation events.

````kotlin
/**
 * Called when the view was attached to this presenter (when it is available)
 */
override fun onAttachView(view: DogsListView) {
    super.onAttachView(view)

    // Listen to view based events
    subscribeToView(view)

    if (dogCache.isEmpty()) {
        // load the dog data
        loadDogs(view)
    } else {
        renderDogs(view, dogCache)
    }
}
````

This method is called by ThirtyInch when the view was created. 
First we subscribe to view Observables.
After that we decide, if we should load data or redraw our cached dogs.

````kotlin
/**
 * Subscribes to every view Observable.
 */
private fun subscribeToView(view: DogsListView) {
    // Reacts to the reload click and gets some new dogs - yay!
    rxHandler.manageViewDisposable(view.onReloadClick()
            // clickDebounce will provide a buffer if the user plays monkey on the reload button
            .debounce(presenterConfig.clickDebounce, TimeUnit.MILLISECONDS)
            // Cheap way to trigger a reload of the doggies
            .subscribe({ loadDogs(view) })
    )
}
````

**subscribeToView()** subscribes to the onReloadClick. The view emits items, when the user clicks on the reload button.
To prevent click spamming, we added a debounce opterator. The subscription will call loadDogs().

````kotlin
/**
 * Creates the dog loading logic wrapped in a Single.
 * Will also tell the view to show the loading indicator
 */
private fun createDogLoader(): Single<List<Dog>> {
    return Single.fromCallable { view!!.getViewModel().setLoading(true) }
            .subscribeOn(AndroidSchedulers.mainThread())
            .flatMap<List<Dog>> { _ -> repository.getRandomDogs(10) }
            .map { dogs ->
                dogCache.clear()
                dogCache.addAll(dogs)
                return@map dogs
            }
}

/**
 * Loads the dogs from the repository and sets the result in the viewmodel.
 * Also disables the loading indicator in the view.
 */
private fun loadDogs(view: DogsListView) {
    rxHandler.manageDisposable(createDogLoader()
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
            .onErrorReturn { throwable ->
                Log.e(TAG, "Could not load cute little doggy pictures.", throwable)
                return@onErrorReturn listOf()
            }
            .subscribe { dogs ->
                renderDogs(view, dogs)
            }
    )
}

private fun renderDogs(view: DogsListView, dogs: List<Dog>) {
    val viewModel = view.getViewModel()
    viewModel.setDogs(dogs)
    viewModel.setLoading(false)
}
````
**loadDogs()** asks the repository for a collection of dogs. It does not know where the dogs are loaded from.
After the dogs were loaded, the result is sent to the viewmodel.

## Summary

We built an app in Kotlin with some good libraries and upcoming Android frameworks as the viewmodel class from the
Android Architecture Components. We used Retrofit 2 to load the data from an api and Dagger 2 to manage 
dependency injection. There are many MVP frameworks out there. I picked ThirtyInch, because I use it in my job.
RxJava 2 is good to get rid of the callback hell and improve threading usage.

## Conclusion

If you decide to use MVPVM and want to have a clean architecture, dependency injection is a good way
to decouple classes. The repository pattern allows you to switch to or add new datasources. The new
viewmodel and Lifecycle classes in the upcoming Architecture Components improve the MVP pattern and
provides another layer of abstraction.

