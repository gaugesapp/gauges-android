package com.github.mobile.gauges.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.PaintDrawable;

import com.github.mobile.gauges.core.DatedViewSummary;
import com.github.mobile.gauges.core.ViewSummary;

/**
 *
 */
public class LastMonthGraphDrawable extends PaintDrawable {

	private static final int COLOR_VIEWS_WEEKDAY = Color.parseColor("#53685E");

	private static final int COLOR_PEOPLE_WEEKDAY = Color.parseColor("#6E9180");

	private static final int MIN_HEIGHT = 4;

	private static final int SPACING_X = 2;

	private DatedViewSummary[] entries;

	private long max = 1;

	/**
	 * Create drawable for view summaries
	 *
	 * @param entries
	 *            last month of entries
	 */
	public LastMonthGraphDrawable(DatedViewSummary[] entries) {
		this.entries = entries;
		for (ViewSummary entry : entries)
			max = Math.max(max, Math.max(entry.getPeople(), entry.getViews()));
	}

	@Override
	public void draw(Canvas canvas) {
		Paint paint = getPaint();
		Rect bound = getBounds();
		int x = 0;
		int width = bound.width() / entries.length;
		width -= SPACING_X;
		int height = bound.height();
		for (DatedViewSummary entry : entries) {
			long views = 0;
			long people = 0;
			if (entry != null) {
				views = entry.getViews();
				people = entry.getPeople();
			}

			paint.setColor(COLOR_VIEWS_WEEKDAY);
			float percentage = (float) views / max;
			canvas.drawRect(x,
					height - Math.max(MIN_HEIGHT, percentage * height), x
							+ width, bound.bottom, paint);

			paint.setColor(COLOR_PEOPLE_WEEKDAY);
			percentage = (float) people / max;
			canvas.drawRect(x,
					height - Math.max(MIN_HEIGHT, percentage * height), x
							+ width, bound.bottom, paint);

			x += width + SPACING_X;
		}
	}
}
