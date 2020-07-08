package com.example.traficontime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_simple_text.view.*
import kotlinx.android.synthetic.main.item_station_record.view.*

class StationTimeTableFragment : Fragment() {

    private lateinit var stationId: String

    private var backgroundDisposable: Disposable? = null
    private val enhancedRecyclerAdapter =
        object : EnhancedRecyclerAdapter<StationRecord>(R.layout.item_station_record) {
            override fun bindItem(parentView: View, item: StationRecord) {
                parentView.tv_time.text = item.time
                parentView.tv_title.text = item.name
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv_saved.adapter = enhancedRecyclerAdapter
        rv_saved.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        enhancedRecyclerAdapter.onItemClickListener = {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onPause() {
        super.onPause()
        backgroundDisposable?.dispose()
    }

    fun setStationId(id: String) {
        stationId = id
        loadData()
    }

    private fun loadData() {
        enhancedRecyclerAdapter.submitList(listOf())
        backgroundDisposable = getBussTimeTable(stationId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                enhancedRecyclerAdapter.submitList(it)
            }, {

            })
    }
}

