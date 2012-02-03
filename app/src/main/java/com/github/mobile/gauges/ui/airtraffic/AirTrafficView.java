package com.github.mobile.gauges.ui.airtraffic;

import static android.graphics.Bitmap.createScaledBitmap;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;

import com.emorym.android_pusher.Pusher;
import com.github.mobile.gauges.R;
import com.github.mobile.gauges.core.Gauge;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class AirTrafficView extends View {

    private ConcurrentLinkedQueue<Hit> hits = new ConcurrentLinkedQueue<Hit>();

    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
    long startTime, prevTime; // Used to track elapsed time for animations and fps
    int frames = 0;     // Used to track frames per second
    float fps = 0;      // frames per second
    Matrix m = new Matrix(); // Matrix used to translate/rotate during rendering

    public static final String PUSHER_APP_KEY = "887bd32ce6b7c2049e0b";

    private Pusher pusher;
    private float mCenterX, mCenterY;
    private float scale;
    private Bitmap map,fittedMap;
    private Paint mapPaint, dotPoint;
    private List<Gauge> gauges = Collections.emptyList();

    /**
     * Constructor. Create objects used throughout the life of the View: the Paint and
     * the animator
     */
    public AirTrafficView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pusher = new Pusher( PUSHER_APP_KEY );
        map = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        mapPaint = new Paint();
        dotPoint = new Paint();
        dotPoint.setColor(0x99ee0000);
        // This listener is where the action is for the flak animations. Every frame of the
        // animation, we calculate the elapsed time and update every flake's position and rotation
        // according to its speed.
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                long nowTime = currentTimeMillis();
                float secs = (float)(nowTime - prevTime) / 1000f;
                prevTime = nowTime;

                invalidate(); // Force a redraw
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(3000);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        mCenterX = width/2.0f;
        mCenterY = height/2.0f;
        scale = width/360.0f;
        fittedMap = createScaledBitmap(map, width, height, true);

        hits.clear();
        // Cancel animator in case it was already running
        animator.cancel();

        resetFramesPerSecondCalc();

        animator.start();
    }

    public void setGauges(List<Gauge> gauges) {
        unsubscribeFromGaugeChannels();
        this.gauges = gauges;
        subscribeToGaugeChannels();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        canvas.translate(mCenterX, mCenterY);
        if (fittedMap!=null) {
            canvas.drawBitmap(fittedMap, -fittedMap.getWidth()/2, -fittedMap.getHeight()/2, mapPaint);
        }

        long now = currentTimeMillis();
        for (Hit hit: hits) {
            draw(hit, canvas, now);
        }

        canvas.restore();

        calcFramesPerSecond();
    }

    private void draw(Hit hit, Canvas canvas, long now) {
        long age = now-hit.time;
        float radius = 7+((float) Math.pow(5 * (1000.0f / (age + 1000)), 2));

        int alpha = round((1 - (age / 60000.0f))* 0xee);
        int colour = 0x00ee0000 + (alpha<<24);
        dotPoint.setColor(colour);
        canvas.drawCircle(hit.lon * scale, -hit.lat * scale, radius, dotPoint);
    }

    private void resetFramesPerSecondCalc() {
        // Set up fps tracking and start the animation
        startTime = currentTimeMillis();
        prevTime = startTime;
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
            Log.d("ATV", "fps = "+fps);
            startTime = nowTime;
            frames = 0;
        }
    }

    public void pause() {
        unsubscribeFromGaugeChannels();
        // Make sure the animator's not spinning in the background when the activity is paused.
        animator.cancel();
    }


    public void resume() {
        subscribeToGaugeChannels();
        animator.start();
    }

    private void subscribeToGaugeChannels() {
        AirTrafficPusherCallback callback = new AirTrafficPusherCallback(hits);

        for (Gauge gauge : gauges) {
            pusher.subscribe(gauge.getId()).bind("hit", callback);
        }
    }

    private void unsubscribeFromGaugeChannels() {
        for (Gauge gauge : gauges) {
            pusher.unsubscribe(gauge.getId());
        }
    }
}
