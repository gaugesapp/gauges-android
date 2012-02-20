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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.view.MenuItem;
import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R.drawable;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.Gauge;
import com.google.inject.Inject;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.io.Serializable;
import java.util.List;

/**
 * Fragment to display a list of gauges
 */
public class GaugeListFragment extends ListLoadingFragment<Gauge> {

    private List<Gauge> gauges;

    @Inject
    private GaugesServiceProvider serviceProvider;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = getListView();
        listView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        listView.setDivider(getResources().getDrawable(drawable.gauge_divider));
        listView.setDividerHeight(2);
        listView.setFastScrollEnabled(true);
    }

    @Override
    public void onLoadFinished(Loader<List<Gauge>> loader, List<Gauge> items) {
        super.onLoadFinished(loader, items);
        gauges = items;
    }

    @Override
    public Loader<List<Gauge>> onCreateLoader(int id, Bundle args) {
        return new GaugeListLoader(getActivity(), serviceProvider);
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {
        Gauge gauge = (Gauge) list.getItemAtPosition(position);
        Intent intent = new Intent(VIEW_GAUGE);
        intent.putExtra(GAUGE, gauge);
        startActivity(intent);
    }

    @Override
    protected ListAdapter adapterFor(List<Gauge> items) {
        return new ViewHoldingListAdapter<Gauge>(items, ViewInflator.viewInflatorFor(getActivity(),
                layout.gauge_list_item), ReflectiveHolderFactory.reflectiveFactoryFor(GaugeViewHolder.class,
                getActivity().getResources()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case id.air_traffic:
            Intent intent = new Intent(VIEW_AIR_TRAFFIC);
            if (gauges != null && !gauges.isEmpty())
                intent.putExtra(GAUGES, (Serializable) gauges);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
