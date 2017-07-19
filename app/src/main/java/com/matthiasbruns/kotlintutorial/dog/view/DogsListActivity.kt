package com.matthiasbruns.kotlintutorial.dog.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.matthiasbruns.kotlintutorial.R

/**
 * This activity holds the DogsListFragment and does nothing special
 */
class DogsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dogs_list)

        if (savedInstanceState == null) {
            // Create the fragment and display it
            val dogsListFragment = DogsListFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, dogsListFragment).commit()
        }
    }

}
