package com.github.mobile.gauges.ui;

import android.view.View;
import android.widget.TextView;

import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.core.PageContent;
import com.madgag.android.listviews.ViewHolder;

import java.text.MessageFormat;

/**
 * View holder for a {@link PageContent} item
 */
public class ContentViewHolder implements ViewHolder<PageContent> {

	private final TextView titleText;

	private final TextView pathText;

	private final TextView viewsText;

	/**
	 * Create view holder
	 *
	 * @param view
	 */
	public ContentViewHolder(final View view) {
		titleText = (TextView) view.findViewById(id.tv_content_title);
		pathText = (TextView) view.findViewById(id.tv_content_path);
		viewsText = (TextView) view.findViewById(id.tv_content_views);
	}

	public void updateViewFor(final PageContent item) {
		titleText.setText(item.getTitle());
		pathText.setText(item.getPath());
		viewsText.setText(MessageFormat.format("{0}", item.getViews()));
	}
}
