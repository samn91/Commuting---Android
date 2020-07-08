package com.example.traficontime

import android.graphics.Typeface
import android.view.View
import kotlinx.android.synthetic.main.item_simple_text.view.*

class FilterAdapter : EnhancedRecyclerAdapter<String>(R.layout.item_filter_text) {

    private val selectedSet = mutableSetOf<String>()

    override fun bindItem(parentView: View, item: String) {
        parentView.setOnClickListener { _ ->
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

    override fun submitList(newList: List<String>) {
        selectedSet.clear()
        selectedSet.addAll(newList)
        super.submitList(newList)
    }

    fun getSelectedItem() = selectedSet

    fun setSelectedItem(list: List<String>) {
        selectedSet.addAll(list)
        notifyDataSetChanged()
    }

}