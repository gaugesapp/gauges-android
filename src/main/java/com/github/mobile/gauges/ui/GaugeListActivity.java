package com.github.mobile.gauges.ui;

import android.R;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.R.menu;

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
