package com.example.traficontime

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.traficontime.timetable.StationTimeTableFragment
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable


class MainActivity : AppCompatActivity() {

    companion object {
        const val GLOBAL_PREFERENCES_KEY = "GLOBAL_PREFERENCES_KEY"
        private const val IDLING_KEY = "GLOBAL"
        val idlingResource: CountingIdlingResource = CountingIdlingResource(IDLING_KEY)
    }

    private lateinit var mainFragment: MainFragment
    private lateinit var searchStationFragment: SearchStationFragment
    private lateinit var stationTimeTableFragment: StationTimeTableFragment
    private lateinit var compositeDisposable: CompositeDisposable
    private val preferences
        get() = getSharedPreferences(
            GLOBAL_PREFERENCES_KEY,
            Context.MODE_PRIVATE
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainFragment = MainFragment()
        searchStationFragment = SearchStationFragment()
        stationTimeTableFragment = StationTimeTableFragment()
        compositeDisposable = CompositeDisposable()

        searchStationFragment.onItemClickListener = {
            storeSaveStation(preferences, SavedStation(it.id, it.name, setOf(), setOf()))
            onBackPressed()
        }

        mainFragment.onItemClickListener = {
            showFragment(stationTimeTableFragment)
            stationTimeTableFragment.setStation(listOf(it))
            supportActionBar!!.title = it.name
        }

        mainFragment.onDeleteListener = {
            preferences.edit { remove(it.id.toString()) }
            reloadMainFragment(preferences)
        }

        reloadMainFragment(preferences)
        showFragment(mainFragment, false)

        supportFragmentManager.addOnBackStackChangedListener {
            invalidateOptionsMenu()
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 23423
            )
        }

    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun storeSaveStation(
        preferences: SharedPreferences,
        savedStation: SavedStation
    ) {
        preferences.edit {
            putString(savedStation.id.toString(), Gson().toJson(savedStation))
        }
        reloadMainFragment(preferences)
    }

    private fun reloadMainFragment(preferences: SharedPreferences) {
        mainFragment.setSavedList(preferences.all.map {
            Gson().fromJson(it.value as String, SavedStation::class.java)
        })
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            supportActionBar!!.title = getString(applicationInfo.labelRes)
        } else super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        menu?.findItem(R.id.menu_config)?.isVisible = mainFragment.isVisible// && !isTimeTableShown
        menu?.findItem(R.id.menu_here)?.isVisible = mainFragment.isVisible

        menu?.findItem(R.id.menu_add)?.isVisible =
            stationTimeTableFragment.isVisible && stationTimeTableFragment.getStationList().size == 1
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_config -> {
                showFragment(searchStationFragment)
                true
            }
            R.id.menu_add -> {
                val stationList = stationTimeTableFragment.getStationList()
                if (stationList.size != 1)
                    throw Exception("cant be added")
                val savedStation = stationList.first().copy(
                    stopPointSet = stationTimeTableFragment.getStopFilter(),
                    busNameSet = stationTimeTableFragment.getBusFilter()
                )
                storeSaveStation(
                    preferences,
                    savedStation
                )
                onBackPressed()
                true
            }
            R.id.menu_here -> {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return true
                }
                idlingResource.increment()
                LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
                    if (it == null) {
                        Toast.makeText(applicationContext, "GPS not found", Toast.LENGTH_SHORT)
                            .show()
                        return@addOnSuccessListener
                    }
                    getNearBy(it.latitude.toString(), it.longitude.toString())
                        .map { it.map { it.toSavedStation() } }
                        .doFinally {
                            idlingResource.decrement()
                        }
                        .subscribe({
                            stationTimeTableFragment.setStation(it)
                            showFragment(stationTimeTableFragment)
                        }, {
                            Log.e("onCreate: ", it.toString(), it)
                        }).let {
                            compositeDisposable.add(it)
                        }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.main, fragment)
                if (addToBackStack)
                    addToBackStack(fragment.javaClass.simpleName)
                commit()
            }
    }
}
