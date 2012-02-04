package com.github.mobile.gauges.ui;

import static java.util.Collections.emptyList;
import android.accounts.AccountsException;
import android.content.Context;
import android.util.Log;

import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.core.Gauge;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.List;

/**
 * Class to load a list of gauges
 */
public class GaugeListLoader extends AsyncLoader<List<Gauge>> {

    private final static String TAG = "GLL";

    private final GaugesServiceProvider serviceProvider;

    /**
     * Creates a gauge list loader using the given {@link Context} and {@link GaugesServiceProvider}
     *
     * @param context
     * @param serviceProvider
     */
    @Inject
    public GaugeListLoader(final Context context, final GaugesServiceProvider serviceProvider) {
        super(context);
        this.serviceProvider = serviceProvider;
    }

    @Override
    public List<Gauge> loadInBackground() {
        try {
            return serviceProvider.getService().getGauges();
        } catch (IOException e) {
            Log.d(TAG, "Exception getting gauges", e);
        } catch (AccountsException e) {
            Log.d(TAG, "Exception getting gauges", e);
        }
        return emptyList();
    }
}
