package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import com.github.mobile.gauges.R.color;
import android.R;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.DatedViewSummary;
import com.github.mobile.gauges.core.Gauge;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment to display list of recent traffic
 */
public class TrafficListFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Gauge gauge = (Gauge) getArguments().getSerializable(GAUGE);

		final List<DatedViewSummary> recentDays = new ArrayList<DatedViewSummary>(
				gauge.getRecentDays());
		final DatedViewSummary[] graphDays = recentDays
				.toArray(new DatedViewSummary[recentDays.size()]);
		final long[][] data = new long[graphDays.length][];
		final int[][] colors = new int[data.length][];
		Arrays.fill(colors,
				new int[] { getResources().getColor(color.graph_views_weekday),
						getResources().getColor(color.graph_people_weekday) });
		for (int i = 0; i < graphDays.length; i++)
			data[graphDays.length - 1 - i] = new long[] {
					graphDays[i].getViews(), graphDays[i].getPeople() };

		View view = getLayoutInflater(savedInstanceState).inflate(
				layout.traffic_graph, null);
		view.findViewById(id.ll_bars).setBackgroundDrawable(
				new BarGraphDrawable(data, colors));

		ListView listView = getListView();

		listView.addHeaderView(view);
		listView.addHeaderView(getLayoutInflater(savedInstanceState).inflate(
				layout.traffic_list_item_labels, null));

		listView.setSelector(R.color.transparent);
		listView.setCacheColorHint(getResources().getColor(R.color.transparent));
		listView.setDrawSelectorOnTop(false);

		setListAdapter(new ViewHoldingListAdapter<DatedViewSummary>(recentDays,
				ViewInflator.viewInflatorFor(getActivity(),
						layout.traffic_list_item),
				ReflectiveHolderFactory
						.reflectiveFactoryFor(TrafficViewHolder.class)));
	}
}
