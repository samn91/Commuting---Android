package com.example.traficontime

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_station_record.view.*

/**
 * Created by Samer on 08/07/2020 21:14.
 */
class TimeTableAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var fullList = listOf<StationRecord>()

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
        holder.itemView.tv_time.text = (notRelTimePrefix + item.time)
               // item.time.split('T')[1].split(":").slice(0..1).joinToString(":"))
        holder.itemView.tv_station_name.text = (item.name + " " + item.toward)
    }

    fun submitList(newList: List<StationRecord>) {
        fullList = newList
        notifyDataSetChanged()
    }

}