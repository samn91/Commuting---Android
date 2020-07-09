package com.example.traficontime

import android.util.Log
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment {

    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    val logError = { e: Throwable ->
        Log.e("Global", ": ", e)
        Toast.makeText(requireActivity(), e.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}