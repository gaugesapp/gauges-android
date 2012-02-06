package com.github.mobile.gauges.ui.airtraffic;

import static android.graphics.Bitmap.createScaledBitmap;
import static android.graphics.Color.BLACK;
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

import com.github.mobile.gauges.R.color;
import com.github.mobile.gauges.R.drawable;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import java.util.Collection;
import java.util.Collections;

/**
 * View to display an Air Traffic map
 */
public class AirTrafficView extends View {

    private static final String MAP_LABEL = "AirTraffic";

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

    private ValueAnimator animator = ValueAnimator.ofFloat(0, 1);

    private AirTrafficResourceProvider resourceProvider;

    /**
     * Scale value used based on map image dimensions
     */
    private double scale;

    /**
     * Correction value used to adjust scaled y position
     */
    private double yCorrector;

    /**
     * Correction value used to adjust scaled x position
     */
    private double xCorrector;

    private int pinHeight;

    private int pinWidth;

    private int innerRingHeight;

    private int innerRingWidth;

    private int outerRingHeight;

    private int outerRingWidth;

    /**
     * Used to track elapsed time for animations and fps
     */
    private long startTime;

    /**
     * Used to track frames per second
     */
    private int frames = 0;

    private float mapLabelWidth;

    /**
     * Frames per second
     */
    private float fps = 0; // frames per second

    private double xMapScale;
    private double yMapScale;
    private Bitmap map, fittedMap;
    private Paint mapPaint;

    private Collection<Hit> hits = Collections.emptyList();

    /**
     * Constructor. Create objects used throughout the life of the View: the Paint and the animator
     *
     * @param context
     * @param attrs
     */
    public AirTrafficView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final Resources resources = getResources();
        map = BitmapFactory.decodeResource(resources, drawable.map);

        mapPaint = new Paint();
        mapPaint.setColor(resources.getColor(color.text));
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                invalidate(); // Force a redraw
            }
        });
        animator.setRepeatCount(INFINITE);
        animator.setDuration(3000);
    }

    /**
     * Set the height to use when drawing text on the map
     *
     * @param height
     * @return this view
     */
    public AirTrafficView setLabelHeight(final float height) {
        mapPaint.setTextSize(height);
        mapLabelWidth = mapPaint.measureText(MAP_LABEL);
        return this;
    }

    /**
     * Set resource provider
     *
     * @param provider
     * @return this view
     */
    public AirTrafficView setResourceProvider(final AirTrafficResourceProvider provider) {
        this.resourceProvider = provider;

        pinHeight = provider.getPinHeight() / 2;
        pinWidth = provider.getPinWidth() / 2;

        outerRingHeight = provider.getRingHeight() * 2 / 3;
        outerRingWidth = provider.getRingWidth() * 2 / 3;

        innerRingHeight = outerRingHeight / 2;
        innerRingWidth = outerRingWidth / 2;

        return this;
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldw, final int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        xMapScale = (double) width / map.getWidth();
        yMapScale = (double) height / map.getHeight();

        double relativeWidth = map.getWidth() / SCALE_DIVISOR;
        scale = relativeWidth * SCALE_MULTIPLIER;

        xCorrector = relativeWidth;
        yCorrector = 65 * relativeWidth;

        fittedMap = createScaledBitmap(map, width, height, true);

        hits.clear();
        // Cancel animator in case it was already running
        animator.cancel();

        resetFramesPerSecondCalc();

        animator.start();
    }

    /**
     * Set the {@link Collection} to draw {@link Hit} items from
     *
     * @param hits
     * @return this view
     */
    public AirTrafficView setHits(final Collection<Hit> hits) {
        this.hits = hits;
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        if (fittedMap != null)
            canvas.drawBitmap(fittedMap, 0, 0, mapPaint);

        if (mapLabelWidth > 0) {
            mapPaint.setShadowLayer(5, 0, 0, BLACK);
            canvas.drawText(MAP_LABEL, fittedMap.getWidth() / 2 - mapLabelWidth / 2,
                    fittedMap.getHeight() - mapPaint.getTextSize(), mapPaint);
            mapPaint.clearShadowLayer();
        }

        long now = currentTimeMillis();
        for (Hit hit : hits)
            draw(hit, canvas, now);

        canvas.restore();

        calcFramesPerSecond();
    }

    private void draw(Hit hit, Canvas canvas, long now) {
        // Find the color index for the given site id
        int key = resourceProvider.getKey(hit.siteId);
        if (key == -1)
            return;

        // Determine the x and y positions to draw the hit at.
        // This code was taken from the gaug.es site
        double globalX = (BITMAP_ORIGIN + hit.lon * PIXELS_PER_LONGITUDE_DEGREE) * 256.0;
        double e = Math.sin(hit.lat * (PI / 180.0));
        e = Math.max(Math.min(e, 0.9999), -0.9999);
        double globalY = (BITMAP_ORIGIN + 0.5 * Math.log((1.0 + e) / (1.0 - e)) * NEGATIVE_PIXELS_PER_LONGITUDE_RADIAN) * 256.0;

        float x = (float) ((globalX * scale) - xCorrector);
        float y = (float) ((globalY * scale) - yCorrector);

        // Take absolute positions on actual map and scale to actual screen size since map image may have been scaled
        x *= xMapScale;
        y *= yMapScale;

        Bitmap pin = resourceProvider.getPin(key);
        RectF rect = new RectF();
        rect.top = y - pinHeight / 2;
        rect.left = x - pinWidth / 2;
        rect.right = rect.left + pinWidth;
        rect.bottom = rect.top + pinHeight;
        canvas.drawBitmap(pin, null, rect, mapPaint);

        // Draw rings if the hit just occurred
        if (now - hit.time < 250) {
            Bitmap ring = resourceProvider.getRing(key);
            rect.top = y - innerRingHeight / 2;
            rect.left = x - innerRingWidth / 2;
            rect.right = rect.left + innerRingWidth;
            rect.bottom = rect.top + innerRingHeight;
            canvas.drawBitmap(ring, null, rect, mapPaint);
        } else if (now - hit.time < 500) {
            Bitmap ring = resourceProvider.getRing(key);
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
        // Make sure the animator's not spinning in the background when the activity is paused.
        animator.cancel();
    }

    /**
     * Resume the animated view
     */
    public void resume() {
        animator.start();
    }
}
