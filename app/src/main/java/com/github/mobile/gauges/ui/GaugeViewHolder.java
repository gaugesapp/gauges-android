package com.github.mobile.gauges.ui;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mobile.gauges.R.color;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.core.DatedViewSummary;
import com.github.mobile.gauges.core.Gauge;
import com.madgag.android.listviews.ViewHolder;

import java.text.NumberFormat;
import java.util.GregorianCalendar;

/**
 * View holder for a {@link Gauge}
 */
public class GaugeViewHolder implements ViewHolder<Gauge> {

    private final TextView nameText;

    private final TextView viewsText;

    private final TextView peopleText;

    private final LinearLayout barGraph;

    private final int[] weekendColors;

    private final int[] weekdayColors;

    private final long[][] data;

    private final int[][] colors;

    /**
     * Create view holder
     *
     * @param view
     * @param resources
     */
    public GaugeViewHolder(final View view, final Resources resources) {
        nameText = (TextView) view.findViewById(id.tv_gauge_name);
        viewsText = (TextView) view.findViewById(id.tv_gauge_views);
        peopleText = (TextView) view.findViewById(id.tv_gauge_people);
        barGraph = (LinearLayout) view.findViewById(id.ll_bars);
        data = new long[7][];
        colors = new int[7][];
        weekdayColors = new int[] { resources.getColor(color.graph_views_weekday),
                resources.getColor(color.graph_people_weekday) };
        weekendColors = new int[] { resources.getColor(color.graph_views_weekend),
                resources.getColor(color.graph_people_weekend) };
    }

    public void updateViewFor(final Gauge gauge) {
        nameText.setText(gauge.getTitle());
        viewsText.setText(NumberFormat.getIntegerInstance().format(gauge.getToday().getViews()));
        peopleText.setText(NumberFormat.getIntegerInstance().format(gauge.getToday().getPeople()));
        int index = data.length - 1;
        GregorianCalendar calendar = new GregorianCalendar();
        for (DatedViewSummary day : gauge.getRecentDays()) {
            data[index] = new long[] { day.getViews(), day.getPeople() };
            calendar.setTime(day.getDate());
            int dayOfWeek = calendar.get(DAY_OF_WEEK);
            if (dayOfWeek == SATURDAY || dayOfWeek == SUNDAY)
                colors[index] = weekendColors;
            else
                colors[index] = weekdayColors;
            index--;
            if (index < 0)
                break;
        }
        barGraph.setBackgroundDrawable(new BarGraphDrawable(data, colors));
    }
}
