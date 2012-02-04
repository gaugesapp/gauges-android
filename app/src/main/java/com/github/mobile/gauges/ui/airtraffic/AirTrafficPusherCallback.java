package com.github.mobile.gauges.ui.airtraffic;

import static java.lang.System.currentTimeMillis;
import static org.json.JSONObject.NULL;
import android.util.Log;

import com.emorym.android_pusher.PusherCallback;

import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Callback that delivers pushed air traffic events to a {@link Queue}
 */
public class AirTrafficPusherCallback extends PusherCallback {

    private static final String TAG = "ATPC";

    private final Queue<Hit> hits;

    private final int maxSize;

    /**
     * Create callback that pushes hits to given {@link Queue}
     *
     * @param hits
     *            the {@link Queue} to add hits to
     * @param maxSize
     *            the maximum number of hits to retain in the given {@link Queue}
     */
    public AirTrafficPusherCallback(Queue<Hit> hits, final int maxSize) {
        this.hits = hits;
        this.maxSize = maxSize;
    }

    /**
     * Process hit
     *
     * @param hit
     */
    protected void onHit(final Hit hit) {
        hits.add(hit);
        if (hits.size() > maxSize)
            hits.poll();
    }

    /**
     * Get the value of the key as a {@link String}
     *
     * @param data
     * @param key
     * @return value, may be null
     * @throws JSONException
     */
    protected String getString(final JSONObject data, final String key) throws JSONException {
        final Object value = data.get(key);
        if (value != null && value != NULL)
            return value.toString();
        else
            return null;
    }

    @Override
    public void onEvent(final JSONObject eventData) {
        try {
            float longitude = (float) eventData.getDouble("longitude");
            float latitude = (float) eventData.getDouble("latitude");
            String id = getString(eventData, "site_id");
            String city = getString(eventData, "city");
            String region = getString(eventData, "region");
            String country = getString(eventData, "country");
            onHit(new Hit(id, longitude, latitude, currentTimeMillis(), city, region, country));
        } catch (JSONException e) {
            Log.d(TAG, "JSON exception", e);
        }
    }
}
