package com.example.daydayapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.daydayapp.GoogleMapTestHelper.findMarkerAndClick;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StartStudyClickTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickStartStudyInTdlFragment() throws InterruptedException {
        int count = 0;
        // Click study location 2 on the map
        findMarkerAndClick("2");
        count++;
        Thread.sleep(500);

        // Click start study button
        onView(withId(R.id.start_study_button)).check(matches(isDisplayed()));
        onView(withId(R.id.start_study_button)).perform(new MyAction("click"));
        count++;
        Thread.sleep(500);

        // Check if started
        onView(withText("End")).check(matches(isDisplayed()));

        assertTrue(count <= 3);
        Thread.sleep(1000);

        // End study
        onView(withId(R.id.start_study_button)).perform(new MyAction("click"));
        Thread.sleep(1000);
    }

    @Test
    public void clickStartStudyInFriendsFragment() throws InterruptedException {
        // Go to friends fragment
        onView(withId(R.id.friends)).perform(click());

        int count = 0;

        // Go to tdl fragment
        onView(withId(R.id.tdl)).perform(click());
        count++;

        // Click study location 2 on the map
        findMarkerAndClick("2");
        count++;
        Thread.sleep(500);

        // Click start study button
        onView(withId(R.id.start_study_button)).check(matches(isDisplayed()));
        onView(withId(R.id.start_study_button)).perform(new MyAction("click"));
        count++;
        Thread.sleep(500);

        // Check if started
        onView(withText("End")).check(matches(isDisplayed()));

        assertTrue(count <= 3);
        Thread.sleep(1000);

        // End study
        onView(withId(R.id.start_study_button)).perform(new MyAction("click"));
        Thread.sleep(1000);
    }

    @Test
    public void clickStartStudyInLBFragment() throws InterruptedException {
        // Go to friends fragment
        onView(withId(R.id.leaderboard)).perform(click());

        int count = 0;

        // Go to tdl fragment
        onView(withId(R.id.tdl)).perform(click());
        count++;

        // Click study location 2 on the map
        findMarkerAndClick("2");
        count++;
        Thread.sleep(500);

        // Click start study button
        onView(withId(R.id.start_study_button)).check(matches(isDisplayed()));
        onView(withId(R.id.start_study_button)).perform(new MyAction("click"));
        count++;
        Thread.sleep(500);

        // Check if started
        onView(withText("End")).check(matches(isDisplayed()));

        assertTrue(count <= 3);
        Thread.sleep(1000);

        // End study
        onView(withId(R.id.start_study_button)).perform(new MyAction("click"));
        Thread.sleep(1000);
    }

    @Test
    public void clickStartStudyInProfileFragment() throws InterruptedException {
        // Go to friends fragment
        onView(withId(R.id.profile)).perform(click());

        int count = 0;

        // Go to tdl fragment
        onView(withId(R.id.tdl)).perform(click());
        count++;

        // Click study location 2 on the map
        findMarkerAndClick("2");
        count++;
        Thread.sleep(500);

        // Click start study button
        onView(withId(R.id.start_study_button)).check(matches(isDisplayed()));
        onView(withId(R.id.start_study_button)).perform(new MyAction("click"));
        count++;
        Thread.sleep(500);

        // Check if started
        onView(withText("End")).check(matches(isDisplayed()));

        assertTrue(count <= 3);
        Thread.sleep(1000);

        // End study
        onView(withId(R.id.start_study_button)).perform(new MyAction("click"));
        Thread.sleep(1000);
    }
}
