package com.example.daydayapp;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

public class GoogleMapTestHelper {

    public static void findMarkerAndClick(String markerTitle) throws InterruptedException {
        Thread.sleep(3000);
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker = mDevice.findObject(new UiSelector().descriptionContains(markerTitle));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }
}
