package com.github.mobile.gauges;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import com.emorym.android_pusher.Pusher;
import com.emorym.android_pusher.PusherCallback;
import com.github.mobile.gauges.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentLinkedQueue;

import static android.graphics.Bitmap.createScaledBitmap;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;

public class AirTrafficWallpaper extends WallpaperService {

    private final Handler mHandler = new Handler();
    private Bitmap map;


    @Override
    public void onCreate() {
        map = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        return new AirTrafficEngine();
    }

    class AirTrafficEngine extends Engine {
        
        public static final String TAG = "ATE";

        private final Paint dotPoint = new Paint();
        private float mCenterX;
        private float mCenterY;

        private final Runnable mDrawCube = new Runnable() {
            public void run() {
                drawFrame();
            }
        };
        private boolean mVisible;
        private Pusher pusher;
        private float scale;
        private ConcurrentLinkedQueue<Hit> hits = new ConcurrentLinkedQueue<Hit>();
        private Bitmap fittedMap;
        private Paint mapPaint;

     	public static final String PUSHER_APP_KEY = "887bd32ce6b7c2049e0b";
        public static final String PUSHER_CHANNEL_1 = "4ed95060f5a1f5137e000005";

        AirTrafficEngine() {
            // Create a Paint to draw the lines for our cube

            mapPaint = new Paint();

            final Paint paint = dotPoint;
            paint.setColor(0x99ee0000);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(2);
            paint.setStrokeCap(Paint.Cap.ROUND);

            pusher = new Pusher( PUSHER_APP_KEY );
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mDrawCube);
//            pusher.disconnect();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                Pusher.Channel channel1 = pusher.subscribe( PUSHER_CHANNEL_1 );
                channel1.bind("hit", new PusherCallback() {
                    @Override
                    public void onEvent(JSONObject eventData) {
                        Log.d( "ATW", PUSHER_CHANNEL_1 + ":" + eventData.toString() );
                        try {
                            float longitude = (float) eventData.getDouble("longitude");
                            float latitude = (float) eventData.getDouble("latitude");
                            Hit hit = new Hit(longitude, latitude, currentTimeMillis());
                            hits.add(hit);
                            if (hits.size()>50) {
                                hits.poll();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                });

                drawFrame();
            } else {
                pusher.unsubscribe(PUSHER_CHANNEL_1);
                mHandler.removeCallbacks(mDrawCube);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            // store the center of the surface, so we can draw the cube in the right spot
            mCenterX = width/2.0f;
            mCenterY = height/2.0f;
            scale = width/360.0f;
            fittedMap = createScaledBitmap(map, width, height, true);
            drawFrame();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mDrawCube);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xStep, float yStep, int xPixels, int yPixels) {
            drawFrame();
        }

        void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    // draw something
                    drawTraffic(c);
                    // drawTouchPoint(c);
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            // Reschedule the next redraw
            mHandler.removeCallbacks(mDrawCube);
            if (mVisible) {
                mHandler.postDelayed(mDrawCube, 1000 / 25);
            }
        }

        void drawTraffic(Canvas c) {
            c.save();
            c.translate(mCenterX, mCenterY);
            c.drawColor(0xffbbbbbb);
            if (fittedMap!=null) {
                c.drawBitmap(fittedMap, -fittedMap.getWidth()/2,-fittedMap.getHeight()/2,mapPaint);
            }

            long now = currentTimeMillis();
            for (Hit hit: hits) {
                long age = now-hit.time;
                float radius = 7+((float) Math.pow(5 * (1000.0f / (age + 1000)), 2));

                int alpha = round((1 - (age / 60000.0f))* 0xee);
                int colour = 0x00ee0000 + (alpha<<24);
                dotPoint.setColor(colour);
                c.drawCircle(hit.lon * scale, -hit.lat * scale * 2, radius, dotPoint);
            }
            c.restore();
        }
    }
}