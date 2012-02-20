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

package com.github.mobile.gauges;

import android.accounts.AccountsException;

import com.github.mobile.gauges.authenticator.ApiKeyProvider;
import com.github.mobile.gauges.core.GaugesService;
import com.google.inject.Inject;

import java.io.IOException;

/**
 * Provider for a {@link GaugesService} instance
 */
public class GaugesServiceProvider {

    @Inject
    private ApiKeyProvider keyProvider;

    /**
     * Get service for configured key provider
     * <p>
     * This method gets an auth key and so it blocks and shouldn't be called on the main thread.
     *
     * @return gauges service
     * @throws IOException
     * @throws AccountsException
     */
    public GaugesService getService() throws IOException, AccountsException {
        return new GaugesService(keyProvider.getAuthKey());
    }
}
