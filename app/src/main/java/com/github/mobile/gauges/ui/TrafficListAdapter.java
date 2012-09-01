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

import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.DatedViewSummary;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Adapter to display a list of traffic items
 */
public class TrafficListAdapter extends
        AlternatingColorListAdapter<DatedViewSummary> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "MMMM dd");

    /**
     * @param inflater
     * @param items
     * @param selectable
     */
    public TrafficListAdapter(LayoutInflater inflater,
            List<DatedViewSummary> items, boolean selectable) {
        super(layout.traffic_list_item, inflater, items, selectable);
    }

    /**
     * @param inflater
     * @param items
     */
    public TrafficListAdapter(LayoutInflater inflater,
            List<DatedViewSummary> items) {
        super(layout.traffic_list_item, inflater, items);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { id.tv_traffic_date, id.tv_traffic_views,
                id.tv_traffic_people };
    }

    @Override
    protected void update(int position, DatedViewSummary item) {
        super.update(position, item);

        setText(0, DATE_FORMAT.format(item.getDate()));
        setNumber(1, item.getViews());
        setNumber(2, item.getPeople());
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
