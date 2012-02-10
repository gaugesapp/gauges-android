package com.github.mobile.gauges.ui.airtraffic;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.util.DisplayMetrics.DENSITY_DEFAULT;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.github.mobile.gauges.R.drawable;
import com.github.mobile.gauges.core.Gauge;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that maps pins and rings to a specific gauge to be used in the air traffic view
 */
public class AirTrafficResourceProvider {

    private final Bitmap[] pins = new Bitmap[10];

    private final Bitmap[] rings = new Bitmap[10];

    private final int pinHeight;

    private final int pinWidth;

    private final int ringHeight;

    private final int ringWidth;

    private int pinIndex = 0;

    private final Map<String, Integer> gaugeColors = new LinkedHashMap<String, Integer>();

    /**
     * Create resource provider
     *
     * @param resources
     */
    public AirTrafficResourceProvider(final Resources resources) {
        Options options = new Options();
        options.inPreferQualityOverSpeed = true;
        options.inDither = true;
        options.inPreferredConfig = ARGB_8888;

        // Load all the pin images
        pins[0] = BitmapFactory.decodeResource(resources, drawable.pin0, options);
        pins[1] = BitmapFactory.decodeResource(resources, drawable.pin1, options);
        pins[2] = BitmapFactory.decodeResource(resources, drawable.pin2, options);
        pins[3] = BitmapFactory.decodeResource(resources, drawable.pin3, options);
        pins[4] = BitmapFactory.decodeResource(resources, drawable.pin4, options);
        pins[5] = BitmapFactory.decodeResource(resources, drawable.pin5, options);
        pins[6] = BitmapFactory.decodeResource(resources, drawable.pin6, options);
        pins[7] = BitmapFactory.decodeResource(resources, drawable.pin7, options);
        pins[8] = BitmapFactory.decodeResource(resources, drawable.pin8, options);
        pins[9] = BitmapFactory.decodeResource(resources, drawable.pin9, options);

        float pinScale = (float) pins[0].getDensity() / DENSITY_DEFAULT;
        pinHeight = Math.round(pins[0].getHeight() / pinScale);
        pinWidth = Math.round(pins[0].getWidth() / pinScale);

        // Load all the ring images
        rings[0] = BitmapFactory.decodeResource(resources, drawable.ring0, options);
        rings[1] = BitmapFactory.decodeResource(resources, drawable.ring1, options);
        rings[2] = BitmapFactory.decodeResource(resources, drawable.ring2, options);
        rings[3] = BitmapFactory.decodeResource(resources, drawable.ring3, options);
        rings[4] = BitmapFactory.decodeResource(resources, drawable.ring4, options);
        rings[5] = BitmapFactory.decodeResource(resources, drawable.ring5, options);
        rings[6] = BitmapFactory.decodeResource(resources, drawable.ring6, options);
        rings[7] = BitmapFactory.decodeResource(resources, drawable.ring7, options);
        rings[8] = BitmapFactory.decodeResource(resources, drawable.ring8, options);
        rings[9] = BitmapFactory.decodeResource(resources, drawable.ring9, options);

        float ringScale = (float) rings[0].getDensity() / DENSITY_DEFAULT;
        ringHeight = Math.round(rings[0].getHeight() / ringScale);
        ringWidth = Math.round(rings[0].getWidth() / ringScale);
    }

    /**
     * Set gauges to be styled
     *
     * @param gauges
     * @return this provider
     */
    public AirTrafficResourceProvider setGauges(final List<Gauge> gauges) {
        gaugeColors.clear();
        pinIndex = 0;
        for (Gauge gauge : gauges) {
            gaugeColors.put(gauge.getId(), pinIndex++);
            if (pinIndex == pins.length)
                pinIndex = 0;
        }
        return this;
    }

    /**
     * Get key for gauge id
     *
     * @param gaugeId
     * @return key, -1 if gauge id has no key
     */
    public int getKey(String gaugeId) {
        Integer key = gaugeColors.get(gaugeId);
        return key != null ? key.intValue() : -1;
    }

    /**
     * Get pin to draw for key
     *
     * @param key
     * @return pin bitmap
     */
    public Bitmap getPin(final int key) {
        return pins[key];
    }

    /**
     * Get pin to draw for gauge with given id
     *
     * @param gaugeId
     * @return bitmap, may be null
     */
    public Bitmap getPin(String gaugeId) {
        int key = getKey(gaugeId);
        return key != -1 ? getPin(key) : null;
    }

    /**
     * Get ring to draw for key
     *
     * @param key
     * @return ring bitmap, may be null
     */
    public Bitmap getRing(final int key) {
        return rings[key];
    }

    /**
     * @return pinHeight
     */
    public int getPinHeight() {
        return pinHeight;
    }

    /**
     * @return pinWidth
     */
    public int getPinWidth() {
        return pinWidth;
    }

    /**
     * @return ringHeight
     */
    public int getRingHeight() {
        return ringHeight;
    }

    /**
     * @return ringWidth
     */
    public int getRingWidth() {
        return ringWidth;
    }
}
