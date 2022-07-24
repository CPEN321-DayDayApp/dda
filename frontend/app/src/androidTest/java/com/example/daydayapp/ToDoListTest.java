package com.example.daydayapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import java.util.Calendar;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ToDoListTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void sampleTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.daydayapp", appContext.getPackageName());
    }

    @Before
    public void clickAddTaskButton() throws InterruptedException {
        findMarkerAndClick("1");
        // Check if add task button exists and click it
        onView(withId(R.id.add_task_button)).check(matches(isDisplayed()));
        onView(withId(R.id.add_task_button)).perform(new MyAction("click"));

        // Check if new task textView is displayed and the text is correct
        onView(withId(R.id.addTaskTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.addTaskTextView)).check(matches(withText("New Task")));

        // Check if new task title is in the popup window and is empty
        onView(withId(R.id.newTaskPopup_title)).check(matches(isDisplayed()));
        onView(withId(R.id.newTaskPopup_title)).check(matches(withText("")));

        //check if select date is in the popup window and is pre-selected current date
        onView(withId(R.id.newTaskPopup_select_date_button)).check(matches(isDisplayed()));
        Calendar cal = Calendar.getInstance();
        onView(withId(R.id.newTaskPopup_select_date_button)).check(matches(withText(makeDateString(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)))));

        //check if set task duration is in the popup window and is pre-selected 15min
        onView(withId(R.id.newTaskPopup_select_duration_button)).check(matches(isDisplayed()));
        onView(withId(R.id.newTaskPopup_select_duration_button)).check(matches(withText("15")));

        //check if "CANCEL" and "SAVE" button is displayed
        onView(withId(R.id.newTaskPopup_cancelButton)).check(matches(isDisplayed()));
        onView(withId(R.id.newTaskPopup_saveButton)).check(matches(isDisplayed()));
    }

    @Test
    public void clickCancelNewTaskButton() {
        onView(withId(R.id.newTaskPopup_cancelButton)).perform(new MyAction("click"));
        //check if dialog dismissed
        onView(withId(R.id.addTaskTextView)).check(doesNotExist());
    }

    @Test
    public void saveTaskWithoutTitle() {
        //click "SAVE" button directly without filling task name
        onView(withId(R.id.newTaskPopup_saveButton)).perform(new MyAction("click"));
        // Check if the toast message is displayed correctly
        onView(withText("Please fill all the fields")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void saveValidTask() throws InterruptedException {
        // fill in the task description
        onView(withId(R.id.newTaskPopup_title)).perform(new MyAction("fill", "test task 0"));
        onView(withId(R.id.newTaskPopup_select_date_button)).perform(new MyAction("fill", "AUG 15 2022"));
        onView(withId(R.id.newTaskPopup_select_duration_button)).perform(new MyAction("fill", "45"));
        onView(withId(R.id.newTaskPopup_saveButton)).perform(new MyAction("click"));
        //check if task added
        Thread.sleep(1000);
        onView(withId(R.id.listRecyclerView)).check(matches(hasDescendant(withText("test task 0"))));
    }

    @Test
    public void editTask() throws InterruptedException {
        saveValidTask();
        Thread.sleep(1000);
        // Swipe right on the newly added task in test
        onView(withId(R.id.listRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));
        Thread.sleep(5000);
        // Check if edit task popup is displayed and shows correct text
        onView(withId(R.id.addTaskTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.addTaskTextView)).check(matches(withText("New Task")));
        onView(withId(R.id.newTaskPopup_title)).check(matches(withText("test task 0")));
        onView(withId(R.id.newTaskPopup_select_date_button)).check(matches(withText("AUG 15 2022")));
        onView(withId(R.id.newTaskPopup_select_duration_button)).check(matches(withText("45")));
        //edit task
        onView(withId(R.id.newTaskPopup_title)).perform(new MyAction("fill", "edit task 0"));
        onView(withId(R.id.newTaskPopup_select_date_button)).perform(new MyAction("fill", "DEC 23 2023"));
        onView(withId(R.id.newTaskPopup_select_duration_button)).perform(new MyAction("fill", "30"));
        onView(withId(R.id.newTaskPopup_saveButton)).perform(new MyAction("click"));
        //check if task edited
        Thread.sleep(1000);
        onView(withId(R.id.listRecyclerView)).check(matches(hasDescendant(withText("edit task 0"))));
    }

    @Test
    public void deleteTask() throws InterruptedException {
        editTask();
        Thread.sleep(1000);
        // Swipe left on the newly added task in test
        onView(withId(R.id.listRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeLeft()));
        Thread.sleep(5000);
        //check if popup dialog shows
        onView(withText("Delete Task")).check(matches(isDisplayed()));
        //click cancel button and check if dialog disappears
        onView(withText("Cancel")).perform(new MyAction("click"));
        onView(withText("Delete Task")).check(doesNotExist());
        //swipe again and click confirm button to delete task
        onView(withId(R.id.listRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeLeft()));
        onView(withText("Confirm")).perform(new MyAction("click"));
        //check if both dialog and task disappear
        Thread.sleep(1000);
        onView(withText("Delete Task")).check(doesNotExist());
        onView(withId(R.id.listRecyclerView)).check(matches(not(hasDescendant(withText("edit task 0")))));
    }

    private String makeDateString(int day, int month, int year)
    {
        if (month < 0 || month > 11)
            return "JAN";

        final String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

        return months[month] + " " + day + " " + year;
    }

    private void findMarkerAndClick(String position) throws InterruptedException {
        Thread.sleep(3000);
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker = mDevice.findObject(new UiSelector().descriptionContains(position));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
    }
}
