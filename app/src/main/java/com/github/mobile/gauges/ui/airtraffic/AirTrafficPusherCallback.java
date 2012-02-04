package com.github.mobile.gauges.ui.airtraffic;

import static java.lang.System.currentTimeMillis;

import com.emorym.android_pusher.PusherCallback;
import java.util.Queue;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Callback that delivers pushed air traffic events to a {@link Queue}
 */
public class AirTrafficPusherCallback extends PusherCallback {

    private static final String TAG = "ATPC";

    private final Queue<Hit> hits;

    /**
     * Create callback that pushes hits to given {@link Queue}
     *
     * @param hits
     */
    public AirTrafficPusherCallback(Queue<Hit> hits) {
        this.hits = hits;
    }

    @Override
    public void onEvent(JSONObject eventData) {
        try {
            float longitude = (float) eventData.getDouble("longitude");
            float latitude = (float) eventData.getDouble("latitude");
            String id = eventData.getString("site_id");
            hits.add(new Hit(id, longitude, latitude, currentTimeMillis()));
            if (hits.size() > 40)
                hits.poll();
        } catch (JSONException e) {
            Log.d(TAG, "JSON exception", e);
        }
    }
}
