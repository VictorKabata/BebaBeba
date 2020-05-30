package com.vickikbt.bebabeba.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vickikbt.bebabeba.R
import com.vickikbt.bebabeba.model.DriversInfo

class DriversNearbyAdapter(val driversList: ArrayList<DriversInfo>) : RecyclerView.Adapter<DriversNearbyAdapter.DriversViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriversViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.driver_found_layout, parent, false)

        return DriversViewHolder(view)
    }

    override fun getItemCount() = driversList.size

    override fun onBindViewHolder(holder: DriversViewHolder, position: Int) {
        val drivers = driversList[position]
        holder.tvDriverName?.text = drivers.Username
    }

    class DriversViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var tvDriverName: TextView? = null

        init {
            this.tvDriverName = row.findViewById(R.id.textView_driver_name)
        }
    }


}

