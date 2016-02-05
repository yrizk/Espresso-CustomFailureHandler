package com.yohan.espressotest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ScreenShotManager {

    private static final String TAG = ScreenShotManager.class.getSimpleName();

    private CountDownLatch mLatch;
    private Bitmap firstBitmap;
    private Context context;


    public ScreenShotManager(Context context) {
        this.context = context;
    }

    public void execute(View view) {
        new BackgroundJob().execute(view);
    }

    public class BackgroundJob extends  AsyncTask<View,Void,Void> {

        @Override
        protected Void doInBackground(View... params) {
            tryThis(params[0]);
            return null;
        }
    }

    public void tryThis(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();
        convertBitmapToPng(bitmap);
    }



    /**
     * Creates a PNG image on disk with the specified view and logo on bottom
     * @param view the view to convert to PNG image
     * @return a {@link Uri} to the location of the image on disk
     */
    public Uri snap(final View view) {
        Context context = view.getContext();
        getViewAndBitmap(context, view);
        waitForUiThread();
        Uri uri = convertBitmapToPng(firstBitmap);
        return uri;
    }

    private void waitForUiThread() {
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "error trying to wait for ui thread to complete",e);
        }
    }

    // this is an ui thread operation only.
    private void getViewAndBitmap(Context context,final View view) {
        mLatch = new CountDownLatch(1);
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    firstBitmap = convertViewToBitmap(view);
                    mLatch.countDown();
                }
            });
        }
    }

    /**
     * Creates a PNG image on disk with the specified view
     *
     * @param bitmap the bitmap to convert to PNG image
     * @return a {@link Uri} to the location of the image on disk
     */
    private Uri convertBitmapToPng(Bitmap bitmap) {
        File screenshot = new File(getCacheDir(), "screenshot-" + System.currentTimeMillis() / 1000 + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(screenshot);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);
            fos.flush();
            fos.close();
            if (Build.VERSION.SDK_INT >= 23) {
                return FileProvider.getUriForFile(context, "com.buzzfeed.messenger.QuizChatFileProvider", screenshot);
            }
            else {
                Log.d(TAG, "location of screenshot: " + screenshot.getAbsolutePath());
                return Uri.fromFile(screenshot);

            }
        } catch (IOException e) {
            Log.d(TAG, "Unable to write bitmap data to file");
            return null;
        }
    }

    /**
     * Creates a @link{Bitmap} with the specified view
     *
     * @param v the view to convert to PNG image
     * @return a {@link Uri} to the location of the image on disk
     */
    private  Bitmap convertViewToBitmap(View v) {
        Bitmap bitmap;
        if (v.getWidth() <= 0 || v.getHeight() <= 0) {
            //View was not laid out
            v.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            v.measure(960, 960);
            Log.d(TAG, "m width / height: " + v.getMeasuredWidth() + " " + v.getMeasuredHeight() + " normal width/height" + v.getWidth() + " " + v.getHeight() ) ;
            bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        } else {
            Log.d(TAG, "width / height: " + v.getWidth() + " " + v.getHeight()) ;
            bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        }
        v.draw(new Canvas(bitmap));
        return bitmap;
    }


    private String getCacheDir() {
        String cacheDir;
        if (Build.VERSION.SDK_INT >= 23) {
            cacheDir = context.getFilesDir().getAbsolutePath();
        }
        else {
            cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath();;
        }
        Log.d(TAG, "getCacheDir() : " + cacheDir);
        new File(cacheDir).mkdirs();
        return cacheDir;
    }

}