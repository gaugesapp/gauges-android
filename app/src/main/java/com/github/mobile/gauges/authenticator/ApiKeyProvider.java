package com.github.mobile.gauges.authenticator;

import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static com.github.mobile.gauges.authenticator.AuthConstants.AUTHTOKEN_TYPE;
import static com.github.mobile.gauges.authenticator.AuthConstants.GAUGES_ACCOUNT_TYPE;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AccountsException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.github.mobile.gauges.core.GaugesService;
import com.google.inject.Inject;

import java.io.IOException;

/**
 * Bridge class that obtains a gaug.es API key for the currently configured account
 */
public class ApiKeyProvider {

    private static final String TAG = "AKP";

    @Inject
    private Activity activity;
    @Inject
    private AccountManager accountManager;

    /**
     * This call blocks, so shouldn't be called on the UI thread
     *
     * @return API key to be used for authorization with a {@link GaugesService} instance
     * @throws AccountsException
     * @throws IOException
     */
    public String getAuthKey() throws AccountsException, IOException {
        AccountManagerFuture<Bundle> accountManagerFuture = accountManager.getAuthTokenByFeatures(GAUGES_ACCOUNT_TYPE,
                AUTHTOKEN_TYPE, new String[0], activity, null, null, null, null);

        Bundle result = accountManagerFuture.getResult();
        String authToken = result.getString(KEY_AUTHTOKEN);
        Log.d(TAG, "Got authToken "+(authToken==null?null:authToken.substring(0,2)+"…"));
        return authToken;
    }
}
