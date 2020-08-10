package com.firebaseapp.traffic_425b3

import android.view.View
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.android.synthetic.main.item_simple_text.view.*
import javax.inject.Inject

/**
 * Created by Samer on 10/08/2020 10:56.
 */

@ActivityScoped
class FavoriteAdapter @Inject constructor() :
    EnhancedRecyclerAdapter<SavedStation>(R.layout.item_simple_text) {
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