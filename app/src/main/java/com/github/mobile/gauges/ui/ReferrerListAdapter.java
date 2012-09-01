/******************************************************************************
 *  Copyright (c) 2012 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package com.github.mobile.gauges.ui;

import android.view.LayoutInflater;

import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.Referrer;

import java.util.List;

/**
 *
 */
public class ReferrerListAdapter extends AlternatingColorListAdapter<Referrer> {

    /**
     * @param inflater
     * @param items
     * @param selectable
     */
    public ReferrerListAdapter(LayoutInflater inflater, List<Referrer> items,
            boolean selectable) {
        super(layout.referrer_list_item, inflater, items, selectable);
    }

    /**
     * @param inflater
     * @param items
     */
    public ReferrerListAdapter(LayoutInflater inflater, List<Referrer> items) {
        super(layout.referrer_list_item, inflater, items);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { id.tv_referrer_host, id.tv_referrer_path,
                id.tv_referrer_views };
    }

    @Override
    protected void update(int position, Referrer item) {
        super.update(position, item);

        setText(0, item.getHost());
        setText(1, item.getPath());
        setNumber(2, item.getViews());
    }
}
