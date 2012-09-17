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

package com.github.mobile.gauges.ui.airtraffic;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.FROYO;
import android.accounts.AccountsException;

import com.emorym.android_pusher.Pusher;
import com.github.mobile.gauges.GaugesServiceProvider;
import com.github.mobile.gauges.core.GaugesService;

import java.io.IOException;

/**
 * Pusher that handles authentication for private channels
 */
public class GaugesPusher extends Pusher {

    private static final String PUSHER_APP_KEY = "887bd32ce6b7c2049e0b";

    private static final int AUTH_SLEEP = 100;

    private static final int AUTH_TIMEOUT = 30 * 1000;

    private final GaugesServiceProvider serviceProvider;

    private GaugesService service;

    /**
     * Create pusher
     *
     * @param provider
     */
    public GaugesPusher(GaugesServiceProvider provider) {
        // Skip certificate validation on Froyo or below
        super(PUSHER_APP_KEY, true, SDK_INT <= FROYO);
        serviceProvider = provider;
    }

    @Override
    protected String authenticate(String channelName) throws IOException {
        if (service == null)
            try {
                service = serviceProvider.getService();
            } catch (AccountsException e) {
                IOException io = new IOException("Account lookup failed");
                io.initCause(e);
                throw io;
            }
        // Socket id is required before authentication can begin so wait
        // until it comes back on the connection_established event
        int slept = 0;
        while (mSocketId == null) {
            try {
                Thread.sleep(AUTH_SLEEP);
            } catch (InterruptedException e) {
                break;
            }
            // Terminate if connection drops
            if (mWebSocket == null || !mWebSocket.isConnected())
                break;
            slept += AUTH_SLEEP;
            if (slept >= AUTH_TIMEOUT)
                break;
        }
        return mSocketId != null ? service
                .getPusherAuth(mSocketId, channelName) : null;
    }
}
