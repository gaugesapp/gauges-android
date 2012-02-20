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

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import com.github.mobile.gauges.R.color;
import com.madgag.android.listviews.ViewCreator;
import com.madgag.android.listviews.ViewHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.util.List;

/**
 * List adapter that colors rows in alternating colors
 *
 * @param <V>
 */
public class AlternatingColorListAdapter<V> extends ViewHoldingListAdapter<V> {

    private int bgPrimary;

    private int bgSecondary;

    /**
     * @param resources
     * @param items
     * @param creator
     * @param holderFactory
     */
    public AlternatingColorListAdapter(final Resources resources, final List<V> items, final ViewCreator creator,
            final ViewHolderFactory<V> holderFactory) {
        super(items, creator, holderFactory);
        bgPrimary = resources.getColor(color.pager_background);
        bgSecondary = resources.getColor(color.pager_background_alternate);
    }

    /**
     * @param primaryColor
     * @param secondaryColor
     * @param itemList
     * @param c
     * @param vhf
     */
    public AlternatingColorListAdapter(int primaryColor, int secondaryColor, final List<V> itemList,
            final ViewCreator c, final ViewHolderFactory<V> vhf) {
        super(itemList, c, vhf);
        bgPrimary = primaryColor;
        bgSecondary = secondaryColor;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        View view = super.getView(index, convertView, parent);
        if (index % 2 != 0)
            view.setBackgroundColor(bgPrimary);
        else
            view.setBackgroundColor(bgSecondary);
        return view;
    }
}
