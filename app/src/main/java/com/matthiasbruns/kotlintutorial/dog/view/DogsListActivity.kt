package com.matthiasbruns.kotlintutorial.dog.view

import android.os.Bundle
import com.matthiasbruns.kotlintutorial.R
import com.pascalwelsch.compositeandroid.activity.CompositeActivity

class DogsListActivity : CompositeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dogs_list)

        if (savedInstanceState == null) {
            val dogsListFragment = DogsListFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, dogsListFragment).commit()
        }
    }


}
