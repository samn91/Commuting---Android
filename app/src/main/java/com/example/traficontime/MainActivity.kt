package com.example.traficontime

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.traficontime.timetable.StationTimeTableFragment
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {

    companion object {
        const val GLOBAL_PREFERENCES_KEY = "GLOBAL_PREFERENCES_KEY"
        private const val IDLING_KEY = "GLOBAL"
        val idlingResource: CountingIdlingResource = CountingIdlingResource(IDLING_KEY)
    }


    private lateinit var mainFragment: MainFragment
    private lateinit var searchStationFragment: SearchStationFragment
    private lateinit var stationTimeTableFragment: StationTimeTableFragment
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


        searchStationFragment.onItemClickListener = {
            storeSaveStation(preferences, SavedStation(it.id, it.name, setOf(), setOf()))
            onBackPressed()
        }

        mainFragment.onItemClickListener = {
            showFragment(stationTimeTableFragment, true)
            stationTimeTableFragment.setStation(it)
        }

        mainFragment.onDeleteListener = {
            preferences.edit { remove(it.id.toString()) }
            reloadMainFragment(preferences)
        }

        reloadMainFragment(preferences)
        showFragment(mainFragment)

        supportFragmentManager.addOnBackStackChangedListener {
            invalidateOptionsMenu()
        }
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
        } else super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        menu?.findItem(R.id.menu_config)?.isVisible = mainFragment.isVisible// && !isTimeTableShown
        menu?.findItem(R.id.menu_add)?.isVisible = stationTimeTableFragment.isVisible
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_config -> {
                showFragment(searchStationFragment, true)
                true
            }
            R.id.menu_add -> {
                val station = stationTimeTableFragment.getStation()
                storeSaveStation(
                    preferences,
                    station
                )
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.main, fragment)
                if (addToBackStack)
                    addToBackStack(fragment.javaClass.simpleName)
                commit()
            }
    }
}

