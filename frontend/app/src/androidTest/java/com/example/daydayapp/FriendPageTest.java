package com.example.daydayapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FriendPageTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void sampleTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.daydayapp", appContext.getPackageName());
    }

    @Test
    public void addFriendAlreadyAdded() throws InterruptedException {
        // Click friend button in the bottom navigation bar
        onView(withId(R.id.friends)).perform(click());

        // Check if add friend button exist and click it
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()));
        onView(withId(R.id.add_friend_button)).perform(new MyAction("click"));

        // Check if new friend textView is displayed and the text is correct
        onView(withId(R.id.newFriendTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.newFriendTextView)).check(matches(withText("Add new friend")));

        // Check if searchView is displayed, type in friend email, and search
        onView(withId(R.id.newFriendSearch)).check(matches(isDisplayed()));
        onView(withId(R.id.newFriendSearch)).perform(new MyAction("search", "peizx28@gmail.com"));

        // Wait for 3 seconds and check the search result
        Thread.sleep(3000);
        onView(withId(R.id.searchFriendResult)).check(matches(withText("Victor Pei")));

        // Check if save button exists and click save button with an added friend
        onView(withId(R.id.newFriendPopup_saveButton)).check(matches(isDisplayed()));
        onView(withId(R.id.newFriendPopup_saveButton)).perform(new MyAction("click"));

        // Check if the toast message is displayed correctly
        onView(withText("Check if the friend is already added.")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void addFriendWithoutSearch() {
        // Click friend button in the bottom navigation bar
        onView(withId(R.id.friends)).perform(click());

        // Check if add friend button exist and click it
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()));
        onView(withId(R.id.add_friend_button)).perform(new MyAction("click"));

        // Check if new friend textView is displayed and the text is correct
        onView(withId(R.id.newFriendTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.newFriendTextView)).check(matches(withText("Add new friend")));

        // Check if save button exists and click save button without filling in email
        onView(withId(R.id.newFriendPopup_saveButton)).check(matches(isDisplayed()));
        onView(withId(R.id.newFriendPopup_saveButton)).perform(new MyAction("click"));

        // Check if the toast message is displayed correctly
        onView(withText("Please fill in email")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
}