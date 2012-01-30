package com.github.mobile.gauges.ui;


import android.R;
import android.accounts.AccountsException;
import android.os.Bundle;
import android.support.v4.app.SupportActivity;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.authenticator.ApiKeyProvider;
import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.core.GaugesService;
import com.google.inject.Inject;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Fragment to display a list of gauges
 */
public class GaugeListFragment extends ListLoadingFragment<Gauge> {

    private final static String TAG = "GLF";

    private @Inject ApiKeyProvider apiKeyProvider;
    private GaugeListEventsCallback containerCallback;

    @Override
    public void onAttach(SupportActivity activity) {
        super.onAttach(activity);
        try {
            containerCallback = (GaugeListEventsCallback) activity;
        } catch (ClassCastException e) {
            activity.finish();
            throw new ClassCastException(activity.toString() + " must implement "
                    + GaugeListEventsCallback.class.getSimpleName());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setCacheColorHint(getResources().getColor(R.color.transparent));
    }

    public Loader<List<Gauge>> onCreateLoader(int id, Bundle args) {
        return new AsyncLoader<List<Gauge>>(getActivity()) {
            public List<Gauge> loadInBackground() {
                try {
                    return new GaugesService(apiKeyProvider.getAuthKey()).getGauges();
                } catch (IOException e) {
                    Log.d(TAG, "Exception getting gauges", e);
                } catch (AccountsException e) {
                    Log.d(TAG, "Exception getting gauges", e);
                }
                return Collections.emptyList();
            }
        };
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        containerCallback.onGaugeSelected((Gauge) l.getItemAtPosition(position));
    }

    protected ListAdapter adapterFor(List<Gauge> items) {
        return new ViewHoldingListAdapter<Gauge>(items, ViewInflator.viewInflatorFor(getActivity(),
                layout.gauge_list_item), ReflectiveHolderFactory.reflectiveFactoryFor(GaugeViewHolder.class,
                getActivity().getResources()));
    }

    public static interface GaugeListEventsCallback {
        public void onGaugeSelected(Gauge gauge);
    }
}
