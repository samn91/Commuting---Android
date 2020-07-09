package com.example.traficontime


import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.example.traficontime.MainActivity.Companion.GLOBAL_PREFERENCES_KEY
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

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

    @Test
    fun mainActivityTest() {
        val overflowMenuButton = onView(
            allOf(
                withContentDescription("More options"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.action_bar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        overflowMenuButton.perform(click())

        val appCompatTextView = onView(
            allOf(
                withId(R.id.title), withText("config"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatTextView.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.et_search),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.main),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )

        val station = "Malmö C"
        appCompatEditText.perform(replaceText(station), closeSoftKeyboard())

        Thread.sleep(1000)// TODO: 09/07/2020 change it to ideling counter

        onView(withId(R.id.rv_search)).perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        childAtPosition(withId(R.id.rv_saved), 0).matches(withText(station))
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
}
