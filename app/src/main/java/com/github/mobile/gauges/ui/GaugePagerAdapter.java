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

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import static com.github.mobile.gauges.IntentConstants.GAUGE_ID;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.github.mobile.gauges.R.string;
import com.github.mobile.gauges.core.Gauge;

/**
 * Pager adapter for a gauge
 */
public class GaugePagerAdapter extends FragmentPagerAdapter {

    private final Resources resources;

    private final Gauge gauge;

    /**
     * Create pager adapter
     *
     * @param gauge
     * @param activity
     */
    public GaugePagerAdapter(Gauge gauge, SherlockFragmentActivity activity) {
        super(activity);

        this.resources = activity.getResources();
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

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
        case 0:
            return resources.getString(string.page_content);
        case 1:
            return resources.getString(string.page_traffic);
        case 2:
            return resources.getString(string.page_referrers);
        default:
            return null;
        }
    }
}
