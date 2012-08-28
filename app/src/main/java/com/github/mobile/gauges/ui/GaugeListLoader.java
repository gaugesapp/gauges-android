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

import android.accounts.OperationCanceledException;
import android.app.Activity;

import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.core.Gauge;
import com.google.inject.Inject;

import java.util.List;

/**
 * Class to load a list of gauges
 */
public class GaugeListLoader extends ThrowableLoader<List<Gauge>> {

    private final Activity activity;

    private final GaugesServiceProvider serviceProvider;

    private final List<Gauge> gauges;

    /**
     * Creates a gauge list loader using the given {@link Activity} and
     * {@link GaugesServiceProvider}
     *
     * @param activity
     * @param gauges
     * @param serviceProvider
     */
    @Inject
    public GaugeListLoader(final Activity activity, final List<Gauge> gauges,
            final GaugesServiceProvider serviceProvider) {
        super(activity, gauges);

        this.gauges = gauges;
        this.activity = activity;
        this.serviceProvider = serviceProvider;
    }

    @Override
    public List<Gauge> loadData() throws Exception {
        try {
            return serviceProvider.getService().getGauges();
        } catch (OperationCanceledException e) {
            activity.finish();
            return gauges;
        }
    }
}
