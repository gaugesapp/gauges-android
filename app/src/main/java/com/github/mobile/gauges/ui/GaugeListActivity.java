package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import static com.github.mobile.gauges.IntentConstants.VIEW_GAUGE;
import android.content.Intent;
import android.os.Bundle;

import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.Gauge;

import roboguice.activity.RoboFragmentActivity;

/**
 * Activity to display list of gauge summaries
 */
public class GaugeListActivity extends RoboFragmentActivity implements OnGaugeSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.gauge_list);
    }

    @Override
    public void onGaugeSelected(Gauge gauge) {
        Intent i = new Intent(VIEW_GAUGE);
        i.putExtra(GAUGE, gauge);
        startActivity(i);
    }
}
