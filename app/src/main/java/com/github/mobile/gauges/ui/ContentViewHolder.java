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
import com.github.mobile.gauges.core.PageContent;
import com.madgag.android.listviews.ViewHolder;

import java.text.NumberFormat;

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
        viewsText.setText(NumberFormat.getIntegerInstance().format(item.getViews()));
    }
}
