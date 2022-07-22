package com.example.daydayapp;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;

import androidx.appcompat.widget.SearchView;

import org.hamcrest.Matcher;

public class MyAction implements ViewAction {
    private final String action;
    private String query;

    public MyAction(String action) {
        this.action = action;
    }

    public MyAction(String action, String query) {
        this.action = action;
        this.query = query;
    }

    @Override
    public Matcher<View> getConstraints() {
        return ViewMatchers.isEnabled(); // no constraints, they are checked above
    }

    @Override
    public String getDescription() {
        return "click plus button";
    }

    @Override
    public void perform(UiController uiController, View view) {
        switch (action) {
            case "click":
                view.performClick();
                break;
            case "search":
                ((SearchView) view).setQuery(query, true);
                break;
            default:
        }
    }
}
