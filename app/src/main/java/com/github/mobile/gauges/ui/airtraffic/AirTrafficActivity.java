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

package com.github.mobile.gauges.ui.airtraffic;

import static android.os.Build.VERSION.SDK_INT;
import static android.view.View.STATUS_BAR_HIDDEN;
import static com.github.mobile.gauges.IntentConstants.GAUGES;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.ImageView;
import android.widget.TextView;

import com.emorym.android_pusher.Pusher;
import com.github.kevinsawicki.wishlist.Toaster;
import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.R.string;
import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.ui.GaugeListLoader;
import com.github.mobile.gauges.ui.ThrowableLoader;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import roboguice.inject.InjectView;

/**
 * Activity to display list of gauge summaries
 */
public class AirTrafficActivity extends RoboSherlockFragmentActivity implements
        LoaderCallbacks<List<Gauge>> {

    private static final String CHANNEL_PREFIX = "private-";

    @Inject
    private GaugesServiceProvider serviceProvider;

    @InjectView(id.air_traffic)
    private AirTrafficView airTrafficView;

    @InjectView(id.iv_pin)
    private ImageView pinImage;

    @InjectView(id.tv_gauge_name)
    private TextView gaugeText;

    @InjectView(id.tv_gauge_location)
    private TextView gaugeLocation;

    private Map<String, String> gaugeTitles = new HashMap<String, String>();

    private final Executor backgroundThread = Executors.newFixedThreadPool(1);

    private Pusher pusher;

    private AirTrafficResourceProvider resourceProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(layout.airtraffic_activity);

        pusher = new GaugesPusher(serviceProvider);

        resourceProvider = new AirTrafficResourceProvider(getResources());

        airTrafficView.setResourceProvider(resourceProvider).setLabelHeight(
                gaugeText.getTextSize() * 3 / 2);

        getSupportActionBar().hide();

        updateSystemUi();

        @SuppressWarnings("unchecked")
        Collection<Gauge> intentGauges = (Collection<Gauge>) getIntent()
                .getSerializableExtra(GAUGES);
        if (intentGauges != null && !intentGauges.isEmpty())
            loadGauges(intentGauges);
        else
            getSupportLoaderManager().initLoader(0, null, this);
    }

    @SuppressWarnings("deprecation")
    private void updateSystemUi() {
        if (SDK_INT >= 14)
            // On ICS this equivalent to SYSTEM_UI_FLAG_LOW_PROFILE - the
            // dimmed-menu-buttons mode
            getWindow().getDecorView().setSystemUiVisibility(STATUS_BAR_HIDDEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unsubscribeFromGaugeChannels(gaugeTitles.keySet());
        airTrafficView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        airTrafficView.resume();
        subscribeToGaugeChannels(gaugeTitles.keySet());
    }

    @Override
    public Loader<List<Gauge>> onCreateLoader(int id, Bundle args) {
        return new GaugeListLoader(this, Collections.<Gauge> emptyList(),
                serviceProvider);
    }

    @Override
    public void onLoadFinished(Loader<List<Gauge>> listLoader,
            List<Gauge> gauges) {
        if (listLoader instanceof ThrowableLoader) {
            Exception exception = ((ThrowableLoader<?>) listLoader)
                    .clearException();
            if (exception != null)
                Toaster.showLong(this, string.error_loading_gauges);
        }

        unsubscribeFromGaugeChannels(gaugeTitles.keySet());
        loadGauges(gauges);
        subscribeToGaugeChannels(gaugeTitles.keySet());
    }

    private void loadGauges(Collection<Gauge> gauges) {
        Map<String, String> newGauges = new HashMap<String, String>();
        for (Gauge gauge : gauges)
            newGauges.put(gauge.getId(), gauge.getTitle());
        gaugeTitles = newGauges;
        resourceProvider.setGauges(gauges);
    }

    /**
     * Get geographic location label for hit
     *
     * @param hit
     * @return label
     */
    protected String getLocation(final Hit hit) {
        String country = hit.country != null ? hit.country : "";
        if (country.length() == 0)
            return "";

        String city = hit.city != null ? hit.city : "";
        String region = hit.region != null ? hit.region : "";

        String location;
        if (region.length() > 0
                && ("United States".equals(country) || "Canada".equals(country)))
            location = region;
        else
            location = country;

        if (city.length() > 0)
            return city + ", " + location;
        else
            return location;
    }

    /**
     * Get page title of hit
     *
     * @param hit
     * @return title
     */
    protected String getTitle(final Hit hit) {
        if (hit.title != null && hit.title.length() > 0) {
            int separator = hit.title.indexOf(" // ");
            if (separator == -1)
                separator = hit.title.indexOf(" - ");
            if (separator > 0)
                return hit.title.substring(0, separator);
            else
                return hit.title;
        } else
            return "";
    }

    @Override
    public void onLoaderReset(Loader<List<Gauge>> listLoader) {
    }

    private void subscribeToGaugeChannels(final Collection<String> subscribeIds) {
        if (subscribeIds.isEmpty())
            return;
        backgroundThread.execute(new Runnable() {

            public void run() {
                final AirTrafficPusherCallback callback = new AirTrafficPusherCallback() {

                    @Override
                    protected void onHit(final Hit hit) {
                        airTrafficView.addHit(hit);

                        gaugeText.post(new Runnable() {

                            @SuppressWarnings("deprecation")
                            public void run() {
                                String gaugeTitle = gaugeTitles.get(hit.siteId);
                                if (gaugeTitle == null)
                                    return;

                                Bitmap bitmap = resourceProvider
                                        .getPin(hit.siteId);
                                if (bitmap != null)
                                    pinImage.setBackgroundDrawable(new BitmapDrawable(
                                            getResources(), bitmap));
                                else
                                    pinImage.setBackgroundDrawable(null);

                                gaugeLocation.setText(getLocation(hit));

                                String title = getTitle(hit);
                                if (title.length() > 0)
                                    gaugeText
                                            .setText(gaugeTitle + ": " + title);
                                else
                                    gaugeText.setText(gaugeTitle);

                            }
                        });
                    }
                };
                for (String gaugeId : subscribeIds)
                    pusher.subscribe(CHANNEL_PREFIX + gaugeId).bind("hit",
                            callback);
            }
        });
    }

    private void unsubscribeFromGaugeChannels(
            final Collection<String> unsubscribeIds) {
        if (unsubscribeIds.isEmpty())
            return;
        backgroundThread.execute(new Runnable() {

            public void run() {
                for (String gaugeId : unsubscribeIds)
                    pusher.unsubscribe(CHANNEL_PREFIX + gaugeId);
            }
        });
    }
}
