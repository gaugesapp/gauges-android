/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mobile.gauges.realtime;


import static android.util.Log.DEBUG;
import static android.util.Log.isLoggable;
import static com.github.mobile.gauges.authenticator.AuthConstants.AUTHTOKEN_TYPE;
import static com.github.mobile.gauges.authenticator.AuthConstants.GAUGES_ACCOUNT_TYPE;
import static java.util.concurrent.Executors.newFixedThreadPool;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OnAccountsUpdateListener;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.core.GaugesService;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import roboguice.service.RoboService;

/**
 * Android Service that interested Activities/Fragments can bind to in order to obtain realtime traffic data.
 * The lifetime of the service is dictated by bound clients - when there are no more bound clients, the Android OS
 * will automatically destroy the service.
 * <p/>
 * On pausing/resuming your activity/fragment, you should add/remove yourself as listener on the localBinder.
 * This stops the service from holding references to your activity after it's finished, which would be
 * a memory leak.
 * <p/>
 * On starting/stopping your activity/fragment, you should bind/unbind the service. This allows Android to shutdown
 * the service if no-one is currently using it.
 */
public class RealtimeTrafficService extends RoboService implements OnAccountsUpdateListener {

    public static final String TAG = "RTS";

    @Inject
    private AccountManager accountManager;

    private final ConcurrentHashMap<HitListener, Boolean> hitListeners = new ConcurrentHashMap<HitListener, Boolean>();

    private final ExecutorService backgroundThread = newFixedThreadPool(1);

    private final IBinder mBinder = new LocalBinder();

    private RealtimeTrafficMonitor gaugeMonitor;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Creating realtime service");
        accountManager.addOnAccountsUpdatedListener(this, null, true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying realtime service");
        accountManager.removeOnAccountsUpdatedListener(this);
        cancelMonitor();
        backgroundThread.shutdownNow();
    }

    private void cancelMonitor() {
        if (gaugeMonitor != null)
            gaugeMonitor.cancel();
    }

    @Override
    public void onAccountsUpdated(Account[] accounts) {
        Log.d(TAG, "onAccountsUpdated() invoked");
        for (final Account account : accounts) {
            Log.d(TAG, "Checking " + account.name);
            if (account.type.equals(GAUGES_ACCOUNT_TYPE)) {
                Log.d(TAG, "Correct account type for  " + account.name);     
                cancelMonitor();
                gaugeMonitor = new RealtimeTrafficMonitor(this, new Provider<GaugesService>() {
                    @Override
                    public GaugesService get() {
                        String apiKey = null;
                        try {
                            apiKey = accountManager.blockingGetAuthToken(account, AUTHTOKEN_TYPE, true);
                            Log.d(TAG, "Got apiKey : "+apiKey);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return new GaugesService(apiKey);
                    }
                });
                backgroundThread.execute(gaugeMonitor);
            }
        }
    }

    void broadcast(Hit hit, Gauge gauge) {
        if (isLoggable(TAG, DEBUG)) {
            long views = gauge.getToday().getViews();
            Log.d(TAG, "Broadcasting '" + gauge.getTitle() + "' views=" + views + " to " + hitListeners.size());
        }
        for (HitListener hitListener : hitListeners.keySet())
            hitListener.observe(hit, gauge);
    }

    class LocalBinder extends Binder {

        public void addHitListener(HitListener hitListener) {
            hitListeners.put(hitListener, true);
        }

        /**
         * We don't want to retain references to hitListeners longer than we have to,
         * they are probably activities or fragments that reference activities, and
         * keeping a reference to those is a memory leak.
         *
         * @param hitListener
         */
        public void removeHitListener(HitListener hitListener) {
            hitListeners.remove(hitListener);
        }

        public List<Gauge> getGauges() {
            return gaugeMonitor == null ? null : gaugeMonitor.getGauges();
        }
    }

}
