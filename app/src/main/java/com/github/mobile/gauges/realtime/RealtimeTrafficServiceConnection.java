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

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.github.mobile.gauges.core.Gauge;

import java.util.List;

/**
 * Provides a default implementation for connection to the realtime traffic servce,
 * implementing the common requirement of registering a HitListener.
 */
public class RealtimeTrafficServiceConnection implements ServiceConnection {

    public static final String TAG = "RTS.C";
    private final HitListener hitListener;
    private RealtimeTrafficService.LocalBinder localBinder;
    private boolean paused = false;

    public RealtimeTrafficServiceConnection(HitListener hitListener) {
        this.hitListener = hitListener;
    }

    @Override
    public synchronized void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d(TAG, "onServiceConnected componentName=" + componentName);

        localBinder = ((RealtimeTrafficService.LocalBinder) iBinder);
        if (!paused) {
            localBinder.addHitListener(hitListener);
        }
    }

    @Override
    public synchronized void onServiceDisconnected(ComponentName componentName) {
        Log.d(TAG, "onServiceDisconnected componentName=" + componentName);
        localBinder = null;
    }

    public synchronized void onResume() {
        paused = false;
        if (localBinder != null) {
            localBinder.addHitListener(hitListener);
        }
    }

    public synchronized void onPause() {
        paused = true;
        if (localBinder != null) {
            localBinder.removeHitListener(hitListener);
        }
    }

    public List<Gauge> getGauges() {
        return localBinder == null ? null : localBinder.getGauges();
    }
}
