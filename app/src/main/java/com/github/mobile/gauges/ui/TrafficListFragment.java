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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.github.mobile.gauges.IntentConstants.GAUGE;
import static com.github.mobile.gauges.IntentConstants.GAUGE_ID;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import android.accounts.AccountsException;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R.color;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.R.string;
import com.github.mobile.gauges.core.DatedViewSummary;
import com.github.mobile.gauges.core.Gauge;
import com.google.inject.Inject;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewInflator;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import roboguice.inject.InjectExtra;

/**
 * Fragment to display list of recent traffic
 */
public class TrafficListFragment extends ListLoadingFragment<DatedViewSummary> {

    private static final String TAG = "TLF";

    @Inject
    private GaugesServiceProvider serviceProvider;

    @InjectExtra(value = GAUGE, optional = true)
    private Gauge gauge;

    @InjectExtra(value = GAUGE_ID, optional = true)
    private String gaugeId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (gauge != null && gaugeId == null)
            gaugeId = gauge.getId();

        ListView listView = getListView();

        if (getListAdapter() == null) {
            listView.addHeaderView(getLayoutInflater(savedInstanceState).inflate(layout.traffic_graph, null), null,
                    false);
            listView.addHeaderView(
                    getLayoutInflater(savedInstanceState).inflate(layout.traffic_list_item_labels, null), null, false);
        }

        listView.setSelector(android.R.color.transparent);
        listView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        listView.setDrawSelectorOnTop(false);
        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);
    }

    @Override
    public Loader<List<DatedViewSummary>> onCreateLoader(int id, Bundle args) {
        return new AsyncLoader<List<DatedViewSummary>>(getActivity()) {

            public List<DatedViewSummary> loadInBackground() {
                final Gauge current = gauge;
                if (current != null)
                    return current.getRecentDays();
                try {
                    Gauge latest = serviceProvider.getService().getGauge(gaugeId);
                    if (latest != null)
                        return latest.getRecentDays();
                } catch (IOException e) {
                    Log.d(TAG, "Exception getting gauge", e);
                    showError(string.error_loading_traffic);
                } catch (AccountsException e) {
                    Log.d(TAG, "Exception getting gauge", e);
                    showError(string.error_loading_traffic);
                }
                return Collections.emptyList();
            }
        };
    }

    @Override
    public void refresh() {
        gauge = null;
        super.refresh();
    }

    @Override
    public void onLoadFinished(Loader<List<DatedViewSummary>> loader, List<DatedViewSummary> items) {
        super.onLoadFinished(loader, items);

        View barGraph = getListView().findViewById(id.ll_bars);
        if (!items.isEmpty()) {
            final int dayCount = items.size();
            final DatedViewSummary[] graphDays = items.toArray(new DatedViewSummary[dayCount]);
            final long[][] data = new long[dayCount][];
            final int[][] colors = new int[dayCount][];

            final Calendar calendar = new GregorianCalendar();
            final int[] weekdayColors = new int[] { getResources().getColor(color.traffic_views_weekday),
                    getResources().getColor(color.traffic_people_weekday) };
            final int[] weekendColors = new int[] { getResources().getColor(color.traffic_views_weekend),
                    getResources().getColor(color.traffic_people_weekend) };

            for (int i = 0; i < dayCount; i++) {
                // Reverse entry order since entries are in reverse chronological order but graph is drawn left to right
                int index = dayCount - 1 - i;

                calendar.setTime(graphDays[i].getDate());
                int dayOfWeek = calendar.get(DAY_OF_WEEK);
                if (dayOfWeek == SATURDAY || dayOfWeek == SUNDAY)
                    colors[index] = weekendColors;
                else
                    colors[index] = weekdayColors;

                data[index] = new long[] { graphDays[i].getViews(), graphDays[i].getPeople() };
            }

            barGraph.setVisibility(VISIBLE);
            barGraph.setBackgroundDrawable(new BarGraphDrawable(data, colors));
        } else
            barGraph.setVisibility(GONE);
    }

    @Override
    protected ListAdapter adapterFor(List<DatedViewSummary> items) {
        return new AlternatingColorListAdapter<DatedViewSummary>(getActivity().getResources(), items,
                ViewInflator.viewInflatorFor(getActivity(), layout.traffic_list_item),
                ReflectiveHolderFactory.reflectiveFactoryFor(TrafficViewHolder.class));
    }
}
