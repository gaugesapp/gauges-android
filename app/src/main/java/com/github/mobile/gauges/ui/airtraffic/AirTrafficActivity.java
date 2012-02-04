package com.github.mobile.gauges.ui.airtraffic;

import static android.os.Build.VERSION.SDK_INT;
import static android.view.View.STATUS_BAR_HIDDEN;
import static com.github.mobile.gauges.R.layout.airtraffic_activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.ImageView;
import android.widget.TextView;

import com.emorym.android_pusher.Pusher;
import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.ui.GaugeListLoader;
import com.google.inject.Inject;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

/**
 * Activity to display list of gauge summaries
 */
public class AirTrafficActivity extends RoboFragmentActivity implements LoaderCallbacks<List<Gauge>> {

    private static final String PUSHER_APP_KEY = "887bd32ce6b7c2049e0b";

    @Inject
    private GaugesServiceProvider serviceProvider;

    @InjectView(id.air_traffic)
    private AirTrafficView airTrafficView;

    @InjectView(id.iv_pin)
    private ImageView pinImage;

    @InjectView(id.tv_gauge_name)
    private TextView gaugeText;

    private final ConcurrentLinkedQueue<Hit> hits = new ConcurrentLinkedQueue<Hit>();

    private Map<String, Gauge> gauges = new HashMap<String, Gauge>();

    private final Executor backgroundThread = Executors.newFixedThreadPool(1);

    private final Pusher pusher = new Pusher(PUSHER_APP_KEY);

    private AirTrafficResourceProvider resourceProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(airtraffic_activity);

        resourceProvider = new AirTrafficResourceProvider(getResources());

        airTrafficView.setResourceProvider(resourceProvider).setHits(hits);

        getActionBar().hide();

        if (SDK_INT >= 14)
            // On ICS this equivalent to SYSTEM_UI_FLAG_LOW_PROFILE - the dimmed-menu-buttons mode
            getWindow().getDecorView().setSystemUiVisibility(STATUS_BAR_HIDDEN);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unsubscribeFromGaugeChannels(this.gauges.values());
        airTrafficView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribeToGaugeChannels(this.gauges.values());
        airTrafficView.resume();
    }

    @Override
    public Loader<List<Gauge>> onCreateLoader(int id, Bundle args) {
        return new GaugeListLoader(this, serviceProvider);
    }

    @Override
    public void onLoadFinished(Loader<List<Gauge>> listLoader, List<Gauge> gauges) {
        unsubscribeFromGaugeChannels(this.gauges.values());
        Map<String, Gauge> newGauges = new HashMap<String, Gauge>();
        for (Gauge gauge : gauges)
            newGauges.put(gauge.getId(), gauge);
        this.gauges = newGauges;
        resourceProvider.setGauges(gauges);
        subscribeToGaugeChannels(gauges);
    }

    @Override
    public void onLoaderReset(Loader<List<Gauge>> listLoader) {
    }

    private void subscribeToGaugeChannels(final Collection<Gauge> subscribeGauges) {
        if (subscribeGauges.isEmpty())
            return;
        backgroundThread.execute(new Runnable() {

            public void run() {
                final AirTrafficPusherCallback callback = new AirTrafficPusherCallback(hits) {
                    protected void onHit(final Hit hit) {
                        super.onHit(hit);
                        gaugeText.post(new Runnable() {

                            public void run() {
                                Gauge gauge = gauges.get(hit.siteId);
                                if (gauge == null)
                                    return;
                                Bitmap bitmap = resourceProvider.getPin(hit.siteId);
                                if (bitmap != null)
                                    pinImage.setBackgroundDrawable(new BitmapDrawable(bitmap));
                                else
                                    pinImage.setBackgroundDrawable(null);
                                gaugeText.setText(gauge.getTitle());
                            }
                        });
                    }
                };
                for (Gauge gauge : subscribeGauges)
                    pusher.subscribe(gauge.getId()).bind("hit", callback);
            }
        });
    }

    private void unsubscribeFromGaugeChannels(final Collection<Gauge> unsubscribeGauges) {
        if (unsubscribeGauges.isEmpty())
            return;
        backgroundThread.execute(new Runnable() {

            public void run() {
                for (Gauge gauge : unsubscribeGauges)
                    pusher.unsubscribe(gauge.getId());
            }
        });
    }
}
