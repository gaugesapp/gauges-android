package com.github.mobile.gauges.ui;

import static com.github.mobile.gauges.ui.ToastUtil.toastOnUiThread;
import android.accounts.AccountsException;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.R.string;
import com.github.mobile.gauges.core.Gauge;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Class to load a list of gauges
 */
public class GaugeListLoader extends AsyncLoader<List<Gauge>> {

    private final static String TAG = "GLL";

    private final Activity activity;

    private final GaugesServiceProvider serviceProvider;

    /**
     * Creates a gauge list loader using the given {@link Activity} and {@link GaugesServiceProvider}
     *
     * @param activity
     * @param serviceProvider
     */
    @Inject
    public GaugeListLoader(final Activity activity, final GaugesServiceProvider serviceProvider) {
        super(activity);
        this.activity = activity;
        this.serviceProvider = serviceProvider;
    }

    /**
     * Show {@link Toast} and log given exception
     *
     * @param e
     */
    protected void showError(final Exception e) {
        Log.d(TAG, "Exception getting gauges", e);
        toastOnUiThread(activity, activity.getString(string.error_loading_gauges));
    }

    @Override
    public List<Gauge> loadInBackground() {
        try {
            return serviceProvider.getService().getGauges();
        } catch (IOException e) {
            showError(e);
        } catch (AccountsException e) {
            showError(e);
        }
        return Collections.emptyList();
    }
}
