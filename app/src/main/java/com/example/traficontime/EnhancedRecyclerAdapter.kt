package com.example.traficontime

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class EnhancedRecyclerAdapter<T>(@LayoutRes private val resId: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    open var onItemClickListener: ((T) -> Unit)? = null

    private var fullList = listOf<T>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        val textView = LayoutInflater.from(parent.context)
            .inflate(resId, parent, false)
        return object : RecyclerView.ViewHolder(textView) {}
    }

    override fun getItemCount() = fullList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItemAt(position)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }
        bindItem(holder.itemView, item)
    }

    open fun submitList(newList: List<T>) {
        fullList = newList

        notifyDataSetChanged()
    }

    abstract fun bindItem(parentView: View, item: T)

    fun getItemAt(position: Int) = fullList[position]


}
