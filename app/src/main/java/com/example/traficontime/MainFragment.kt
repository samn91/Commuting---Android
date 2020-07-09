package com.example.traficontime

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_simple_text.view.*

class MainFragment : BaseFragment(R.layout.fragment_main) {

    var onItemClickListener: ((SavedStation) -> Unit)? = null

    private val enhancedRecyclerAdapter =
        object : EnhancedRecyclerAdapter<SavedStation>(R.layout.item_simple_text) {
            override fun bindItem(parentView: View, item: SavedStation) {
                var text = item.name
                if (item.stopPoint.isNotEmpty()) {
                    text += "|" + item.stopPoint.joinToString(",")
                }
                if (item.busName.isNotEmpty()) {
                    text += "|" + item.busName.joinToString(",")
                }
                parentView.tv_title.text = text
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_saved.adapter = enhancedRecyclerAdapter
        rv_saved.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        enhancedRecyclerAdapter.onItemClickListener = {
            onItemClickListener?.invoke(it)
        }
    }

    fun setSavedList(list: List<SavedStation>) {
        enhancedRecyclerAdapter.submitList(list)
    }

}