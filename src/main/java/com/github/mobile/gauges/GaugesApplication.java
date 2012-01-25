package com.github.mobile.gauges;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

/**
 * 
 */
public class GaugesApplication extends Application {

	/**
	 * Create main application
	 */
	public GaugesApplication() {
	}

	/**
	 * Create main application
	 * 
	 * @param context
	 */
	public GaugesApplication(final Context context) {
		attachBaseContext(context);
	}

	/**
	 * Create main application
	 * 
	 * @param instrumentation
	 */
	public GaugesApplication(final Instrumentation instrumentation) {
		attachBaseContext(instrumentation.getTargetContext());
	}
}
