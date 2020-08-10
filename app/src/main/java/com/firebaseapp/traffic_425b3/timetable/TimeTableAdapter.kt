package com.firebaseapp.traffic_425b3.timetable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebaseapp.traffic_425b3.R
import com.firebaseapp.traffic_425b3.StationRecord
import com.firebaseapp.traffic_425b3.showFormatedTime
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.android.synthetic.main.item_station_record.view.*
import javax.inject.Inject

/**
 * Created by Samer on 08/07/2020 21:14.
 */

@ActivityScoped
class TimeTableAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var fullList = listOf<StationRecord>()
    private var showStationName = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_station_record, parent, false)
        return object : RecyclerView.ViewHolder(textView) {}
    }

    override fun getItemCount() = fullList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = fullList[position]

        val notRelTimePrefix = if (item.isRealTime) "" else "*"
        holder.itemView.tv_time.text = (notRelTimePrefix + item.time.showFormatedTime())
        val stationName = if (showStationName) item.stationName + ": " else ""
        holder.itemView.tv_station_name.text =
            (stationName + item.busName + " " + item.toward)
    }

    fun submitList(newList: List<StationRecord>) {
        fullList = newList
        showStationName = fullList.map { it.stationName }.toSet().size > 1
        notifyDataSetChanged()
    }

}