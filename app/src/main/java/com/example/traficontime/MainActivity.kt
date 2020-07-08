package com.example.traficontime

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {

    private lateinit var mainFragment: MainFragment
    private lateinit var searchStationFragment: SearchStationFragment
    private lateinit var stationTimeTableFragment: StationTimeTableFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainFragment = MainFragment()
        searchStationFragment = SearchStationFragment()
        stationTimeTableFragment = StationTimeTableFragment()

        val preferences = getPreferences(Context.MODE_PRIVATE)

        searchStationFragment.onItemClickListener = {
            preferences.edit {
                putString(it.id.toString(), Gson().toJson(it))
            }
        }

        mainFragment.onItemClickListener = {
            showFragment(stationTimeTableFragment, true)
            stationTimeTableFragment.setStationId(it.id.toString())
        }

        mainFragment.setSavedList(preferences.all.map {
            Gson().fromJson(it.value as String, Station::class.java)
        })

        showFragment(mainFragment)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.emnu_config -> {
                showFragment(searchStationFragment, true)
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

