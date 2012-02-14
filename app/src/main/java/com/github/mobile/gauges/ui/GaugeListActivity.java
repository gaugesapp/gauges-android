package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import static com.github.mobile.gauges.IntentConstants.VIEW_GAUGE;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

import com.github.mobile.gauges.R;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.ui.airtraffic.AirTrafficActivity;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.airtraffic_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.airtraffic:
                Intent i = new Intent(this, AirTrafficActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
