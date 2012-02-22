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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.PaintDrawable;

/**
 * Drawable bar graph
 */
public class BarGraphDrawable extends PaintDrawable {

    private static final int MIN_HEIGHT = 4;

    private static final int SPACING_X = 1;

    private final int[][] colors;

    private final long[][] data;

    private long max = 1;

    /**
     * Create drawable bar graph for data and colors
     *
     * @param data
     * @param colors
     */
    public BarGraphDrawable(final long[][] data, final int[][] colors) {
        super(android.R.color.transparent);
        this.data = data;
        this.colors = colors;
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[i].length; j++)
                max = Math.max(max, data[i][j]);
    }

    @Override
    public void draw(final Canvas canvas) {
        final Paint paint = getPaint();
        final Rect bounds = getBounds();
        final float width = ((float) bounds.width() / data.length) - SPACING_X;
        final int height = bounds.height();
        float x = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                paint.setColor(colors[i][j]);
                float percentage = (float) data[i][j] / max;
                canvas.drawRect(x, height - Math.max(MIN_HEIGHT, percentage * height), x + width, bounds.bottom, paint);
            }
            x += width + SPACING_X;
        }
    }
}
