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

package com.github.mobile.gauges.test;

import static com.github.mobile.gauges.authenticator.AuthConstants.AUTHTOKEN_TYPE;
import static com.github.mobile.gauges.authenticator.AuthConstants.GAUGES_ACCOUNT_TYPE;
import static com.github.mobile.gauges.tests.R.string.test_account_api_key;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

/**
 * Utilities for verifying gaug.es account
 */
public class TestUserAccountUtil {

    private static final String TAG = "TestUserAccountUtil";

    /**
     * Checks the device has a valid Gauges account, if not, adds one using the test credentials found in system
     * property 'gauges.test.api.key'.
     *
     * The credentials can be passed on the command line like this: mvn
     * -Dgauges.test.api.key=0123456789abcdef0123456789abcdef install
     *
     * @param instrumentation
     *            taken from the test context
     * @return true if valid account credentials are available
     */
    public static boolean ensureValidGaugesAccountAvailable(Instrumentation instrumentation) {
        Context c = instrumentation.getContext();
        AccountManager accountManager = AccountManager.get(instrumentation.getTargetContext());

        for (Account account : accountManager.getAccountsByType(GAUGES_ACCOUNT_TYPE)) {
            if (accountManager.peekAuthToken(account, AUTHTOKEN_TYPE) != null) {
                Log.i(TAG, "Using existing account : " + account.name);
                return true; // we have a valid account that has successfully authenticated
            }
        }

        String testApiKey = c.getString(test_account_api_key);
        String truncatedApiKey = testApiKey.substring(0, 4) + "…";

        if (!testApiKey.matches("\\p{XDigit}{32}")) {
            Log.w(TAG, "No valid test account credentials in gauges.test.api.key : " + truncatedApiKey);
            return false;
        }

        Log.i(TAG, "Adding test account using supplied api key credential : " + truncatedApiKey);
        Account account = new Account("test@example.com", GAUGES_ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, null, null); // this test account will not have a valid password
        accountManager.setAuthToken(account, AUTHTOKEN_TYPE, testApiKey);
        return true;
    }
}
