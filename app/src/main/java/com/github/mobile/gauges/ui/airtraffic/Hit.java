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

/**
 * Class to model a hit of traffic to a specific site
 */
public class Hit {

    final String title;

    final String siteId;

    final float lon;

    final float lat;

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
     * @param city
     * @param region
     * @param country
     */
    public Hit(final String siteId, final String title, final float lon, final float lat, final String city,
            final String region, final String country) {
        this.siteId = siteId;
        this.title = title;
        this.lon = lon;
        this.lat = lat;
        this.city = city;
        this.region = region;
        this.country = country;
    }
}
