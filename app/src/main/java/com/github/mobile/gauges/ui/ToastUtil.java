package com.github.mobile.gauges.ui;

import static android.widget.Toast.LENGTH_LONG;
import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

public class ToastUtil {

    private final static String TAG = "ToastUtil";

    /**
     * Shows a toast to the user - can be called from any thread, toast will be displayed using the UI-thread.
     * <p>
     * The important thing about the delayed aspect of the UI-thread code used by this
     * method is that it may actually run <em>after</em> the associated activity
     * has been destroyed - so it can not keep a reference to the activity. Calling methods
     * on a destroyed activity may throw exceptions, and keeping a reference to it is technically a
     * short-term memory-leak: http://developer.android.com/resources/articles/avoiding-memory-leaks.html
     *
     */
    public static void toastOnUiThread(Activity activity, final String message) {
        Log.d(TAG, "Will display toast : "+ message);
        final Application application = activity.getApplication();
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(application, message, LENGTH_LONG).show();
            }
        });
    }
    
}
