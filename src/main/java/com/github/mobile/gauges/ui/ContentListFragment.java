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
import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.core.GaugesService;
import com.github.mobile.gauges.core.PageContent;
import com.google.inject.Inject;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Fragment to load page content information for a {@link Gauge}
 */
public class ContentListFragment extends ListLoadingFragment<PageContent> {

	private static final String TAG = "CLA";

    @Inject ApiKeyProvider apiKeyProvider;

	/**
	 * Create content list fragment
	 */
	public ContentListFragment() {
	}

	public Loader<List<PageContent>> onCreateLoader(int id, Bundle args) {
		return new AsyncLoader<List<PageContent>>(getActivity()) {

			public List<PageContent> loadInBackground() {
				GaugesService service = new GaugesService(apiKeyProvider.getAuthKey());
				try {
					return service.getContent(getArguments()
							.getString(GAUGE_ID));
				} catch (IOException e) {
					Log.d(TAG, "Exception getting page content", e);
					return Collections.emptyList();
				}
			}
		};
	}

	protected ListAdapter adapterFor(List<PageContent> items) {
		return new ViewHoldingListAdapter<PageContent>(items,
				ViewInflator.viewInflatorFor(getActivity(),
						layout.content_list_item),
				ReflectiveHolderFactory
						.reflectiveFactoryFor(ContentViewHolder.class));
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		String url = ((PageContent) l.getItemAtPosition(position)).getUrl();
		startActivity(new Intent(ACTION_VIEW, Uri.parse(url)));
	}
}
