package com.github.mobile.gauges.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.DatedViewSummary;
import com.github.mobile.gauges.core.Gauge;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

/**
 * Fragment to display list of recent traffic
 */
public class TrafficListFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ViewHoldingListAdapter<DatedViewSummary>(
				((Gauge) getArguments().getSerializable("gauge"))
						.getRecentDays(),
				ViewInflator.viewInflatorFor(getActivity(),
						layout.traffic_list_item), ReflectiveHolderFactory
						.reflectiveFactoryFor(TrafficViewHolder.class)));
	}
}
