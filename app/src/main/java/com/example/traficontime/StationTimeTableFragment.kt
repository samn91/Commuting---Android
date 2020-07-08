package com.example.traficontime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_station_time_table.*

class StationTimeTableFragment : Fragment() {

    private lateinit var stationId: String
    private var backgroundDisposable: Disposable? = null

    private val timeAdapter = TimeTableAdapter()
    private val stationAdapter = FilterAdapter()
    private val stopsAdapter = FilterAdapter()
    private val bussAdapter = FilterAdapter()

    private var timeList = listOf<StationRecord>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_station_time_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        rv_time_table.adapter = timeAdapter
        rv_time_table.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        rv_filter_station.adapter = stationAdapter
        rv_filter_stop.adapter = stopsAdapter
        rv_filter_bus.adapter = bussAdapter

        stopsAdapter.onItemClickListener = {
            resumitTimeList()
        }
        bussAdapter.onItemClickListener = {
            resumitTimeList()
        }
//        timeAdapter.onItemClickListener = {
//            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
//        }
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
        timeAdapter.submitList(listOf())
        backgroundDisposable = getBussTimeTable(stationId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                timeList = it
                stopsAdapter.submitList(it.map { it.stopPoint }.toSet().toList())
                bussAdapter.submitList(it.map { it.name }.toSet().toList())
                resumitTimeList()
            }, {

            })
    }

    private fun resumitTimeList() {
        val filteredList = timeList
            .filter { stopsAdapter.getSelectedItem().contains(it.stopPoint) }
            .filter { bussAdapter.getSelectedItem().contains(it.name) }
        timeAdapter.submitList(filteredList)
    }
}

