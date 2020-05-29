package com.vickikbt.bebabeba.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vickikbt.bebabeba.R
import com.vickikbt.bebabeba.model.DriversNearby
import kotlinx.android.synthetic.main.driver_found_layout.view.*

class DriversNearbyAdapter(private val data: List<DriversNearby>) : RecyclerView.Adapter<DriversNearbyAdapter.ViewHolder>() {
    private val items: MutableList<CardView>

    init {
        this.items = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriversNearbyAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.driver_found_layout, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: DriversNearbyAdapter.ViewHolder, position: Int) {
        holder.tvTitle.text = data[position].username
        //items.add(holder.card)
    }

    inner class ViewHolder
    internal constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.textView_driver_name

    }


}