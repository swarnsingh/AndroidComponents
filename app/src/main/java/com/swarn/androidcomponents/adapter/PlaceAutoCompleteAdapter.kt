package com.swarn.androidcomponents.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swarn.androidcomponents.R
import com.swarn.androidcomponents.data.Places


class PlaceAutoCompleteAdapter(itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<PlaceAutoCompleteAdapter.PlaceAutoCompleteViewHolder>() {

    private var mAddresses: ArrayList<Places> = ArrayList()

    private var mItemClickListener: ItemClickListener = itemClickListener

    fun setData(data: ArrayList<Places>) {
        mAddresses.clear()
        mAddresses.addAll(data)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceAutoCompleteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.places_autocomplete_layout, parent, false)

        return PlaceAutoCompleteViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mAddresses.size
    }

    override fun onBindViewHolder(holder: PlaceAutoCompleteViewHolder, position: Int) {
        holder.onBind(position)
    }

    /*override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    filterResults.values = mAddresses
                    filterResults.count = mAddresses.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }*/

    inner class PlaceAutoCompleteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var primaryAddressTxtView = itemView.findViewById<TextView>(R.id.primary_address_txt_view)

        private var fullAddressTxtView = itemView.findViewById<TextView>(R.id.full_address_txt_view)

        fun onBind(position: Int) {
            val place = mAddresses[position]
            primaryAddressTxtView.text = place.primaryAddress
            fullAddressTxtView.text = place.secondaryAddress

            itemView.setOnClickListener {
                mItemClickListener.onItemClick(place)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(place: Places)
    }

}