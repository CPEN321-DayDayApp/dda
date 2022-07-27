package com.example.daydayapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;

import androidx.appcompat.widget.SearchView;

import org.hamcrest.Matcher;

public class MyAction implements ViewAction {
    private final String action;
    private String query;
    private CharSequence text;

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
            case "fill":
                ((TextView) view).setText(query);
                break;
            case "read":
                TextView tv = (TextView)view;
                text = tv.getText();
                break;
            default:
        }
    }

    public int getText() {
        return Integer.parseInt(String.valueOf(text));
    }
}
