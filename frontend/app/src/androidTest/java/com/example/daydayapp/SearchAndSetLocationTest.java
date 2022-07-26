package com.example.daydayapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.daydayapp.GoogleMapTestHelper.findMarkerAndClick;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SearchAndSetLocationTest {
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void searchLocation() throws InterruptedException {
        // Click on the search bar
        ViewInteraction clickSearchBar = onView(
                allOf(withId(R.id.places_autocomplete_search_input),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.autocomplete_fragment),
                                        0),
                                1),
                        isDisplayed()));
        clickSearchBar.perform(click());

        // Set search text as "MacMillan"
        ViewInteraction setSearchText = onView(
                allOf(withId(R.id.places_autocomplete_search_bar),
                        childAtPosition(
                                allOf(withId(R.id.places_autocomplete_search_bar_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        setSearchText.perform(replaceText("MacMillan"), closeSoftKeyboard());

        // Click on the first result
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.places_autocomplete_list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                2)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        findMarkerAndClick("MacMillan Building");

        // Click confirm to add study location
        onView(withText("CONFIRM")).perform(new MyAction("click"));

        // Click on the marker. If there's no dialog, it means it's already a study location
        findMarkerAndClick("2");
        onView(withId(R.id.tdlPanel)).check(matches(not(hasDescendant(withText("Set as Study Location")))));

        // Delete the newly added location
        onView(withId(R.id.delete_location_button)).perform(new MyAction("click"));
        onView(withText("CONFIRM")).perform(new MyAction("click"));
    }

    @Test
    public void clickOnMap() throws InterruptedException {
        Thread.sleep(5000);

        // Click on the (500, 500) pixel on the screen (randomly chosen point to click on map)
        onView(withId(R.id.tdlPanel)).perform(clickXY(500, 500));

        findMarkerAndClick("2");

        onView(withText("CONFIRM")).perform(new MyAction("click"));

        // Click on the marker. If there's no dialog, it means it's already a study location
        findMarkerAndClick("2");
        onView(withId(R.id.tdlPanel)).check(matches(not(hasDescendant(withText("Set as Study Location")))));

        onView(withId(R.id.delete_location_button)).perform(new MyAction("click"));
        onView(withText("CONFIRM")).perform(new MyAction("click"));
    }

    public static ViewAction clickXY(final int x, final int y){
        return new GeneralClickAction(
                Tap.SINGLE,
                view -> {
                    final int[] screenPos = new int[2];
                    view.getLocationOnScreen(screenPos);

                    final float screenX = screenPos[0] + x;
                    final float screenY = screenPos[1] + y;

                    return new float[]{screenX, screenY};
                },
                Press.FINGER);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}