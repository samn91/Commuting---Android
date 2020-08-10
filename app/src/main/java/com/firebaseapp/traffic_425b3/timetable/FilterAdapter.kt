package com.firebaseapp.traffic_425b3.timetable

import android.graphics.Typeface
import android.view.View
import com.firebaseapp.traffic_425b3.EnhancedRecyclerAdapter
import com.firebaseapp.traffic_425b3.R
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.android.synthetic.main.item_simple_text.view.*
import javax.inject.Inject

@ActivityScoped
class FilterAdapter @Inject constructor() : EnhancedRecyclerAdapter<String>(
    R.layout.item_filter_text
) {

    private val selectedSet = mutableSetOf<String>()

    override fun bindItem(parentView: View, item: String) {
        parentView.setOnClickListener {
            if (selectedSet.size == itemCount) {
                selectedSet.clear()
                selectedSet.add(item)
                onItemClickListener?.invoke(item)
                notifyDataSetChanged()
                return@setOnClickListener
            }
            if (selectedSet.contains(item)) {
                selectedSet.remove(item)
            } else {
                selectedSet.add(item)
            }
            val style = if (selectedSet.contains(item)) Typeface.BOLD else Typeface.NORMAL
            parentView.tv_title.typeface = Typeface.create(parentView.tv_title.typeface, style)
            onItemClickListener?.invoke(item)
        }
        parentView.tv_title.text = item
        val style = if (selectedSet.contains(item)) Typeface.BOLD else Typeface.NORMAL
        parentView.tv_title.typeface = Typeface.create(parentView.tv_title.typeface, style)
    }

    fun getSelectedItem() = selectedSet

    fun setSelectedItem(list: Set<String>) {
        selectedSet.addAll(list)
        notifyDataSetChanged()
    }

}