package com.github.mobile.gauges.ui;

import android.R;
import android.os.Bundle;
import android.support.v4.app.SupportActivity;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.Gauge;
import com.google.inject.Inject;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.util.List;

/**
 * Fragment to display a list of gauges
 */
public class GaugeListFragment extends ListLoadingFragment<Gauge> {

    private OnGaugeSelectedListener containerCallback;

    @Inject
    private GaugesServiceProvider serviceProvider;

    @Override
    public void onAttach(SupportActivity activity) {
        super.onAttach(activity);
        if (activity instanceof OnGaugeSelectedListener)
            containerCallback = (OnGaugeSelectedListener) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = getListView();
        listView.setCacheColorHint(getResources().getColor(R.color.transparent));
        listView.setFastScrollEnabled(true);
    }

    @Override
    public Loader<List<Gauge>> onCreateLoader(int id, Bundle args) {
        return new GaugeListLoader(getActivity(), serviceProvider);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (containerCallback != null)
            containerCallback.onGaugeSelected((Gauge) l.getItemAtPosition(position));
    }

    @Override
    protected ListAdapter adapterFor(List<Gauge> items) {
        return new ViewHoldingListAdapter<Gauge>(items, ViewInflator.viewInflatorFor(getActivity(),
                layout.gauge_list_item), ReflectiveHolderFactory.reflectiveFactoryFor(GaugeViewHolder.class,
                getActivity().getResources()));
    }
}
