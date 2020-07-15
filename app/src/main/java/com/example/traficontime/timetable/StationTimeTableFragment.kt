package com.example.traficontime.timetable

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.traficontime.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_station_time_table.*

class StationTimeTableFragment : BaseFragment(R.layout.fragment_station_time_table) {

    private lateinit var stationList: List<SavedStation>
    private var backgroundDisposable: Disposable? = null

    private val timeAdapter = TimeTableAdapter()
    private val stationAdapter = FilterAdapter()
    private val stopsAdapter = FilterAdapter()
    private val bussAdapter = FilterAdapter()

    private var timeList = listOf<StationRecord>()

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

        stationAdapter.onItemClickListener = {
            resubmitTimeList()
        }
        stopsAdapter.onItemClickListener = {
            resubmitTimeList()
        }
        bussAdapter.onItemClickListener = {
            resubmitTimeList()
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

    fun setStation(savedStation: List<SavedStation>) {
        if (savedStation.size == 1) {
            // updateHeader()
            //hideStationSelection
        }
        stationList = savedStation
    }

    private fun loadData() {
        MainActivity.idlingResource.increment()
        stationAdapter.clearAdapter()
        stopsAdapter.clearAdapter()
        bussAdapter.clearAdapter()
        timeAdapter.submitList(listOf())
        val calls = stationList.map {
            getBussTimeTable(
                it.id, it.name
            )
        }
        backgroundDisposable =
            Single.zip(
                calls
            ) {
                it.map { it as List<StationRecord> }.flatten()
            }.observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    MainActivity.idlingResource.decrement()
                }
                .subscribe({ recordList ->
                    timeList = recordList
                    val allStops = timeList.map { it.stopPoint }.toSet()
                    val allBuses = timeList.map { it.busName }.toSet()

                    stationAdapter.submitList(stationList.map { it.name })
                    stationAdapter.setSelectedItem(stationList.map { it.name }.toSet())

                    stopsAdapter.submitList(allStops.toList())
                    bussAdapter.submitList(allBuses.toList())

                    rv_filter_station.show(stationList.size > 1)
                    rv_filter_stop.show(allStops.size > 1)

                    if (stationList.size == 1) {
                        val station = stationList.first()
                        stopsAdapter.setSelectedItem(if (station.stopPointSet.isEmpty()) allStops else station.stopPointSet)
                        bussAdapter.setSelectedItem(if (station.busNameSet.isEmpty()) allBuses else station.busNameSet)
                    } else {
                        stopsAdapter.setSelectedItem(allStops)
                        bussAdapter.setSelectedItem(allBuses)
                    }

                    resubmitTimeList()
                }, logError)
    }

    private fun resubmitTimeList() {
        val filteredList = timeList
            .filter { stationAdapter.getSelectedItem().contains(it.stationName) }
            .filter { stopsAdapter.getSelectedItem().contains(it.stopPoint) }
            .filter { bussAdapter.getSelectedItem().contains(it.busName) }
            .sortedBy { it.time }
        timeAdapter.submitList(filteredList)
    }

    fun getStationList() = stationList
}

