package com.github.mobile.gauges.ui.airtraffic;

/**
 * Class to model a hit of traffic to a specific site
 */
public class Hit {

    final String siteId;

    final float lon;

    final float lat;

    final long time;

    /**
     * Create a hit for the given site
     *
     * @param siteId
     * @param lon
     * @param lat
     * @param time
     */
    public Hit(final String siteId, final float lon, final float lat, final long time) {
        this.siteId = siteId;
        this.lon = lon;
        this.lat = lat;
        this.time = time;
    }
}
