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

package com.github.mobile.gauges;

import android.content.Intent;
import android.os.Bundle;

import com.github.mobile.gauges.core.Gauge;

import java.util.Collection;

/**
 * Constants used for {@link Intent} and {@link Bundle} properties
 */
public interface IntentConstants {

    /**
     * Reference to a {@link Gauge}
     */
    String GAUGE = "gauge";

    /**
     * Reference to a {@link Collection} of {@link Gauge} items
     */
    String GAUGES = "gauges";

    /**
     * Gauge id
     */
    String GAUGE_ID = "gaugeId";

    /**
     * Action prefix for all intents created
     */
    String INTENT_PREFIX = "com.github.mobile.gauges.";

    /**
     * View Gauge action
     */
    String VIEW_GAUGE = INTENT_PREFIX + "gauge.VIEW";

    /**
     * View Air Traffic action
     */
    String VIEW_AIR_TRAFFIC = INTENT_PREFIX + "airtraffic.VIEW";
}
