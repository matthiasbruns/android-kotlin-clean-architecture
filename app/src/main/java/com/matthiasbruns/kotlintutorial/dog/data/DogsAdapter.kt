package com.matthiasbruns.kotlintutorial.dog.data

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.matthiasbruns.kotlintutorial.R
import kotlinx.android.synthetic.main.item_dog.view.*

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
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
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
     * position.
     * <p>
     * Note that unlike {@link android.widget.ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     *
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */

    override fun onBindViewHolder(holder: DogsViewHolder?, position: Int) {
        holder!!.bind(_items[position])
    }
}

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