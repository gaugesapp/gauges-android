package com.github.mobile.gauges.ui.airtraffic;

import static android.graphics.Bitmap.createScaledBitmap;
import static com.nineoldandroids.animation.ValueAnimator.INFINITE;
import static java.lang.Math.PI;
import static java.lang.System.currentTimeMillis;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.emorym.android_pusher.Pusher;
import com.github.mobile.gauges.R.drawable;
import com.github.mobile.gauges.core.Gauge;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * View to display an Air Traffic map
 */
public class AirTrafficView extends View {

    private static final String PUSHER_APP_KEY = "887bd32ce6b7c2049e0b";

    /**
     * Unscaled width of map image used
     */
    private static final double MAP_WIDTH = 960.0;

    /**
     * Unscaled height of map image used
     */
    private static final double MAP_HEIGHT = 596.0;

    /**
     * Divisor used to compute the scaling value
     * <p>
     * Constant taken from gaug.es site
     */
    private static final double SCALE_DIVISOR = 720.0;

    /**
     * Multiplier used to compute the scaling value
     * <p>
     * Constant taken from gaug.es site
     */
    private static final double SCALE_MULTIPLIER = 0.169;

    /**
     * Relative width used for x/y correction and scale value
     */
    private static final double RELATIVE_WIDTH = MAP_WIDTH / SCALE_DIVISOR;

    /**
     * Scale value used based on map image dimensions
     */
    private static final double SCALE = RELATIVE_WIDTH * SCALE_MULTIPLIER;

    /**
     * Correction value used to adjust scaled y position
     */
    private static final double Y_CORRECTOR = 65 * RELATIVE_WIDTH;

    /**
     * Correction value used to adjust scaled x position
     */
    private static final double X_CORRECTOR = RELATIVE_WIDTH;

    /**
     * Constant taken from gaug.es site
     */
    private static final double PIXELS_PER_LONGITUDE_DEGREE = 16.0 / 360.0;

    /**
     * Constant taken from gaug.es site
     */
    private static final double NEGATIVE_PIXELS_PER_LONGITUDE_RADIAN = -(16.0 / (2.0 * PI));

    /**
     * Constant taken from gaug.es site
     */
    private static final double BITMAP_ORIGIN = 16.0 / 2.0;

    private ConcurrentLinkedQueue<Hit> hits = new ConcurrentLinkedQueue<Hit>();

    private ValueAnimator animator = ValueAnimator.ofFloat(0, 1);

    private final Bitmap[] pins = new Bitmap[10];

    private final Bitmap[] rings = new Bitmap[10];

    private int pinIndex = 0;

    private int pinHeight;

    private int pinWidth;

    private int innerRingHeight;

    private int innerRingWidth;

    private int outerRingHeight;

    private int outerRingWidth;

    private final Map<String, Integer> gaugeColors = new LinkedHashMap<String, Integer>();

    /**
     * Used to track elapsed time for animations and fps
     */
    private long startTime;

    /**
     * Used to track frames per second
     */
    private int frames = 0;

    /**
     * Frames per second
     */
    private float fps = 0; // frames per second

    private Pusher pusher;
    private double xMapScale;
    private double yMapScale;
    private Bitmap map, fittedMap;
    private Paint mapPaint;
    private List<Gauge> gauges = Collections.emptyList();

    /**
     * Constructor. Create objects used throughout the life of the View: the Paint and the animator
     *
     * @param context
     * @param attrs
     */
    public AirTrafficView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pusher = new Pusher(PUSHER_APP_KEY);
        final Resources resources = getResources();
        map = BitmapFactory.decodeResource(resources, drawable.map);

        // Load all the pin images
        pins[0] = BitmapFactory.decodeResource(resources, drawable.pin0);
        pins[1] = BitmapFactory.decodeResource(resources, drawable.pin1);
        pins[2] = BitmapFactory.decodeResource(resources, drawable.pin2);
        pins[3] = BitmapFactory.decodeResource(resources, drawable.pin3);
        pins[4] = BitmapFactory.decodeResource(resources, drawable.pin4);
        pins[5] = BitmapFactory.decodeResource(resources, drawable.pin5);
        pins[6] = BitmapFactory.decodeResource(resources, drawable.pin6);
        pins[7] = BitmapFactory.decodeResource(resources, drawable.pin7);
        pins[8] = BitmapFactory.decodeResource(resources, drawable.pin8);
        pins[9] = BitmapFactory.decodeResource(resources, drawable.pin9);

        pinHeight = pins[0].getHeight() / 2;
        pinWidth = pins[0].getWidth() / 2;

        // Load all the ring images
        rings[0] = BitmapFactory.decodeResource(resources, drawable.ring0);
        rings[1] = BitmapFactory.decodeResource(resources, drawable.ring1);
        rings[2] = BitmapFactory.decodeResource(resources, drawable.ring2);
        rings[3] = BitmapFactory.decodeResource(resources, drawable.ring3);
        rings[4] = BitmapFactory.decodeResource(resources, drawable.ring4);
        rings[5] = BitmapFactory.decodeResource(resources, drawable.ring5);
        rings[6] = BitmapFactory.decodeResource(resources, drawable.ring6);
        rings[7] = BitmapFactory.decodeResource(resources, drawable.ring7);
        rings[8] = BitmapFactory.decodeResource(resources, drawable.ring8);
        rings[9] = BitmapFactory.decodeResource(resources, drawable.ring9);

        outerRingHeight = rings[0].getHeight() * 2 / 3;
        outerRingWidth = rings[0].getWidth() * 2 / 3;

        innerRingHeight = outerRingHeight / 2;
        innerRingWidth = outerRingWidth / 2;

        mapPaint = new Paint();
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                invalidate(); // Force a redraw
            }
        });
        animator.setRepeatCount(INFINITE);
        animator.setDuration(3000);
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldw, final int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        xMapScale = (double) width / MAP_WIDTH;
        yMapScale = (double) height / MAP_HEIGHT;

        fittedMap = createScaledBitmap(map, width, height, true);

        hits.clear();
        // Cancel animator in case it was already running
        animator.cancel();

        resetFramesPerSecondCalc();

        animator.start();
    }

    /**
     * Set the list of {@link Gauge} items to display
     *
     * @param gauges
     */
    public void setGauges(List<Gauge> gauges) {
        unsubscribeFromGaugeChannels();
        this.gauges = gauges;
        subscribeToGaugeChannels();
        generatePinColors();
    }

    private void generatePinColors() {
        gaugeColors.clear();
        for (Gauge gauge : gauges) {
            gaugeColors.put(gauge.getId(), pinIndex++);
            if (pinIndex == pins.length)
                pinIndex = 0;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        if (fittedMap != null)
            canvas.drawBitmap(fittedMap, 0, 0, mapPaint);

        long now = currentTimeMillis();
        for (Hit hit : hits)
            draw(hit, canvas, now);

        canvas.restore();

        calcFramesPerSecond();
    }

    private void draw(Hit hit, Canvas canvas, long now) {
        // Find the color index for the given site id
        Integer colorIndex = gaugeColors.get(hit.siteId);
        if (colorIndex == null)
            return;

        // Determine the x and y positions to draw the hit at.
        // This code was taken from the gaug.es site
        double globalX = (BITMAP_ORIGIN + hit.lon * PIXELS_PER_LONGITUDE_DEGREE) * 256.0;
        double e = Math.sin(hit.lat * (PI / 180.0));
        e = Math.max(Math.min(e, 0.9999), -0.9999);
        double globalY = (BITMAP_ORIGIN + 0.5 * Math.log((1.0 + e) / (1.0 - e)) * NEGATIVE_PIXELS_PER_LONGITUDE_RADIAN) * 256.0;

        float x = (float) (Math.round(globalX * SCALE) - X_CORRECTOR);
        float y = (float) (Math.round(globalY * SCALE) - Y_CORRECTOR);

        // Take absolute positions on actual map and scale to actual screen size since map image may have been scaled
        x *= xMapScale;
        y *= yMapScale;

        Bitmap pin = pins[colorIndex.intValue()];
        RectF rect = new RectF();
        rect.top = y - pinHeight / 2;
        rect.left = x - pinWidth / 2;
        rect.right = rect.left + pinWidth;
        rect.bottom = rect.top + pinHeight;
        canvas.drawBitmap(pin, null, rect, mapPaint);

        // Draw rings if the hit just occurred
        if (now - hit.time < 250) {
            Bitmap ring = rings[colorIndex.intValue()];
            rect.top = y - innerRingHeight / 2;
            rect.left = x - innerRingWidth / 2;
            rect.right = rect.left + innerRingWidth;
            rect.bottom = rect.top + innerRingHeight;
            canvas.drawBitmap(ring, null, rect, mapPaint);
        } else if (now - hit.time < 500) {
            Bitmap ring = rings[colorIndex.intValue()];
            rect.top = y - outerRingHeight / 2;
            rect.left = x - outerRingWidth / 2;
            rect.right = rect.left + outerRingWidth;
            rect.bottom = rect.top + outerRingHeight;
            canvas.drawBitmap(ring, null, rect, mapPaint);
        }
    }

    private void resetFramesPerSecondCalc() {
        // Set up fps tracking and start the animation
        startTime = currentTimeMillis();
        frames = 0;
    }

    private void calcFramesPerSecond() {
        // fps counter: count how many frames we draw and once a second calculate the
        // frames per second
        ++frames;
        long nowTime = currentTimeMillis();
        long deltaTime = nowTime - startTime;
        if (deltaTime > 1000) {
            float secs = (float) deltaTime / 1000f;
            fps = (float) frames / secs;
            Log.d("ATV", "fps = " + fps);
            startTime = nowTime;
            frames = 0;
        }
    }

    /**
     * Pause the animated view
     */
    public void pause() {
        unsubscribeFromGaugeChannels();
        // Make sure the animator's not spinning in the background when the activity is paused.
        animator.cancel();
    }

    /**
     * Resume the animated view
     */
    public void resume() {
        subscribeToGaugeChannels();
        animator.start();
    }

    private void subscribeToGaugeChannels() {
        AirTrafficPusherCallback callback = new AirTrafficPusherCallback(hits);

        for (Gauge gauge : gauges)
            pusher.subscribe(gauge.getId()).bind("hit", callback);
    }

    private void unsubscribeFromGaugeChannels() {
        for (Gauge gauge : gauges)
            pusher.unsubscribe(gauge.getId());
    }
}
