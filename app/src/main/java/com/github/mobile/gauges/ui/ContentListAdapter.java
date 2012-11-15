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
import com.github.mobile.gauges.core.PageContent;

import java.util.List;

/**
 * Adapter to display a list of page contents
 */
public class ContentListAdapter extends
        AlternatingColorListAdapter<PageContent> {

    /**
     * @param inflater
     * @param items
     * @param selectable
     */
    public ContentListAdapter(LayoutInflater inflater, List<PageContent> items,
            boolean selectable) {
        super(layout.content_list_item, inflater, items, selectable);
    }

    /**
     * @param inflater
     * @param items
     */
    public ContentListAdapter(LayoutInflater inflater, List<PageContent> items) {
        super(layout.content_list_item, inflater, items);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { id.tv_content_title, id.tv_content_path,
                id.tv_content_views };
    }

    @Override
    protected void update(int position, PageContent item) {
        super.update(position, item);

        setText(0, item.getTitle());
        setText(1, item.getPath());
        setNumber(2, item.getViews());
    }
}
