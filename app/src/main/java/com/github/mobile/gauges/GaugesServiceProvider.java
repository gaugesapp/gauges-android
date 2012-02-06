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
