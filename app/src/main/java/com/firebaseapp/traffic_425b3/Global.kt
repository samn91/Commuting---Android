package com.firebaseapp.traffic_425b3

import android.util.Log
import android.view.View
import java.text.SimpleDateFormat
import java.util.*

fun View.show(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

val timeDateFormat = SimpleDateFormat("HH:mm")
fun Date.showFormatedTime() = timeDateFormat.format(this)