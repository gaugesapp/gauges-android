package com.github.mobile.gauges.ui;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.widget.ListAdapter;

import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.core.GaugesService;
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

	public Loader<List<Gauge>> onCreateLoader(int id, Bundle args) {
		return new AsyncLoader<List<Gauge>>(getActivity()) {

			public List<Gauge> loadInBackground() {
				GaugesService service = new GaugesService(null, null);
				try {
					return service.getGauges();
				} catch (IOException e) {
					return Collections.emptyList();
				}
			}
		};
	}

	protected ListAdapter adapterFor(List<Gauge> items) {
		return new ViewHoldingListAdapter<Gauge>(items,
				ViewInflator.viewInflatorFor(getActivity(),
						layout.gauge_list_item),
				ReflectiveHolderFactory
						.reflectiveFactoryFor(GaugeViewHolder.class));
	}
}
