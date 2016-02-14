package com.yohan.espressotest;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;

public class MainActivityInstrumentationTest
        extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity mActivity;

    public MainActivityInstrumentationTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
        Espresso.setFailureHandler(new ScreenShotFailureHandler(mActivity));
    }

    public void testSimple() {
        Espresso.onView(ViewMatchers.withId(R.id.main_button)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Main Button was clicked!")).check(ViewAssertions.doesNotExist()); // this will fail.
    }

}