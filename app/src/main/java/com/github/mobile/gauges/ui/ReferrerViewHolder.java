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
import com.github.mobile.gauges.core.Referrer;
import com.madgag.android.listviews.ViewHolder;

import java.text.NumberFormat;

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
        viewsText.setText(NumberFormat.getIntegerInstance().format(item.getViews()));
    }
}
