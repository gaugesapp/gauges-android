package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;

import com.github.mobile.gauges.core.Gauge;
import com.viewpagerindicator.TitlePageIndicator;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

/**
 * Activity to view a specific {@link Gauge}'s traffic, content, and referrer information
 */
public class GaugeViewActivity extends RoboFragmentActivity {

    private @InjectView(id.tpi_header) TitlePageIndicator indicator;
    private @InjectView(id.vp_pages) ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.gauge_view);

        Gauge gauge = (Gauge) getIntent().getSerializableExtra(GAUGE);
        setTitle(gauge.getTitle());

        pager.setAdapter(new GaugePagerAdapter(getApplicationContext(), gauge, getSupportFragmentManager()));

        indicator.setViewPager(pager);
        pager.setCurrentItem(1);
    }
}
