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
import static com.github.mobile.gauges.IntentConstants.GAUGES;
import static com.github.mobile.gauges.IntentConstants.VIEW_AIR_TRAFFIC;
import static com.github.mobile.gauges.IntentConstants.VIEW_GAUGE;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.view.MenuItem;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.string;
import com.github.mobile.gauges.core.Gauge;
import com.google.inject.Inject;

import java.io.Serializable;
import java.util.List;

/**
 * Fragment to display a list of gauges
 */
public class GaugeListFragment extends ItemListFragment<Gauge> {

    @Inject
    private GaugesServiceProvider serviceProvider;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(string.no_gauges);
    }

    @Override
    protected void configureList(Activity activity, ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
    }

    @Override
    public Loader<List<Gauge>> onCreateLoader(int id, Bundle args) {
        return new GaugeListLoader(getActivity(), items, serviceProvider);
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {
        Gauge gauge = (Gauge) list.getItemAtPosition(position);
        Intent intent = new Intent(VIEW_GAUGE);
        intent.putExtra(GAUGE, gauge);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case id.air_traffic:
            if (getActivity() != null) {
                Intent intent = new Intent(VIEW_AIR_TRAFFIC);
                if (items != null && !items.isEmpty())
                    intent.putExtra(GAUGES, (Serializable) items);
                startActivity(intent);
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return string.error_loading_gauges;
    }

    @Override
    protected SingleTypeAdapter<Gauge> createAdapter(List<Gauge> items) {
        return new GaugeListAdapter(getActivity().getLayoutInflater(), items);
    }
}
