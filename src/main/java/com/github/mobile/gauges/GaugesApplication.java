package com.github.mobile.gauges;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.FROYO;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

import com.github.kevinsawicki.http.HttpRequest;

/**
 * Gauges application
 */
public class GaugesApplication extends Application {

    /**
     * Create main application
     */
    public GaugesApplication() {
        // Disable http.keepAlive on Froyo and below
        if (SDK_INT <= FROYO)
            HttpRequest.keepAlive(false);
    }

    /**
     * Create main application
     *
     * @param context
     */
    public GaugesApplication(final Context context) {
        this();
        attachBaseContext(context);
    }

    /**
     * Create main application
     *
     * @param instrumentation
     */
    public GaugesApplication(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }
}
