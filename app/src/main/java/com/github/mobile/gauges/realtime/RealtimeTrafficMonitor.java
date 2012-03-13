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

package com.github.mobile.gauges.realtime;


import static java.lang.Thread.sleep;
import static java.util.Collections.emptyList;
import android.util.Log;

import com.github.mobile.gauges.core.DatedViewSummary;
import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.core.GaugesService;
import com.google.inject.Provider;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activities and Fragments should not retain references to
 * the traffic monitor, as fresh instances will be created
 * when user credentials change.
 */
class RealtimeTrafficMonitor implements Runnable {

    public static final String TAG = "RTM";

    private static final String CHANNEL_PREFIX = "private-";

    private final Provider<GaugesService> apiService;
    private final RealtimeTrafficService realtimeTrafficService;
    private final GaugesPusher pusher;
    private final Map<String, Gauge> idGauges = new HashMap<String, Gauge>();
    private final TrafficPusherCallback callback = new TrafficPusherCallback() {
        protected void onHit(final Hit hit) {
            realtimeTrafficService.broadcast(hit, updatedGaugeFor(hit));
        }
    };

    private boolean cancelled = false;
    private List<Gauge> gauges = emptyList();

    public RealtimeTrafficMonitor(RealtimeTrafficService realtimeTrafficService, Provider<GaugesService> apiService) {
        this.apiService = apiService;
        this.realtimeTrafficService = realtimeTrafficService;
        pusher = new GaugesPusher(apiService);
    }

    void cancel() {
        Log.d(TAG,"monitor cancel() called");
        cancelled = true;
    }

    public void run() {
        while (!cancelled) {
            try {
                gauges = apiService.get().getGauges();
            } catch (IOException e) {
                Log.e(TAG, "Problem getting gauges ", e);
            }

            Log.d(TAG,"current gauge count = "+gauges.size());
            for (Gauge gauge : gauges) {
                if (idGauges.put(gauge.getId(), gauge) == null) {
                    Log.d(TAG,"subscribing to new Gauge "+gauge.getId());
                    pusher.subscribe(CHANNEL_PREFIX + gauge.getId()).bind("hit", callback);
                }
            }

            try { sleep(120*1000L); } catch (InterruptedException e) { }
        }

        Log.d(TAG,"cancelled, unsubscribing");
        for (String gaugeId : idGauges.keySet()) {
            pusher.unsubscribe(CHANNEL_PREFIX + gaugeId);
        }
        pusher.disconnect();
    }

    private Gauge updatedGaugeFor(Hit hit) {
        Gauge gauge = idGauges.get(hit.siteId);
        if (gauge != null) {
            DatedViewSummary today = gauge.getToday();
            if (today != null) {
                today.setViews(today.getViews() + 1);
            }
        }
        return gauge;
    }

    public List<Gauge> getGauges() {
        return gauges;
    }

}
