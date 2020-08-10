package com.firebaseapp.traffic_425b3

import android.view.View
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.item_simple_text.view.*
import javax.inject.Inject

/**
 * Created by Samer on 10/08/2020 10:59.
 */

@ActivityScoped
class TextViewAdapter @Inject constructor() :
    EnhancedRecyclerAdapter<Station>(R.layout.item_simple_text) {
    override fun bindItem(parentView: View, item: Station) {
        parentView.tv_title.text = item.name
    }
}