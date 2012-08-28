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
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.R.string;
import com.github.mobile.gauges.core.Referrer;
import com.google.inject.Inject;

import java.util.List;

/**
 * Fragment to display a list of {@link Referrer} instances
 */
public class ReferrerListFragment extends ItemListFragment<Referrer> {

    @Inject
    private GaugesServiceProvider serviceProvider;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_referrers);
    }

    @Override
    protected void configureList(Activity activity, ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);

        getListAdapter().addHeader(
                activity.getLayoutInflater().inflate(
                        layout.referrer_list_item_labels, null));
    }

    @Override
    public void onDestroyView() {
        setListAdapter(null);

        super.onDestroyView();
    }

    public Loader<List<Referrer>> onCreateLoader(int id, Bundle args) {
        final List<Referrer> initialItems = items;
        return new ThrowableLoader<List<Referrer>>(getActivity(), items) {

            @Override
            public List<Referrer> loadData() throws Exception {
                try {
                    return serviceProvider.getService().getReferrers(
                            getArguments().getString(GAUGE_ID));
                } catch (OperationCanceledException e) {
                    Activity activity = getActivity();
                    if (activity != null)
                        activity.finish();
                    return initialItems;
                }
            }
        };
    }

    @Override
    protected SingleTypeAdapter<Referrer> createAdapter(List<Referrer> items) {
        return new ReferrerListAdapter(getActivity().getLayoutInflater(), items);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        String url = ((Referrer) l.getItemAtPosition(position)).getUrl();
        startActivity(new Intent(ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return string.error_loading_referrers;
    }
}
