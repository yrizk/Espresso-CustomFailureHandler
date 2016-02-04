package com.yohan.espressotest;

import android.app.Activity;
import android.support.test.espresso.FailureHandler;
import android.util.Log;
import android.view.View;

import org.hamcrest.Matcher;

/**
 * Created by rizk on 2/3/16.
 */
public class MyFailureHandler implements FailureHandler {


    private static final String TAG = MyFailureHandler.class.getSimpleName();

    private ScreenShotManager ssManager;

    private Activity mActivity;
    public MyFailureHandler(Activity activity) {
        mActivity = activity;
        ssManager = new ScreenShotManager(activity);
    }


    @Override
    public void handle(Throwable error, Matcher<View> viewMatcher) {
        ssManager.snap(mActivity.getWindow().getDecorView());
        Log.e(TAG, "Error matching the following matcher: " + viewMatcher, error);
        try {
            throw error;
        }
        catch (Throwable t) {
            Log.e(TAG, "Error matching the following matcher: " + viewMatcher, error);
        }
    }

}
