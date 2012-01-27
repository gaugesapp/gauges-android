package com.github.mobile.gauges.authenticator;

import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static com.github.mobile.gauges.authenticator.Constants.AUTHTOKEN_TYPE;
import static com.github.mobile.gauges.authenticator.Constants.GAUGES_ACCOUNT_TYPE;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.inject.Inject;

public class ApiKeyProvider {

    private static final String TAG = "AKP";

    @Inject Activity activity;
    @Inject AccountManager accountManager;

    /**
     * This call blocks, so shouldn't be called on the UI thread
     *
     * @return auth key
     */
    public String getAuthKey() {
        AccountManagerFuture<Bundle> accountManagerFuture = accountManager.getAuthTokenByFeatures
                (GAUGES_ACCOUNT_TYPE, AUTHTOKEN_TYPE, new String[]{}, activity, null, null, null, null);

        try {
            String apiKey = accountManagerFuture.getResult().getString(KEY_AUTHTOKEN);
            Log.d(TAG, "apiKey=" + apiKey);
            return apiKey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
