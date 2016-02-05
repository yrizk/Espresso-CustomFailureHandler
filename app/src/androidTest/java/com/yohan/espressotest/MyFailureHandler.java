package com.yohan.espressotest;

import android.content.Context;
import android.support.test.espresso.EspressoException;
import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.core.deps.guava.base.Preconditions;
import android.support.test.espresso.core.deps.guava.base.Throwables;
import android.view.View;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by rizk on 2/3/16.
 */
public class MyFailureHandler implements FailureHandler {

    private static final AtomicInteger failureCount = new AtomicInteger(0);
    private final Context appContext;

    public MyFailureHandler(
            Context appContext) {
        this.appContext = Preconditions.checkNotNull(appContext);
    }

    @Override
    public void handle(Throwable error, Matcher<View> viewMatcher) {
        if (error instanceof EspressoException || error instanceof AssertionFailedError
                || error instanceof AssertionError) {
            throw Throwables.propagate(getUserFriendlyError(error, viewMatcher));
        } else {
            throw Throwables.propagate(error);
        }
    }

    /**
     * When the error is coming from espresso, it is more user friendly to:
     * 1. propagate assertions as assertions
     * 2. swap the stack trace of the error to that of current thread (which will show
     * directly where the actual problem is)
     */
    private Throwable getUserFriendlyError(Throwable error, Matcher<View> viewMatcher) {
        if (error instanceof PerformException) {
            // Re-throw the exception with the viewMatcher (used to locate the view) as the view
            // description (makes the error more readable). The reason we do this here: not all creators
            // of PerformException have access to the viewMatcher.
            throw new PerformException.Builder()
                    .from((PerformException) error)
                    .withViewDescription(viewMatcher.toString())
                    .build();
        }

        if (error instanceof AssertionError) {
            // reports Failure instead of Error.
            // assertThat(...) throws an AssertionFailedError.
            error = new AssertionFailedWithCauseError(error.getMessage(), error);
        }

        error.setStackTrace(Thread.currentThread().getStackTrace());
        return error;
    }

    private static final class AssertionFailedWithCauseError extends AssertionFailedError {
        /* junit hides the cause constructor. */
        public AssertionFailedWithCauseError(String message, Throwable cause) {
            super(message);
            initCause(cause);
        }
    }

}
