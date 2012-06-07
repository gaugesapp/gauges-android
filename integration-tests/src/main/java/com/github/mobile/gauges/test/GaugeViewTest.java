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

package com.github.mobile.gauges.test;

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.ui.GaugeViewActivity;

/**
 * Tests for displaying a specific {@link Gauge}
 */
public class GaugeViewTest extends ActivityInstrumentationTestCase2<GaugeViewActivity> {

    /**
     * Create test for {@link GaugeViewActivity}
     */
    public GaugeViewTest() {
        super(GaugeViewActivity.class);
    }

    /**
     * Configure intent used to display a {@link Gauge}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.putExtra(GAUGE, new Gauge().setTitle("a gauge").setId("123"));
        setActivityIntent(intent);
    }

    /**
     * Verify activity exists
     */
    public void testActivityExists() {
        assertNotNull(getActivity());
    }
}
