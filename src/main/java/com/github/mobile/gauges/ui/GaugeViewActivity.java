package com.github.mobile.gauges.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.github.mobile.gauges.R;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.core.Gauge;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import roboguice.activity.RoboFragmentActivity;

/**
 * Activity to view a specific {@link Gauge}
 */
public class GaugeViewActivity extends RoboFragmentActivity {

	/**
	 * Pager adapter for a gauge
	 */
	public static class PagerAdapter extends FragmentPagerAdapter implements
			TitleProvider {

		private static final String[] TITLES = { "Content", "Traffic",
				"Referrers" };

		private final Gauge gauge;

		/**
		 * Create pager adapter
		 *
		 * @param gauge
		 * @param fragmentManager
		 */
		public PagerAdapter(Gauge gauge, FragmentManager fragmentManager) {
			super(fragmentManager);
			this.gauge = gauge;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Fragment getItem(int position) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("gauge", gauge);
			bundle.putString("gaugeId", gauge.getId());
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
			return TITLES[position];
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gauge_view);

		Gauge gauge = (Gauge) getIntent().getSerializableExtra("gauge");

		ViewPager pager = (ViewPager) findViewById(id.vp_pages);
		pager.setAdapter(new PagerAdapter(gauge, getSupportFragmentManager()));

		((TitlePageIndicator) findViewById(id.tpi_header)).setViewPager(pager);
		pager.setCurrentItem(1);
	}
}
