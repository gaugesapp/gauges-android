package com.github.mobile.gauges.ui;

import static java.util.Collections.emptyList;

import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.core.Gauge;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.List;

import android.accounts.AccountsException;
import android.content.Context;
import android.util.Log;

public class GaugeListLoader extends AsyncLoader<List<Gauge>> {

    private final static String TAG = "GLL";
    
    @Inject
    private GaugesServiceProvider serviceProvider;
    
    @Inject
    public GaugeListLoader(Context context) {
        super(context);
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
