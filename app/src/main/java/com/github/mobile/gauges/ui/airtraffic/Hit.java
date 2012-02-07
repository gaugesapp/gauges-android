package com.github.mobile.gauges.ui.airtraffic;

/**
 * Class to model a hit of traffic to a specific site
 */
public class Hit {

    final String title;

    final String siteId;

    final float lon;

    final float lat;

    final long time;

    final String city;

    final String region;

    final String country;

    /**
     * Create a hit for the given site
     *
     * @param siteId
     * @param title
     * @param lon
     * @param lat
     * @param time
     * @param city
     * @param region
     * @param country
     */
    public Hit(final String siteId, final String title, final float lon, final float lat, final long time,
            final String city, final String region, final String country) {
        this.siteId = siteId;
        this.title = title;
        this.lon = lon;
        this.lat = lat;
        this.time = time;
        this.city = city;
        this.region = region;
        this.country = country;
    }
}
