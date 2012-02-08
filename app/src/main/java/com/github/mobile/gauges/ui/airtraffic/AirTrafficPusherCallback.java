package com.github.mobile.gauges.ui.airtraffic;

import static java.lang.System.currentTimeMillis;
import static org.json.JSONObject.NULL;
import android.util.Log;

import com.emorym.android_pusher.PusherCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Callback that delivers pushed air traffic events to an implemented {@link #onHit(Hit)} callback
 */
public abstract class AirTrafficPusherCallback extends PusherCallback {

    private static final String TAG = "ATPC";

    /**
     * Process hit
     *
     * @param hit
     */
    protected abstract void onHit(final Hit hit);

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
            String title = getString(eventData, "title");
            onHit(new Hit(id, title, longitude, latitude, currentTimeMillis(), city, region, country));
        } catch (JSONException e) {
            Log.d(TAG, "JSON exception", e);
        }
    }
}
