package com.github.mobile.gauges.ui.airtraffic;

import static android.os.Build.VERSION.SDK_INT;
import static android.view.View.STATUS_BAR_HIDDEN;
import static com.github.mobile.gauges.R.layout.airtraffic_activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.ui.GaugeListLoader;
import com.google.inject.Inject;

import java.util.List;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

/**
 * Activity to display list of gauge summaries
 */
public class AirTrafficActivity extends RoboFragmentActivity implements LoaderCallbacks<List<Gauge>> {

    @Inject
    private GaugesServiceProvider serviceProvider;

    @InjectView(id.air_traffic)
    private AirTrafficView airTrafficView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(airtraffic_activity);

        getActionBar().hide();

        if (SDK_INT >= 14)
            // On ICS this equivalent to SYSTEM_UI_FLAG_LOW_PROFILE - the dimmed-menu-buttons mode
            getWindow().getDecorView().setSystemUiVisibility(STATUS_BAR_HIDDEN);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        airTrafficView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        airTrafficView.resume();
    }

    @Override
    public Loader<List<Gauge>> onCreateLoader(int id, Bundle args) {
        return new GaugeListLoader(this, serviceProvider);
    }

    @Override
    public void onLoadFinished(Loader<List<Gauge>> listLoader, List<Gauge> gauges) {
        airTrafficView.setGauges(gauges);
    }

    @Override
    public void onLoaderReset(Loader<List<Gauge>> listLoader) {
    }
}
