package com.github.mobile.gauges.ui.airtraffic;

import static android.os.Build.VERSION.SDK_INT;
import static android.view.View.STATUS_BAR_HIDDEN;
import static com.github.mobile.gauges.IntentConstants.GAUGES;
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

    @InjectView(id.tv_gauge_location)
    private TextView gaugeLocation;

    private Map<String, Gauge> gauges = new HashMap<String, Gauge>();

    private final Executor backgroundThread = Executors.newFixedThreadPool(1);

    private final Pusher pusher = new Pusher(PUSHER_APP_KEY);

    private AirTrafficResourceProvider resourceProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(airtraffic_activity);

        resourceProvider = new AirTrafficResourceProvider(getResources());

        airTrafficView.setResourceProvider(resourceProvider).setLabelHeight(gaugeText.getTextSize() * 3 / 2);

        getSupportActionBar().hide();

        if (SDK_INT >= 14)
            // On ICS this equivalent to SYSTEM_UI_FLAG_LOW_PROFILE - the dimmed-menu-buttons mode
            getWindow().getDecorView().setSystemUiVisibility(STATUS_BAR_HIDDEN);

        @SuppressWarnings("unchecked")
        Collection<Gauge> intentGauges = (Collection<Gauge>) getIntent().getSerializableExtra(GAUGES);
        if (intentGauges != null && !intentGauges.isEmpty())
            loadGauges(intentGauges);
        else
            getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unsubscribeFromGaugeChannels(gauges.values());
        airTrafficView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        airTrafficView.resume();
        subscribeToGaugeChannels(gauges.values());
    }

    @Override
    public Loader<List<Gauge>> onCreateLoader(int id, Bundle args) {
        return new GaugeListLoader(this, serviceProvider);
    }

    @Override
    public void onLoadFinished(Loader<List<Gauge>> listLoader, List<Gauge> gauges) {
        loadGauges(gauges);
    }

    private void loadGauges(Collection<Gauge> gauges) {
        unsubscribeFromGaugeChannels(this.gauges.values());
        Map<String, Gauge> newGauges = new HashMap<String, Gauge>();
        for (Gauge gauge : gauges)
            newGauges.put(gauge.getId(), gauge);
        this.gauges = newGauges;
        resourceProvider.setGauges(gauges);
        subscribeToGaugeChannels(gauges);
    }

    /**
     * Get geographic location label for hit
     *
     * @param hit
     * @return label
     */
    protected String getLocation(final Hit hit) {
        String city = hit.city != null ? hit.city : "";
        String region = hit.region != null ? hit.region : "";
        String country = hit.country != null ? hit.country : "";

        if (country.length() == 0)
            return "";

        String location;
        if (region.length() > 0 && ("United States".equals(country) || "Canada".equals(country)))
            location = region;
        else
            location = country;

        if (city.length() > 0)
            return city + ", " + location;
        else
            return location;
    }

    /**
     * Get page title of hit
     *
     * @param hit
     * @return title
     */
    protected String getTitle(final Hit hit) {
        if (hit.title != null && hit.title.length() > 0) {
            int separator = hit.title.indexOf(" // ");
            if (separator == -1)
                separator = hit.title.indexOf(" - ");
            if (separator > 0)
                return hit.title.substring(0, separator);
            else
                return hit.title;
        } else
            return "";
    }

    @Override
    public void onLoaderReset(Loader<List<Gauge>> listLoader) {
    }

    private void subscribeToGaugeChannels(final Collection<Gauge> subscribeGauges) {
        if (subscribeGauges.isEmpty())
            return;
        backgroundThread.execute(new Runnable() {

            public void run() {
                final AirTrafficPusherCallback callback = new AirTrafficPusherCallback() {

                    @Override
                    protected void onHit(final Hit hit) {
                        airTrafficView.addHit(hit);

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

                                gaugeLocation.setText(getLocation(hit));

                                String title = getTitle(hit);
                                if (title.length() > 0)
                                    gaugeText.setText(gauge.getTitle() + ": " + title);
                                else
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
