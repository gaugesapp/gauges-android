package com.github.mobile.gauges.ui;

import android.os.Bundle;

import com.github.mobile.gauges.R.layout;

import roboguice.activity.RoboFragmentActivity;

/**
 * Activity to display list of gauge summaries
 */
public class GaugeListActivity extends RoboFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.gauge_list);
    }
}
