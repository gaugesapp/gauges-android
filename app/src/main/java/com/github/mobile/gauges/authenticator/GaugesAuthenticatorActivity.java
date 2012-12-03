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
package com.github.mobile.gauges.authenticator;

import static android.R.layout.simple_dropdown_item_1line;
import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static com.github.kevinsawicki.http.HttpRequest.post;
import static com.github.mobile.gauges.authenticator.AuthConstants.GAUGES_ACCOUNT_TYPE;
import static com.github.mobile.gauges.core.GaugesConstants.URL_AUTH;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.wishlist.EditTextUtils;
import com.github.kevinsawicki.wishlist.EditTextUtils.BooleanRunnable;
import com.github.kevinsawicki.wishlist.Toaster;
import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.R.menu;
import com.github.mobile.gauges.R.string;
import com.github.mobile.gauges.ui.TextWatcherAdapter;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockAccountAuthenticatorActivity;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

/**
 * Activity to authenticate the user against gaug.es
 */
public class GaugesAuthenticatorActivity extends
        RoboSherlockAccountAuthenticatorActivity {

    /**
     * PARAM_CONFIRMCREDENTIALS
     */
    public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";

    /**
     * PARAM_PASSWORD
     */
    public static final String PARAM_PASSWORD = "password";

    /**
     * PARAM_USERNAME
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * PARAM_AUTHTOKEN_TYPE
     */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    private static final String TAG = "GaugesAuthActivity";

    private AccountManager accountManager;

    @InjectView(id.et_email)
    private AutoCompleteTextView emailText;

    @InjectView(id.et_password)
    private EditText passwordText;

    private MenuItem signInItem;

    private TextWatcher watcher = validationTextWatcher();

    private RoboAsyncTask<Boolean> authenticationTask;
    private String authToken;
    private String authTokenType;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
    private Boolean confirmCredentials = false;

    private String email;

    private String password;

    /**
     * Was the original caller asking for an entirely new account?
     */
    protected boolean requestNewAccount = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        accountManager = AccountManager.get(this);
        final Intent intent = getIntent();
        email = intent.getStringExtra(PARAM_USERNAME);
        authTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
        requestNewAccount = email == null;
        confirmCredentials = intent.getBooleanExtra(PARAM_CONFIRMCREDENTIALS,
                false);

        setContentView(layout.login_activity);

        emailText.setAdapter(new ArrayAdapter<String>(this,
                simple_dropdown_item_1line, userEmailAccounts()));

        EditTextUtils.onDone(passwordText, new BooleanRunnable() {

            @Override
            public boolean run() {
                if (signInItem != null && signInItem.isEnabled()) {
                    handleLogin();
                    return true;
                }
                return false;
            }
        });

        emailText.addTextChangedListener(watcher);
        passwordText.addTextChangedListener(watcher);

        TextView signupText = (TextView) findViewById(id.tv_signup);
        signupText.setMovementMethod(LinkMovementMethod.getInstance());
        signupText.setText(Html.fromHtml(getString(string.signup_link)));
    }

    private List<String> userEmailAccounts() {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        List<String> emailAddresses = new ArrayList<String>(accounts.length);
        for (Account account : accounts)
            emailAddresses.add(account.name);
        return emailAddresses;
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUIWithValidation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu optionsMenu) {
        getSupportMenuInflater().inflate(menu.login, optionsMenu);
        signInItem = optionsMenu.findItem(id.m_login);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateUIWithValidation();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case id.m_login:
            handleLogin();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void updateUIWithValidation() {
        boolean populated = populated(emailText) && populated(passwordText);
        if (signInItem != null)
            signInItem.setEnabled(populated);
    }

    private boolean populated(EditText editText) {
        return editText != null && editText.length() > 0;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(string.message_signing_in));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if (authenticationTask != null)
                    authenticationTask.cancel(true);
            }
        });
        return dialog;
    }

    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication.
     */
    public void handleLogin() {
        if (authenticationTask != null)
            return;

        if (requestNewAccount)
            email = emailText.getText().toString();
        password = passwordText.getText().toString();
        showProgress();

        authenticationTask = new RoboAsyncTask<Boolean>(this) {
            public Boolean call() throws Exception {
                HttpRequest request = post(URL_AUTH).form("email", email).form(
                        "password", password);
                Log.d(TAG, "response=" + request.code());
                return request.ok();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                Throwable cause = e.getCause() != null ? e.getCause() : e;

                String message;
                // A 401 is returned as an IOException with this message
                if ("Received authentication challenge is null".equals(cause
                        .getMessage()))
                    message = getString(string.message_bad_credentials);
                else if ("No authentication challenges found".equals(cause
                        .getMessage()))
                    message = getString(string.message_bad_credentials);
                else
                    message = cause.getMessage();

                Toaster.showLong(GaugesAuthenticatorActivity.this, message);
            }

            @Override
            public void onSuccess(Boolean authSuccess) {
                onAuthenticationResult(authSuccess);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                authenticationTask = null;
            }
        };
        authenticationTask.execute();
    }

    /**
     * Called when response is received from the server for confirm credentials
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller.
     *
     * @param result
     */
    protected void finishConfirmCredentials(boolean result) {
        final Account account = new Account(email, GAUGES_ACCOUNT_TYPE);
        accountManager.setPassword(account, password);

        final Intent intent = new Intent();
        intent.putExtra(KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. Also sets
     * the authToken in AccountManager for this account.
     */

    protected void finishLogin() {
        final Account account = new Account(email, GAUGES_ACCOUNT_TYPE);

        if (requestNewAccount)
            accountManager.addAccountExplicitly(account, password, null);
        else
            accountManager.setPassword(account, password);
        final Intent intent = new Intent();
        authToken = password;
        intent.putExtra(KEY_ACCOUNT_NAME, email);
        intent.putExtra(KEY_ACCOUNT_TYPE, GAUGES_ACCOUNT_TYPE);
        if (authTokenType != null
                && authTokenType.equals(AuthConstants.AUTHTOKEN_TYPE))
            intent.putExtra(KEY_AUTHTOKEN, authToken);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param result
     */
    public void onAuthenticationResult(boolean result) {
        if (result)
            if (!confirmCredentials)
                finishLogin();
            else
                finishConfirmCredentials(true);
        else {
            Log.e(TAG, "onAuthenticationResult: failed to authenticate");
            if (requestNewAccount)
                Toaster.showLong(GaugesAuthenticatorActivity.this,
                        string.message_auth_failed_new_account);
            else
                Toaster.showLong(GaugesAuthenticatorActivity.this,
                        string.message_auth_failed);
        }
    }
}
