package com.github.mobile.gauges.ui;

import static android.content.Intent.ACTION_VIEW;
import static com.github.mobile.gauges.IntentConstants.GAUGE_ID;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.authenticator.ApiKeyProvider;
import com.github.mobile.gauges.core.GaugesService;
import com.github.mobile.gauges.core.Referrer;
import com.google.inject.Inject;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Fragment to display a list of {@link Referrer} instances
 */
public class ReferrerListFragment extends ListLoadingFragment<Referrer> {

	private static final String TAG = "RLF";

    @Inject ApiKeyProvider apiKeyProvider;

	/**
	 * Create referrer list fragment
	 */
	public ReferrerListFragment() {
	}

	public Loader<List<Referrer>> onCreateLoader(int id, Bundle args) {
		return new AsyncLoader<List<Referrer>>(getActivity()) {

			public List<Referrer> loadInBackground() {
				GaugesService service = new GaugesService(apiKeyProvider.getAuthKey());
				try {
					return service.getReferrers(getArguments().getString(
							GAUGE_ID));
				} catch (IOException e) {
					Log.d(TAG, "Exception getting referrers", e);
					return Collections.emptyList();
				}
			}
		};
	}

	protected ListAdapter adapterFor(List<Referrer> items) {
		return new ViewHoldingListAdapter<Referrer>(items,
				ViewInflator.viewInflatorFor(getActivity(),
						layout.referrer_list_item),
				ReflectiveHolderFactory
						.reflectiveFactoryFor(ReferrerViewHolder.class));
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		String url = ((Referrer) l.getItemAtPosition(position)).getUrl();
		startActivity(new Intent(ACTION_VIEW, Uri.parse(url)));
	}
}
