package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.DatedViewSummary;
import com.github.mobile.gauges.core.Gauge;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.util.List;

/**
 * Fragment to display list of recent traffic
 */
public class TrafficListFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Gauge gauge = (Gauge) getArguments().getSerializable(GAUGE);
		List<DatedViewSummary> recentDays = gauge.getRecentDays();

		View view = getLayoutInflater(savedInstanceState).inflate(
				layout.traffic_graph, null);
		DatedViewSummary[] graphDays = recentDays
				.toArray(new DatedViewSummary[recentDays.size()]);
		for (int i = 0; i < graphDays.length / 2; i++) {
			DatedViewSummary swap = graphDays[i];
			graphDays[i] = graphDays[graphDays.length - 1 - i];
			graphDays[graphDays.length - 1 - i] = swap;
		}

		(view.findViewById(id.ll_bars))
				.setBackgroundDrawable(new LastMonthGraphDrawable(graphDays));
		getListView().addHeaderView(view);

		setListAdapter(new ViewHoldingListAdapter<DatedViewSummary>(recentDays,
				ViewInflator.viewInflatorFor(getActivity(),
						layout.traffic_list_item),
				ReflectiveHolderFactory
						.reflectiveFactoryFor(TrafficViewHolder.class)));
	}
}
