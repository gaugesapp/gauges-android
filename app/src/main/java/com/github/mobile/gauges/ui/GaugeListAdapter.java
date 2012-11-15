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

import android.view.LayoutInflater;
import android.view.View;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.DatedViewSummary;
import com.github.mobile.gauges.core.Gauge;

import java.util.Collection;

/**
 * Adapter to display a list of gauges
 */
public class GaugeListAdapter extends SingleTypeAdapter<Gauge> {

    /**
     * Create adapter to display a list of gauges
     *
     * @param inflater
     * @param gauges
     */
    public GaugeListAdapter(LayoutInflater inflater, Collection<Gauge> gauges) {
        super(inflater, layout.gauge_list_item);

        setItems(gauges);
    }

    protected View initialize(View view) {
        view = super.initialize(view);

        GaugeGraphView graph = view(view, 3);
        graph.setNumDays(7);
        return view;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { id.tv_gauge_name, id.tv_gauge_views,
                id.tv_gauge_people, id.gauge_graph };
    }

    @Override
    protected void update(final int position, final Gauge gauge) {
        setText(0, gauge.getTitle());

        DatedViewSummary today = gauge.getToday();
        long views;
        long people;
        if (today != null) {
            views = today.getViews();
            people = today.getPeople();
        } else {
            views = 0;
            people = 0;
        }
        setNumber(1, views);
        setNumber(2, people);

        GaugeGraphView graph = view(3);
        graph.updateGraphWith(gauge.getRecentDays());
    }
}
