package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;

import com.github.mobile.gauges.R;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.menu;
import com.github.mobile.gauges.core.Gauge;
import com.viewpagerindicator.TitlePageIndicator;

import roboguice.activity.RoboFragmentActivity;

/**
 * Activity to view a specific {@link Gauge}'s traffic, content, and referrer information
 */
public class GaugeViewActivity extends RoboFragmentActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu optionsMenu) {
        getMenuInflater().inflate(menu.gauges, optionsMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case id.refresh:
            ViewPager pager = (ViewPager) findViewById(id.vp_pages);
            String tag = FragmentPagerAdapter.makeFragmentName(pager.getId(), pager.getCurrentItem());
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment instanceof ListLoadingFragment<?>) {
                ((ListLoadingFragment<?>) fragment).refresh();
                return true;
            }
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gauge_view);

        Gauge gauge = (Gauge) getIntent().getSerializableExtra(GAUGE);
        setTitle(gauge.getTitle());

        ViewPager pager = (ViewPager) findViewById(id.vp_pages);
        pager.setAdapter(new GaugePagerAdapter(getApplicationContext(), gauge, getSupportFragmentManager()));

        TitlePageIndicator indicator = (TitlePageIndicator) findViewById(id.tpi_header);
        indicator.setViewPager(pager);
        pager.setCurrentItem(1);
    }
}
