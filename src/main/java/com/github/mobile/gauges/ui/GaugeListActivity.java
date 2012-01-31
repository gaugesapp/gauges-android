package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.github.mobile.gauges.R;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.Gauge;

import roboguice.activity.RoboFragmentActivity;

/**
 * Activity to display list of gauge summaries
 */
public class GaugeListActivity extends RoboFragmentActivity implements GaugeListFragment.GaugeListEventsCallback {
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.gauge_list);

        viewPager = (ViewPager) findViewById(R.id.vp_pages);
        viewPager.setAdapter(new GaugePagerAdapter(getApplicationContext(), null, getSupportFragmentManager()));
    }

    @Override
    public void onGaugeSelected(Gauge gauge) {
        if (viewPager == null) {
            Intent i = new Intent(this, GaugeViewActivity.class);
            i.putExtra(GAUGE, gauge);
            startActivity(i);
        } else {
            ((GaugePagerAdapter) viewPager.getAdapter()).setGauge(gauge);
        }
    }

}
