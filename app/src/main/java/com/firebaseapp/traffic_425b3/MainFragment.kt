package com.firebaseapp.traffic_425b3

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

@ActivityScoped
@AndroidEntryPoint
class MainFragment @Inject constructor(val adapter: FavoriteAdapter) :
    BaseFragment(R.layout.fragment_main) {

    var onItemClickListener: ((SavedStation) -> Unit)? = null
    var onDeleteListener: ((SavedStation) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_saved.adapter = adapter
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
                onDeleteListener?.invoke(adapter.getItemAt(position))
            }
        }
        val itemTouchHelper = ItemTouchHelper(value)
        itemTouchHelper.attachToRecyclerView(rv_saved)

        adapter.onItemClickListener = {
            onItemClickListener?.invoke(it)
        }

    }

    fun setSavedList(list: List<SavedStation>) {
        adapter.submitList(list)
        tv_main_hint?.show(adapter.itemCount == 0)
    }

}