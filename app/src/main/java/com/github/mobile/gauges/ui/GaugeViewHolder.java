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

import android.view.View;
import android.widget.TextView;

import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.core.DatedViewSummary;
import com.github.mobile.gauges.core.Gauge;
import com.madgag.android.listviews.ViewHolder;

import java.text.NumberFormat;

/**
 * View holder for a {@link Gauge}
 */
public class GaugeViewHolder implements ViewHolder<Gauge> {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance();

    private final TextView nameText;

    private final TextView viewsText;

    private final TextView peopleText;

    private final GaugeGraphView barGraph;

    /**
     * Create view holder
     *
     * @param view
     */
    public GaugeViewHolder(final View view) {
        nameText = (TextView) view.findViewById(id.tv_gauge_name);
        viewsText = (TextView) view.findViewById(id.tv_gauge_views);
        peopleText = (TextView) view.findViewById(id.tv_gauge_people);
        barGraph = (GaugeGraphView) view.findViewById(id.gauge_graph);
        barGraph.setNumDays(7);
    }

    public void updateViewFor(final Gauge gauge) {
        nameText.setText(gauge.getTitle());
        DatedViewSummary today = gauge.getToday();
        long viewsToday = 0;
        long peopleToday = 0;
        if (today != null) {
            viewsToday = today.getViews();
            peopleToday = today.getPeople();
        }

        viewsText.setText(NUMBER_FORMAT.format(viewsToday));
        peopleText.setText(NUMBER_FORMAT.format(peopleToday));
        barGraph.updateGraphWith(gauge.getRecentDays());
    }

}
