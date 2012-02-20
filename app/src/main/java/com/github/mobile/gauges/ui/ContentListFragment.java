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
import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.core.PageContent;
import com.google.inject.Inject;
import com.madgag.android.listviews.ReflectiveHolderFactory;
import com.madgag.android.listviews.ViewInflator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Fragment to load page content information for a {@link Gauge}
 */
public class ContentListFragment extends ListLoadingFragment<PageContent> {

    private static final String TAG = "CLA";

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
            listView.addHeaderView(getActivity().getLayoutInflater().inflate(layout.content_list_item_labels, null),
                    null, false);
    }

    @Override
    public void onDestroyView() {
        setListAdapter(null);
        super.onDestroyView();
    }

    @Override
    public Loader<List<PageContent>> onCreateLoader(int id, Bundle args) {
        return new AsyncLoader<List<PageContent>>(getActivity()) {

            public List<PageContent> loadInBackground() {
                try {
                    return serviceProvider.getService().getContent(getArguments().getString(GAUGE_ID));
                } catch (IOException e) {
                    Log.d(TAG, "Exception getting page content", e);
                    showError(string.error_loading_contents);
                } catch (AccountsException e) {
                    Log.d(TAG, "Exception getting page content", e);
                    showError(string.error_loading_contents);
                }
                return Collections.emptyList();
            }
        };
    }

    @Override
    protected ListAdapter adapterFor(List<PageContent> items) {
        return new AlternatingColorListAdapter<PageContent>(getActivity().getResources(), items,
                ViewInflator.viewInflatorFor(getActivity(), layout.content_list_item),
                ReflectiveHolderFactory.reflectiveFactoryFor(ContentViewHolder.class));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String url = ((PageContent) l.getItemAtPosition(position)).getUrl();
        startActivity(new Intent(ACTION_VIEW, Uri.parse(url)));
    }
}
