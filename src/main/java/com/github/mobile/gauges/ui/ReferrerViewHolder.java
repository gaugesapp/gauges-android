package com.github.mobile.gauges.ui;

import android.view.View;
import android.widget.TextView;

import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.core.Referrer;
import com.madgag.android.listviews.ViewHolder;

import java.text.MessageFormat;

/**
 * View holder for a {@link Referrer}
 */
public class ReferrerViewHolder implements ViewHolder<Referrer> {

	private final TextView hostText;

	private final TextView pathText;

	private final TextView viewsText;

	/**
	 * Create view holder
	 *
	 * @param view
	 */
	public ReferrerViewHolder(final View view) {
		hostText = (TextView) view.findViewById(id.tv_referrer_host);
		pathText = (TextView) view.findViewById(id.tv_referrer_path);
		viewsText = (TextView) view.findViewById(id.tv_referrer_views);
	}

	public void updateViewFor(final Referrer item) {
		hostText.setText(item.getHost());
		pathText.setText(item.getPath());
		viewsText.setText(MessageFormat.format("{0}", item.getViews()));
	}
}
