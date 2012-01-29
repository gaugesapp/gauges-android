package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import static com.github.mobile.gauges.IntentConstants.GAUGE_ID;
import android.R;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.mobile.gauges.R.color;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.authenticator.ApiKeyProvider;
import com.github.mobile.gauges.core.DatedViewSummary;
import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.core.GaugesService;
import com.google.inject.Inject;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Fragment to display list of recent traffic
 */
public class TrafficListFragment extends ListLoadingFragment<DatedViewSummary> {

    private static final String TAG = "TLF";

    @Inject
    private ApiKeyProvider apiKeyProvider;

    private Gauge gauge;

    private String gaugeId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gauge = (Gauge) getArguments().getSerializable(GAUGE);
        if (gauge != null)
            gaugeId = gauge.getId();
        if (gaugeId == null)
            gaugeId = getArguments().getString(GAUGE_ID);

        ListView listView = getListView();

        if (getListAdapter() == null) {
            listView.addHeaderView(getLayoutInflater(savedInstanceState).inflate(layout.traffic_graph, null), null,
                    false);
            listView.addHeaderView(
                    getLayoutInflater(savedInstanceState).inflate(layout.traffic_list_item_labels, null), null, false);
        }

        listView.setSelector(R.color.transparent);
        listView.setCacheColorHint(getResources().getColor(R.color.transparent));
        listView.setDrawSelectorOnTop(false);
    }

    public Loader<List<DatedViewSummary>> onCreateLoader(int id, Bundle args) {
        return new AsyncLoader<List<DatedViewSummary>>(getActivity()) {

            public List<DatedViewSummary> loadInBackground() {
                final Gauge current = gauge;
                if (current != null)
                    return current.getRecentDays();
                try {
                    Gauge latest = new GaugesService(apiKeyProvider.getAuthKey()).getGauge(gaugeId);
                    if (latest != null)
                        return latest.getRecentDays();
                } catch (IOException e) {
                    Log.d(TAG, "Exception getting gauge", e);
                }
                return Collections.emptyList();
            }
        };
    }

    @Override
    public void refresh() {
        gauge = null;
        super.refresh();
    }

    public void onLoadFinished(Loader<List<DatedViewSummary>> loader, List<DatedViewSummary> items) {
        super.onLoadFinished(loader, items);

        View barGraph = getListView().findViewById(id.ll_bars);
        if (!items.isEmpty()) {
            final List<DatedViewSummary> recentDays = new ArrayList<DatedViewSummary>(items);
            final DatedViewSummary[] graphDays = recentDays.toArray(new DatedViewSummary[recentDays.size()]);
            final long[][] data = new long[graphDays.length][];
            final int[][] colors = new int[data.length][];
            Arrays.fill(colors, new int[] { getResources().getColor(color.graph_views_weekday),
                    getResources().getColor(color.graph_people_weekday) });
            for (int i = 0; i < graphDays.length; i++)
                data[graphDays.length - 1 - i] = new long[] { graphDays[i].getViews(), graphDays[i].getPeople() };

            barGraph.setVisibility(View.VISIBLE);
            barGraph.setBackgroundDrawable(new BarGraphDrawable(data, colors));
        } else
            barGraph.setVisibility(View.GONE);
    }

    protected ListAdapter adapterFor(List<DatedViewSummary> items) {
        return new ViewHoldingListAdapter<DatedViewSummary>(items, ViewInflator.viewInflatorFor(getActivity(),
                layout.traffic_list_item), ReflectiveHolderFactory.reflectiveFactoryFor(TrafficViewHolder.class));
    }
}
