package com.example.daydayapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.daydayapp.GoogleMapTestHelper.findMarkerAndClick;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UpdateScoreTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void sampleTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.daydayapp", appContext.getPackageName());
    }

    @Test
    public void updateScore() throws InterruptedException {
        // get current score
        onView(withId(R.id.leaderboard)).perform(click());
        Thread.sleep(3000);
        MyAction action = new MyAction("read");
        onView(withId(R.id.text_view_progress)).perform(action);
        int original_score = action.getText();

        // add a new task at location 1
        onView(withId(R.id.tdl)).perform(click());
        Thread.sleep(3000);
        findMarkerAndClick("1");
        onView(withId(R.id.add_task_button)).perform(new MyAction("click"));
        onView(withId(R.id.newTaskPopup_title)).perform(new MyAction("fill", "test task 0"));
        onView(withId(R.id.newTaskPopup_select_date_button)).perform(new MyAction("fill", "AUG 15 2022"));
        onView(withId(R.id.newTaskPopup_select_duration_button)).perform(new MyAction("fill", "45"));
        onView(withId(R.id.newTaskPopup_saveButton)).perform(new MyAction("click"));
        Thread.sleep(1000);

        // start study (set status as STUDYING)
        onView(withId(R.id.start_study_button)).perform(new MyAction("click"));
        Thread.sleep(1000);

        // set added task as DONE by checking the box
        onView(withId(R.id.todoCheckBox)).perform(new MyAction("click"));
        Thread.sleep(1000);

        // end study
        onView(withId(R.id.start_study_button)).perform(new MyAction("click"));

        // check score
        onView(withId(R.id.leaderboard)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.text_view_progress)).perform(action);
        int current_score = action.getText();
        assertEquals(current_score, original_score + 3);
    }
}
