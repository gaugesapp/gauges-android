package com.github.mobile.gauges;

import com.github.mobile.gauges.core.Gauge;

import android.content.Intent;
import android.os.Bundle;

/**
 * Constants used for {@link Intent} and {@link Bundle} properties
 */
public interface IntentConstants {

    /**
     * Reference to a {@link Gauge}
     */
    String GAUGE = "gauge";

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
}
