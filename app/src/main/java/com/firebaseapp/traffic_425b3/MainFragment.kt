package com.firebaseapp.traffic_425b3

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_simple_text.view.*


class MainFragment : BaseFragment(R.layout.fragment_main) {

    var onItemClickListener: ((SavedStation) -> Unit)? = null
    var onDeleteListener: ((SavedStation) -> Unit)? = null

    private val enhancedRecyclerAdapter =
        object : EnhancedRecyclerAdapter<SavedStation>(R.layout.item_simple_text) {
            override fun bindItem(parentView: View, item: SavedStation) {
                var text = item.name
                if (item.stopPointSet.isNotEmpty()) {
                    text += "|" + item.stopPointSet.joinToString(",")
                }
                if (item.busNameSet.isNotEmpty()) {
                    text += "|" + item.busNameSet.joinToString(",")
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

        val value = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                onDeleteListener?.invoke(enhancedRecyclerAdapter.getItemAt(position))
            }
        }
        val itemTouchHelper = ItemTouchHelper(value)
        itemTouchHelper.attachToRecyclerView(rv_saved)

        enhancedRecyclerAdapter.onItemClickListener = {
            onItemClickListener?.invoke(it)
        }
    }

    fun setSavedList(list: List<SavedStation>) {
        enhancedRecyclerAdapter.submitList(list)
    }

}