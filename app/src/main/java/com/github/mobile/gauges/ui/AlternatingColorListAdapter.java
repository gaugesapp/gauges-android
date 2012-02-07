package com.github.mobile.gauges.ui;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import com.github.mobile.gauges.R.color;
import com.madgag.android.listviews.ViewCreator;
import com.madgag.android.listviews.ViewHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

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
