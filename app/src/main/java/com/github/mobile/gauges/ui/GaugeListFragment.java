package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.IntentConstants.GAUGES;
import static com.github.mobile.gauges.IntentConstants.VIEW_AIR_TRAFFIC;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.view.MenuItem;
import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R.drawable;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.Gauge;
import com.google.inject.Inject;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.io.Serializable;
import java.util.List;

/**
 * Fragment to display a list of gauges
 */
public class GaugeListFragment extends ListLoadingFragment<Gauge> {

    private OnGaugeSelectedListener containerCallback;

    private List<Gauge> gauges;

    @Inject
    private GaugesServiceProvider serviceProvider;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnGaugeSelectedListener)
            containerCallback = (OnGaugeSelectedListener) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = getListView();
        listView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        listView.setDivider(getResources().getDrawable(drawable.gauge_divider));
        listView.setDividerHeight(2);
        listView.setFastScrollEnabled(true);
    }

    @Override
    public void onLoadFinished(Loader<List<Gauge>> loader, List<Gauge> items) {
        super.onLoadFinished(loader, items);
        gauges = items;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case id.air_traffic:
            Intent intent = new Intent(VIEW_AIR_TRAFFIC);
            if (gauges != null && !gauges.isEmpty())
                intent.putExtra(GAUGES, (Serializable) gauges);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
