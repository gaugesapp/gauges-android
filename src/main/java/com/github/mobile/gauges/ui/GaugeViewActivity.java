package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import static com.github.mobile.gauges.IntentConstants.GAUGE_ID;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;

import com.github.mobile.gauges.R;
import com.github.mobile.gauges.R.color;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.menu;
import com.github.mobile.gauges.R.string;
import com.github.mobile.gauges.core.Gauge;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import roboguice.activity.RoboFragmentActivity;

/**
 * Activity to view a specific {@link Gauge}'s traffic, content, and referrer information
 */
public class GaugeViewActivity extends RoboFragmentActivity {

    /**
     * Pager adapter for a gauge
     */
    public static class GaugePagerAdapter extends FragmentPagerAdapter implements TitleProvider {

        private final Context context;

        private final Gauge gauge;

        /**
         * Create pager adapter
         *
         * @param context
         *
         * @param gauge
         * @param fragmentManager
         */
        public GaugePagerAdapter(Context context, Gauge gauge, FragmentManager fragmentManager) {
            super(fragmentManager);
            this.context = context;
            this.gauge = gauge;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(GAUGE, gauge);
            bundle.putString(GAUGE_ID, gauge.getId());
            switch (position) {
            case 0:
                ContentListFragment contentFragment = new ContentListFragment();
                contentFragment.setArguments(bundle);
                return contentFragment;
            case 1:
                TrafficListFragment trafficFragment = new TrafficListFragment();
                trafficFragment.setArguments(bundle);
                return trafficFragment;
            case 2:
                ReferrerListFragment referrerFragment = new ReferrerListFragment();
                referrerFragment.setArguments(bundle);
                return referrerFragment;
            default:
                return null;
            }
        }

        public String getTitle(int position) {
            switch (position) {
            case 0:
                return context.getString(string.page_content);
            case 1:
                return context.getString(string.page_traffic);
            case 2:
                return context.getString(string.page_referrers);
            default:
                return null;
            }
        }
    }

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
        indicator.setTextColor(getResources().getColor(color.text_light));
        indicator.setFooterColor(getResources().getColor(color.text_light));
        indicator.setViewPager(pager);
        pager.setCurrentItem(1);
    }
}
