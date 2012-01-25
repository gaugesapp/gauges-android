package com.github.mobile.gauges.ui;

import android.R;
import android.os.Bundle;

import com.github.mobile.gauges.R.layout;

import roboguice.activity.RoboFragmentActivity;

/**
 * Activity to display list of gauge summaries
 */
public class GaugeListActivity extends RoboFragmentActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.gauge_list);

		GaugeListFragment gauges = (GaugeListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.list);
		if (gauges == null) {
			gauges = new GaugeListFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.list, gauges).commit();
		}
	}

}
