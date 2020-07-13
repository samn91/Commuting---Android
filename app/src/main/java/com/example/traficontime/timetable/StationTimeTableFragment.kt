package com.example.traficontime.timetable

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.traficontime.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_station_time_table.*

class StationTimeTableFragment : BaseFragment(R.layout.fragment_station_time_table) {

    private lateinit var station: SavedStation
    private var backgroundDisposable: Disposable? = null

    private val timeAdapter =
        TimeTableAdapter()
    private val stationAdapter =
        FilterAdapter()
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

        stopsAdapter.onItemClickListener = {
            resubmitTimeList()
        }
        bussAdapter.onItemClickListener = {
            resubmitTimeList()
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

    fun setStation(savedStation: SavedStation) {
        station = savedStation
    }

    private fun loadData() {
        MainActivity.idlingResource.increment()
        stopsAdapter.submitList(listOf())
        bussAdapter.submitList(listOf())
        timeAdapter.submitList(listOf())
        backgroundDisposable = getBussTimeTable(
            station.id
        ).observeOn(AndroidSchedulers.mainThread())
            .doFinally {
            MainActivity.idlingResource.decrement()
        }
            .subscribe({ recordList ->
                timeList = recordList
                val allStops = recordList.map { it.stopPoint }.toSet()
                val allBuses = recordList.map { it.name }.toSet()
                stopsAdapter.submitList(allStops.toList())
                bussAdapter.submitList(allBuses.toList())

                stopsAdapter.setSelectedItem(if (station.stopPoint.isEmpty()) allStops else station.stopPoint)
                bussAdapter.setSelectedItem(if (station.busName.isEmpty()) allBuses else station.busName)
                resubmitTimeList()
            }, logError)
    }

    private fun resubmitTimeList() {
        station = station.copy(
            stopPoint = stopsAdapter.getSelectedItem(),
            busName = bussAdapter.getSelectedItem()
        )
        val filteredList = timeList
            .filter { station.stopPoint.contains(it.stopPoint) }
            .filter { station.busName.contains(it.name) }
        timeAdapter.submitList(filteredList)
    }

    fun getStation() = station
}

