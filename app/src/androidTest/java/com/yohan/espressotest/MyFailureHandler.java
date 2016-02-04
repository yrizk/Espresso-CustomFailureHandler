package com.yohan.espressotest;

import android.content.Context;
import android.support.test.espresso.FailureHandler;
import android.view.View;

import org.hamcrest.Matcher;

/**
 * Created by rizk on 2/3/16.
 */
public class MyFailureHandler implements FailureHandler {

    private Context context;

    public MyFailureHandler(Context context) {
        this.context = context;
    }


    @Override
    public void handle(Throwable error, Matcher<View> viewMatcher) {



    }

}
