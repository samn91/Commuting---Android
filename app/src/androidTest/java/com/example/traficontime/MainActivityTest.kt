package com.example.traficontime


import android.Manifest
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.AmbiguousViewMatcherException
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import com.example.traficontime.MainActivity.Companion.GLOBAL_PREFERENCES_KEY
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    companion object {
        private const val OSTERVARN_STATION = "Malmö Östervärn"
        private const val MALMO_C_STATION = "Malmö C"
        private const val BUS_4_A = "4 Segevång"
        private const val BUS_4_B = "4 Bunkeflostrand direkt Centralen"
        private const val BUS_130_A = "130 Lund via Åkarp och Hjärup"
    }

    @Rule
    @JvmField
    var permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Rule
    @JvmField
    var mActivityTestRule = object : ActivityTestRule<MainActivity>(MainActivity::class.java) {

        override fun beforeActivityLaunched() {
            InstrumentationRegistry.getInstrumentation().targetContext.getSharedPreferences(
                GLOBAL_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            ).edit().clear().commit()
            super.beforeActivityLaunched()
        }
    }

    @Before
    fun setup(): Unit {
        IdlingRegistry.getInstance().register(MainActivity.idlingResource)
    }

    @Test
    fun addStationTest() {
        val station = OSTERVARN_STATION
        addStation(station)
        childAtPosition(withId(R.id.rv_saved), 0).matches(withText(station))
    }

    @Test
    fun timeTableTest() {
        addStation(OSTERVARN_STATION)
        onView(withText(OSTERVARN_STATION)).perform(click())
        checkIfBusShown(BUS_4_B)
    }

    @Test
    fun nearByStationTest() {
        mockLocation()
        onView(withContentDescription("More options")).perform(click())
        onView(allOf(withId(R.id.title), withText("here"))).perform(click())
        onView(withId(R.id.rv_filter_station)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_filter_bus)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_filter_stop)).check(matches(isDisplayed()))
        onView(withText("4")).perform(click())
        onView(withText("5")).perform(click())

        checkIfBusShown("$MALMO_C_STATION: $BUS_4_A direkt Värnhem")
    }

    private fun mockLocation() {
        val lm =
            InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager?
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val mocLocationProvider = lm!!.getBestProvider(criteria, true)
            ?: throw Exception("Couldn't get location provider")
        lm.addTestProvider(
            mocLocationProvider, false, false,
            false, false, true, true, true, 1, 2
        )
        lm.setTestProviderEnabled(mocLocationProvider, true)
        val loc = Location(mocLocationProvider)
        val mockLocation = Location(mocLocationProvider)

        mockLocation.latitude = 55.609297
        mockLocation.longitude = 13.000251
        mockLocation.altitude = loc.altitude
        mockLocation.time = System.currentTimeMillis()
        mockLocation.elapsedRealtimeNanos = System.nanoTime();
        mockLocation.accuracy = 0.0f
        lm.setTestProviderLocation(mocLocationProvider, mockLocation)
    }

    @Test
    fun timeTableFilterSelectionTest() {
        addStation(MALMO_C_STATION)
        onView(withText(MALMO_C_STATION)).perform(click())
        onView(allOf(withText("A"), withParent(withId(R.id.rv_filter_stop))))
            .check(matches(withBoldText()))//init selection all selected
            .perform(click())
            .check(matches(withBoldText()))//first selection only A selected
            .perform(click())
            .check(matches(not(withBoldText())))//resecting A becomes unselected

        onView(allOf(withText("D"), withParent(withId(R.id.rv_filter_stop))))
            .check(matches(not(withBoldText())))
            .perform(click())
            .check(matches(withBoldText()))
    }


    @Test
    fun timeTableStopFilterTest() {
        val station = OSTERVARN_STATION
        addStation(station)
        onView(withText(station)).perform(click())
        onView(allOf(withText("A"), withParent(withId(R.id.rv_filter_stop)))).perform(click())
        checkIfBusShown(BUS_130_A)
        checkIfBusShown(BUS_4_A)
        onView(withText(BUS_4_B)).check(doesNotExist())
    }

    @Test
    fun timeTableBusFilterTest() {
        val station = OSTERVARN_STATION
        addStation(station)
        onView(withText(station)).perform(click())
        onView(allOf(withText("4"), withParent(withId(R.id.rv_filter_bus)))).perform(click())
        checkIfBusShown(BUS_4_A)
        checkIfBusShown(BUS_4_B)
        onView(withText(BUS_130_A)).check(doesNotExist())
    }

    @Test
    fun timeTableUseExactFilterTest() {
        val station = OSTERVARN_STATION
        addStation(station)
        onView(withText(station)).perform(click())
        onView(allOf(withText("A"), withParent(withId(R.id.rv_filter_stop)))).perform(click())
        onView(allOf(withText("4"), withParent(withId(R.id.rv_filter_bus)))).perform(click())

        onView(withContentDescription("More options")).perform(click())
        onView(allOf(withId(R.id.title), withText("add"))).perform(click())

        onView(withText("$station|A|4")).perform(click())
        checkIfBusShown(BUS_4_A)
        onView(withText(BUS_4_B)).check(doesNotExist())
        onView(withText(BUS_130_A)).check(doesNotExist())
    }

    @Test
    fun deleteStationTest() {
        addStation(OSTERVARN_STATION)
        onView(withText(OSTERVARN_STATION)).check(matches(isDisplayed()))
        onView(withText(MALMO_C_STATION)).check(doesNotExist())

        onView(withId(R.id.rv_saved)).perform(
            actionOnItem<ViewHolder>(
                withText(OSTERVARN_STATION),
                swipeLeft()
            )
        )

        addStation(MALMO_C_STATION)
        onView(withText(MALMO_C_STATION)).check(matches(isDisplayed()))
        onView(withText(OSTERVARN_STATION)).check(doesNotExist())

        onView(withId(R.id.rv_saved)).perform(
            actionOnItem<ViewHolder>(
                withText(MALMO_C_STATION),
                swipeLeft()
            )
        )

        onView(withText(MALMO_C_STATION)).check(doesNotExist())
    }

    private fun checkIfBusShown(bus: String) {
        try {
            onView(withText(bus)).check(matches(isDisplayed()))
        } catch (e: AmbiguousViewMatcherException) {

        }
    }

    private fun addStation(station: String) {
        onView(withContentDescription("More options")).perform(click())
        onView(allOf(withId(R.id.title), withText("config"))).perform(click())
        onView(withId(R.id.et_search)).perform(replaceText(station), closeSoftKeyboard())
        onView(withId(R.id.rv_search)).perform(actionOnItemAtPosition<ViewHolder>(0, click()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    private fun withBoldText(): Matcher<View?>? {
        return object : BoundedMatcher<View?, TextView>(TextView::class.java) {
            override fun matchesSafely(textView: TextView): Boolean {
                return textView.typeface.isBold
            }

            override fun describeTo(description: Description) {
                description.appendText("Textview bold")
            }
        }
    }

}
