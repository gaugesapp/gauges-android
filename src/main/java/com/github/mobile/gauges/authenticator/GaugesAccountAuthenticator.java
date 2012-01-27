package com.github.mobile.gauges.authenticator;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static com.github.mobile.gauges.authenticator.AuthConstants.GAUGES_ACCOUNT_TYPE;
import static com.github.mobile.gauges.authenticator.GaugesAuthenticatorActivity.PARAM_AUTHTOKEN_TYPE;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.mobile.gauges.core.ClientData;
import com.github.mobile.gauges.core.EmailPasswordCredentials;
import com.github.mobile.gauges.core.GaugesService;

class GaugesAccountAuthenticator extends AbstractAccountAuthenticator {

    private static final String TAG = "GaugesAccountAuth";

    private Context mContext;

    public GaugesAccountAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    /*
     * The user has requested to add a new account to the system. We return an intent that will launch our login screen
     * if the user has not logged in yet, otherwise our activity will just pass the user's credentials on to the
     * account
     * manager.
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType,
                             String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mContext, GaugesAuthenticatorActivity.class);
        intent.putExtra(PARAM_AUTHTOKEN_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
        // Log.d(AccountAuthenticatorService.TAG, "addAccount " + accountType +
        // " authTokenType=" + authTokenType);
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType,
                               Bundle options) throws NetworkErrorException {
        Log.d(TAG,"getAuthToken() called : authTokenType="+authTokenType);
        String password = AccountManager.get(mContext).getPassword(account);
        EmailPasswordCredentials credentials = new EmailPasswordCredentials(account.name, password);
        Log.d(TAG,"getAuthToken() credentials="+credentials);
        ClientData clientData = new GaugesService(credentials.emailAddress, credentials.password)
                .createClientData("Gaug.es for Android");
        String apiKey = clientData.getKey();
        Log.d(TAG,"getAuthToken() called : apiKey="+apiKey);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ACCOUNT_NAME, account.name);
        bundle.putString(KEY_ACCOUNT_TYPE, GAUGES_ACCOUNT_TYPE);
        bundle.putString(KEY_AUTHTOKEN, apiKey);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (authTokenType.equals(AuthConstants.AUTHTOKEN_TYPE)) {
            return authTokenType;
        }
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features)
            throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType,
                                    Bundle options) {
        return null;
    }
}
