package com.firebaseapp.traffic_425b3

import android.view.View
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.item_saved_text.view.*
import javax.inject.Inject

/**
 * Created by Samer on 10/08/2020 10:56.
 */

@ActivityScoped
class FavoriteAdapter @Inject constructor() :
    EnhancedRecyclerAdapter<SavedStation>(R.layout.item_saved_text) {
    override fun bindItem(parentView: View, item: SavedStation) {
        parentView.tv_title.text = item.name

        val subtitle = mutableListOf<String>()
        var text = ""
        if (item.stopPointSet.isNotEmpty()) {
            subtitle.add(item.stopPointSet.joinToString(", "))
        }
        if (item.busNameSet.isNotEmpty()) {
            subtitle.add(item.busNameSet.joinToString(", "))
        }
        parentView.tv_subtitle.show(subtitle.isNotEmpty())
        parentView.tv_subtitle.text = subtitle.joinToString("\n")
    }
}