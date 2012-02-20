/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mobile.gauges.ui;

import static android.content.Intent.ACTION_VIEW;
import static com.github.mobile.gauges.IntentConstants.GAUGE_ID;
import android.accounts.AccountsException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.R.string;
import com.github.mobile.gauges.core.Referrer;
import com.google.inject.Inject;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewInflator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Fragment to display a list of {@link Referrer} instances
 */
public class ReferrerListFragment extends ListLoadingFragment<Referrer> {

    private static final String TAG = "RLF";

    @Inject
    private GaugesServiceProvider serviceProvider;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = getListView();
        listView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);

        if (getListAdapter() == null)
            listView.addHeaderView(getActivity().getLayoutInflater().inflate(layout.referrer_list_item_labels, null),
                    null, false);
    }

    @Override
    public void onDestroyView() {
        setListAdapter(null);
        super.onDestroyView();
    }

    public Loader<List<Referrer>> onCreateLoader(int id, Bundle args) {
        return new AsyncLoader<List<Referrer>>(getActivity()) {

            public List<Referrer> loadInBackground() {
                try {
                    return serviceProvider.getService().getReferrers(getArguments().getString(GAUGE_ID));
                } catch (IOException e) {
                    Log.d(TAG, "Exception getting referrers", e);
                    showError(string.error_loading_referrers);
                } catch (AccountsException e) {
                    Log.d(TAG, "Exception getting referrers", e);
                    showError(string.error_loading_referrers);
                }
                return Collections.emptyList();
            }
        };
    }

    protected ListAdapter adapterFor(List<Referrer> items) {
        return new AlternatingColorListAdapter<Referrer>(getActivity().getResources(), items,
                ViewInflator.viewInflatorFor(getActivity(), layout.referrer_list_item),
                ReflectiveHolderFactory.reflectiveFactoryFor(ReferrerViewHolder.class));
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        String url = ((Referrer) l.getItemAtPosition(position)).getUrl();
        startActivity(new Intent(ACTION_VIEW, Uri.parse(url)));
    }
}
