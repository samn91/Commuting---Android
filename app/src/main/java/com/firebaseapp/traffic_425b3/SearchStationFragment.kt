package com.firebaseapp.traffic_425b3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_search_station.*
import kotlinx.android.synthetic.main.item_simple_text.view.*


class SearchStationFragment : BaseFragment(R.layout.fragment_search_station) {

    var onItemClickListener: ((Station) -> Unit)? = null

    private var backgroundDisposable: Disposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val enhancedRecyclerAdapter =
            object : EnhancedRecyclerAdapter<Station>(R.layout.item_simple_text) {
                override fun bindItem(parentView: View, item: Station) {
                    parentView.tv_title.text = item.name
                }
            }

        enhancedRecyclerAdapter.onItemClickListener = onItemClickListener

        rv_search.adapter = enhancedRecyclerAdapter
        rv_search.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        et_search.doAfterTextChanged { text ->
            if (text?.length ?: 0 > 3) {
                MainActivity.idlingResource.increment()
                backgroundDisposable?.dispose()
                backgroundDisposable = getStations(text.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        MainActivity.idlingResource.decrement()
                    }
                    .subscribe({
                        enhancedRecyclerAdapter.submitList(it)
                    }, logError)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        backgroundDisposable?.dispose()
    }
}