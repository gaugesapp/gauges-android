/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mobile.gauges.ui;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.github.mobile.gauges.IntentConstants.GAUGE;
import static com.github.mobile.gauges.IntentConstants.GAUGES;
import static com.github.mobile.gauges.IntentConstants.VIEW_AIR_TRAFFIC;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.view.MenuItem;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.Gauge;
import com.viewpagerindicator.TitlePageIndicator;

import java.io.Serializable;
import java.util.Collections;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Activity to view a specific {@link Gauge}'s traffic, content, and referrer information
 */
public class GaugeViewActivity extends RoboFragmentActivity {

    @InjectView(id.tpi_header)
    private TitlePageIndicator indicator;

    @InjectView(id.vp_pages)
    private ViewPager pager;

    @InjectExtra(GAUGE)
    private Gauge gauge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.gauge_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(gauge.getTitle());

        pager.setAdapter(new GaugePagerAdapter(getResources(), gauge, getSupportFragmentManager()));

        indicator.setViewPager(pager);
        pager.setCurrentItem(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent homeIntent = new Intent(this, GaugeListActivity.class);
            homeIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
            return true;
        case id.air_traffic:
            Intent trafficIntent = new Intent(VIEW_AIR_TRAFFIC);
            trafficIntent.putExtra(GAUGES, (Serializable) Collections.singletonList(gauge));
            startActivity(trafficIntent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
