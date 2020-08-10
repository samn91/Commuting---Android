package com.firebaseapp.traffic_425b3

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_search_station.*
import javax.inject.Inject

@ActivityScoped
@AndroidEntryPoint
class SearchStationFragment @Inject constructor(val adapter: TextViewAdapter) :
    BaseFragment(R.layout.fragment_search_station) {

    var onItemClickListener: ((Station) -> Unit)? = null

    private var backgroundDisposable: Disposable? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.onItemClickListener = onItemClickListener

        rv_search.adapter = adapter
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
                        adapter.submitList(it)
                    }, logError)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        backgroundDisposable?.dispose()
    }
}