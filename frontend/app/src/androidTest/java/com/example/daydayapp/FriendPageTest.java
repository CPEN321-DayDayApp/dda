package com.example.daydayapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FriendPageTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void checkFriendsFragmentAndClickAddButton() throws InterruptedException {
        Thread.sleep(1000);

        // Click friend button in the bottom navigation bar
        onView(withId(R.id.friends)).perform(click());

        // Check if friends fragment is loaded and friend list recycler view exists
        onView(withId(R.id.friends_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.friendListRecyclerView)).check(matches(isDisplayed()));

        // Check if Victor Pei is my friend
        onView(withId(R.id.friendListRecyclerView)).check(matches(hasDescendant(withText("Victor Pei"))));

        // Check if add friend button exist and click it
        onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()));
        onView(withId(R.id.add_friend_button)).perform(new MyAction("click"));

        // Check if new friend textView is displayed and the text is correct
        onView(withId(R.id.newFriendTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.newFriendTextView)).check(matches(withText("Add new friend")));

        Thread.sleep(500);
    }

    @Test
    public void newFriendPopUpWindowCancel() {
        // Check if cancel button exists and click cancel
        onView(withId(R.id.newFriendPopup_cancelButton)).check(matches(isDisplayed()));
        onView(withId(R.id.newFriendPopup_cancelButton)).perform(new MyAction("click"));

        // Check if the PopUp window is gone
        onView(withId(R.id.newFriendTextView)).noActivity();
    }

    @Test
    public void addFriendWithoutSearch() {
        // Check if save button exists and click save button without filling in email
        onView(withId(R.id.newFriendPopup_saveButton)).check(matches(isDisplayed()));
        onView(withId(R.id.newFriendPopup_saveButton)).perform(new MyAction("click"));

        // Check if the toast message is displayed correctly
        onView(withText("Please fill in email")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void addFriendAlreadyAdded() throws InterruptedException {
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
    public void addNonExistingFriend() throws InterruptedException {
        // Check if searchView is displayed, type in friend email, and search
        onView(withId(R.id.newFriendSearch)).check(matches(isDisplayed()));
        onView(withId(R.id.newFriendSearch)).perform(new MyAction("search", "123@gmail.com"));

        // Wait for 10 seconds and check the search result
        Thread.sleep(10000);
        onView(withId(R.id.searchFriendResult)).check(matches(withText("Not found")));

        // Check if save button exists and click save button with an added friend
        onView(withId(R.id.newFriendPopup_saveButton)).check(matches(isDisplayed()));
        onView(withId(R.id.newFriendPopup_saveButton)).perform(new MyAction("click"));

        // Check if the toast message is displayed correctly
        onView(withText("User not found. Please try again")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void addValidFriend() throws InterruptedException {
        // Check if searchView is displayed, type in friend email, and search
        onView(withId(R.id.newFriendSearch)).check(matches(isDisplayed()));
        onView(withId(R.id.newFriendSearch)).perform(new MyAction("search", "maskcpen291@gmail.com"));

        // Wait for 3 seconds and check the search result
        Thread.sleep(3000);
        onView(withId(R.id.searchFriendResult)).check(matches(withText("Cpen 291")));

        // Check if save button exists and click save button with an added friend
        onView(withId(R.id.newFriendPopup_saveButton)).check(matches(isDisplayed()));
        onView(withId(R.id.newFriendPopup_saveButton)).perform(new MyAction("click"));

        // Check if the friend is added
        Thread.sleep(1000);
        onView(withId(R.id.friendListRecyclerView)).check(matches(hasDescendant(withText("Cpen 291"))));

        // Check if Victor Pei is still my friend
        onView(withId(R.id.friendListRecyclerView)).check(matches(hasDescendant(withText("Victor Pei"))));

        // Swipe left on the newly added friend in test and delete it
        onView(withId(R.id.friendListRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, swipeLeft()));
        onView(withText("CONFIRM")).perform(new MyAction("click"));
    }
}