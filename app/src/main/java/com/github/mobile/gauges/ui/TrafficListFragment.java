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
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.R.string;
import com.github.mobile.gauges.core.DatedViewSummary;
import com.github.mobile.gauges.core.Gauge;
import com.google.inject.Inject;

import java.util.Collections;
import java.util.List;

import roboguice.inject.InjectExtra;

/**
 * Fragment to display list of recent traffic
 */
public class TrafficListFragment extends ItemListFragment<DatedViewSummary> {

    @Inject
    private GaugesServiceProvider serviceProvider;

    @InjectExtra(value = GAUGE, optional = true)
    private Gauge gauge;

    @InjectExtra(value = GAUGE_ID, optional = true)
    private String gaugeId;

    private GaugeGraphView barGraph;

    @Override
    protected void configureList(Activity activity, ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);

        View graph = activity.getLayoutInflater().inflate(layout.traffic_graph,
                null);
        barGraph = (GaugeGraphView) graph.findViewById(id.gauge_graph);
        getListAdapter().addHeader(graph);
        getListAdapter().addHeader(
                activity.getLayoutInflater().inflate(
                        layout.traffic_list_item_labels, null));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (gauge != null && gaugeId == null)
            gaugeId = gauge.getId();

        setEmptyText(string.no_traffic);
    }

    @Override
    public Loader<List<DatedViewSummary>> onCreateLoader(int id, Bundle args) {
        final List<DatedViewSummary> initialItems = items;
        return new ThrowableLoader<List<DatedViewSummary>>(getActivity(), items) {

            @Override
            public List<DatedViewSummary> loadData() throws Exception {
                final Gauge current = gauge;
                if (current != null)
                    return current.getRecentDays();
                try {
                    Gauge latest = serviceProvider.getService().getGauge(
                            gaugeId);
                    if (latest != null)
                        return latest.getRecentDays();
                    else
                        return Collections.emptyList();
                } catch (OperationCanceledException e) {
                    Activity activity = getActivity();
                    if (activity != null)
                        activity.finish();
                    return initialItems;
                }
            }
        };
    }

    @Override
    protected void forceRefresh() {
        gauge = null;

        super.forceRefresh();
    }

    @Override
    public void refresh() {
        gauge = null;

        super.refresh();
    }

    @Override
    public void onLoadFinished(Loader<List<DatedViewSummary>> loader,
            List<DatedViewSummary> trafficData) {
        super.onLoadFinished(loader, trafficData);

        if (!trafficData.isEmpty()) {
            barGraph.setNumDays(trafficData.size());
            barGraph.updateGraphWith(trafficData);
            ViewUtils.setGone(barGraph, false);
        } else
            ViewUtils.setGone(barGraph, true);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return string.error_loading_traffic;
    }

    @Override
    protected SingleTypeAdapter<DatedViewSummary> createAdapter(
            List<DatedViewSummary> items) {
        return new TrafficListAdapter(getActivity().getLayoutInflater(), items);
    }
}
