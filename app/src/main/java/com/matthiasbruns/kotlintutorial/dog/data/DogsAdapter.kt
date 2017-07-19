package com.matthiasbruns.kotlintutorial.dog.data

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.matthiasbruns.kotlintutorial.R
import kotlinx.android.synthetic.main.item_dog.view.*

/**
 * Created by Bruns on 19.07.2017.
 */

class DogsAdapter : RecyclerView.Adapter<DogsViewHolder>() {

    private val _items = mutableListOf<Dog>()

    var dogs: List<Dog>? get() = _items.toList()
        set(value) {
            _items.clear()
            if (value != null) {
                _items.addAll(value)
            }
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DogsViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_dog, parent, false)
        return DogsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return _items.size
    }

    override fun onBindViewHolder(holder: DogsViewHolder?, position: Int) {
        holder!!.bind(_items[position])
    }
}

class DogsViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    fun bind(dog: Dog) {
        itemView.dog_type_view.text = dog.format.toLowerCase()
        itemView.dog_date_view.text = dog.time

        Glide.with(itemView.context)
                .load(dog.url)
                .into(itemView.dog_image_view)

    }
}