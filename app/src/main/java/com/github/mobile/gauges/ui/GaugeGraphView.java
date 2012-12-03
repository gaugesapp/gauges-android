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

import static com.github.mobile.gauges.R.styleable.GaugeGraphView_peopleWeekdayColor;
import static com.github.mobile.gauges.R.styleable.GaugeGraphView_peopleWeekendColor;
import static com.github.mobile.gauges.R.styleable.GaugeGraphView_viewsWeekdayColor;
import static com.github.mobile.gauges.R.styleable.GaugeGraphView_viewsWeekendColor;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.github.mobile.gauges.R;
import com.github.mobile.gauges.core.DatedViewSummary;

import java.util.GregorianCalendar;
import java.util.List;

/**
 * A styleable graph that shows the recent traffic for a gauge.
 */
public class GaugeGraphView extends LinearLayout {

    private static final long[] NO_TRAFFIC = new long[] { 0, 0 };

    private final int[] weekdayColors, weekendColors;

    private long[][] data;

    private int[][] colors;

    /**
     * Create graph view from context and attributes
     *
     * @param context
     * @param attrs
     */
    public GaugeGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GaugeGraphView);
        weekdayColors = new int[] { array.getColor(GaugeGraphView_viewsWeekdayColor, 0),
                array.getColor(GaugeGraphView_peopleWeekdayColor, 0) };
        weekendColors = new int[] { array.getColor(GaugeGraphView_viewsWeekendColor, 0),
                array.getColor(GaugeGraphView_peopleWeekendColor, 0) };

        array.recycle();
    }

    /**
     * This should be set before the graph is updated with traffic data, and will not take effect until
     * {@link #updateGraphWith(List)} is called.
     *
     * @param numDays
     *            the number of days to display in the graph, 1 bar per day
     */
    public void setNumDays(int numDays) {
        data = new long[numDays][];
        colors = new int[numDays][];
    }

    /**
     * Updates the graph to display the supplied data, which will be padded or truncated to match the number of days
     * specifed with {@link #setNumDays(int)}.
     *
     * @param trafficData
     *            a list of traffic data by day in reverse-chronological order
     */
    @SuppressWarnings("deprecation")
    public void updateGraphWith(List<DatedViewSummary> trafficData) {
        setBackgroundDrawable(createBarGraphDrawableFor(trafficData));
    }

    private BarGraphDrawable createBarGraphDrawableFor(List<DatedViewSummary> daySummaries) {
        GregorianCalendar calendar = new GregorianCalendar();
        int daySummaryIndex = 0;
        for (int barIndex = data.length - 1; barIndex >= 0; --barIndex) {
            if (daySummaryIndex < daySummaries.size()) {
                DatedViewSummary day = daySummaries.get(daySummaryIndex);
                calendar.setTime(day.getDate()); // take date from day summary returned by API
                data[barIndex] = new long[] { day.getViews(), day.getPeople() };
            } else {
                calendar.add(DAY_OF_YEAR, -1); // this bar is for the day before that of the prior iteration
                data[barIndex] = NO_TRAFFIC; // the API returned no traffic data for this day
            }

            int dayOfWeek = calendar.get(DAY_OF_WEEK);
            colors[barIndex] = (dayOfWeek == SATURDAY || dayOfWeek == SUNDAY) ? weekendColors : weekdayColors;
            ++daySummaryIndex;
        }
        return new BarGraphDrawable(data, colors);
    }
}
