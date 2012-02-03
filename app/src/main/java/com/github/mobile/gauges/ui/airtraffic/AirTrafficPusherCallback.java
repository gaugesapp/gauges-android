package com.github.mobile.gauges.ui.airtraffic;

import static java.lang.System.currentTimeMillis;

import com.emorym.android_pusher.PusherCallback;
import java.util.Queue;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class AirTrafficPusherCallback extends PusherCallback {
    private final Queue<Hit> hits;

    public AirTrafficPusherCallback(Queue<Hit> hits) {
        this.hits = hits;
    }

    @Override
    public void onEvent(JSONObject eventData) {
        Log.d("ATPC", "event :" + eventData.toString());
        try {
            float longitude = (float) eventData.getDouble("longitude");
            float latitude = (float) eventData.getDouble("latitude");
            hits.add(new Hit(longitude, latitude, currentTimeMillis()));
            if (hits.size() > 40) {
                hits.poll();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
