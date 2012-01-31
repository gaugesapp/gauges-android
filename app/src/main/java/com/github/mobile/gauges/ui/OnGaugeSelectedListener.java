package com.github.mobile.gauges.ui;

import com.github.mobile.gauges.core.Gauge;

/**
 * Interface definition for a callback to be invoked when a {@link Gauge} has been selected.
 */
public interface OnGaugeSelectedListener {

    /**
     * Callback method to be invoked when a {@link Gauge} is selected
     *
     * @param gauge
     */
    void onGaugeSelected(Gauge gauge);
}
