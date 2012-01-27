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
	public boolean onCreateOptionsMenu(Menu optionsMenu) {
		getMenuInflater().inflate(menu.gauges, optionsMenu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case id.refresh:
			((GaugeListFragment) getSupportFragmentManager().findFragmentById(
					R.id.list)).refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.gauge_list);

		if (getSupportFragmentManager().findFragmentById(R.id.list) == null)
			getSupportFragmentManager().beginTransaction()
					.add(R.id.list, new GaugeListFragment()).commit();
	}

}
