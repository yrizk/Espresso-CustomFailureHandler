package com.yohan.espressotest;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenShooter {

    private static final String TAG = ScreenShooter.class.getSimpleName();
    private Context context;

    public ScreenShooter(Context context) {
        this.context = context;
    }

    /**
     * Creates a PNG image on disk with the specified view
     * @param view the view
     * @return a {@link Uri} to the location of the image on disk
     */
    public Uri shoot(View view) {
        if (!view.isDrawingCacheEnabled()) {
            view.setDrawingCacheEnabled(true);
        }
        Bitmap bitmap = view.getDrawingCache();
        Uri location = convertBitmapToPng(bitmap);
        return location;
    }

    private Uri convertBitmapToPng(Bitmap bitmap) {
        File screenshot = new File(getCacheDir(), "test-failure-screenshot.png");
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

    private String getCacheDir() {
        String cacheDir;
        if (Build.VERSION.SDK_INT >= 23) {
            cacheDir = context.getFilesDir().getAbsolutePath();
        }
        else {
            cacheDir = context.getExternalCacheDir().getAbsolutePath();;
        }
        Log.d(TAG, "getCacheDir() : " + cacheDir);
        new File(cacheDir).mkdirs();
        return cacheDir;
    }

}
