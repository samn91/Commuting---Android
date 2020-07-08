package com.example.traficontime

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SimpleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = listOf<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val textView = TextView(parent.context)
        return object : RecyclerView.ViewHolder(textView) {}
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as TextView).text = list[position]
    }

    fun submitList(newList: List<String>) {
        list = newList
        notifyDataSetChanged()
    }

}