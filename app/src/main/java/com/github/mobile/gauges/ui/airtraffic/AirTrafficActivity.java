package com.github.mobile.gauges.ui.airtraffic;

import static android.os.Build.VERSION.SDK_INT;
import static android.support.v4.view.Window.FEATURE_ACTION_BAR_OVERLAY;
import static android.view.View.STATUS_BAR_HIDDEN;
import static com.github.mobile.gauges.R.layout.airtraffic_activity;

import com.github.mobile.gauges.R;
import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.ui.GaugeListLoader;
import com.google.inject.Inject;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

/**
 * Activity to display list of gauge summaries
 */
public class AirTrafficActivity extends RoboFragmentActivity implements
    		LoaderManager.LoaderCallbacks<List<Gauge>> {

    @Inject
    private GaugeListLoader gaugeListLoader;

    private @InjectView(R.id.air_traffic) AirTrafficView airTrafficView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature((int) FEATURE_ACTION_BAR_OVERLAY);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));


        if (SDK_INT >= 14) {
            // On ICS this equivalent to SYSTEM_UI_FLAG_LOW_PROFILE - the dimmed-menu-buttons mode
            getWindow().getDecorView().setSystemUiVisibility(STATUS_BAR_HIDDEN);
        }

        getSupportLoaderManager().initLoader(0, null, this);
        setContentView(airtraffic_activity);
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
        return gaugeListLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Gauge>> listLoader, List<Gauge> gauges) {
        airTrafficView.setGauges(gauges);
    }

    @Override
    public void onLoaderReset(Loader<List<Gauge>> listLoader) {}
}
