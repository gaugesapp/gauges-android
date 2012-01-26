package com.github.mobile.gauges.ui;

import android.view.View;
import android.widget.TextView;

import com.github.mobile.gauges.core.DatedViewSummary;
import com.madgag.android.listviews.ViewHolder;
import com.github.mobile.gauges.R.id;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

/**
 * View holder for a traffic entry
 */
public class TrafficViewHolder implements ViewHolder<DatedViewSummary> {

	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(
			"MMMM dd");

	private final TextView dateText;

	private final TextView viewsText;

	private final TextView peopleText;

	/**
	 * Create traffic view holder
	 *
	 * @param view
	 */
	public TrafficViewHolder(final View view) {
		dateText = (TextView) view.findViewById(id.tv_traffic_date);
		viewsText = (TextView) view.findViewById(id.tv_traffic_views);
		peopleText = (TextView) view.findViewById(id.tv_traffic_people);
	}

	public void updateViewFor(final DatedViewSummary item) {
		dateText.setText(FORMATTER.format(item.getDate()));
		viewsText.setText(MessageFormat.format("{0}", item.getViews()));
		peopleText.setText(MessageFormat.format("{0}", item.getPeople()));
	}
}
