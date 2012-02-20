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


import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.KeyEvent.KEYCODE_A;
import static android.view.KeyEvent.KEYCODE_B;
import static com.github.mobile.gauges.test.TestUserAccountUtil.ensureValidGaugesAccountAvailable;
import static com.github.rtyley.android.screenshot.celebrity.Screenshots.poseForScreenshot;
import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.widget.EditText;

import com.github.mobile.gauges.R;
import com.github.mobile.gauges.authenticator.GaugesAuthenticatorActivity;
import com.github.mobile.gauges.ui.GaugeListActivity;
import com.github.mobile.gauges.ui.airtraffic.AirTrafficActivity;
import com.jayway.android.robotium.solo.Solo;

/**
 * This test drives the app, taking screenshots, to give a quick visual overview of the app
 * which can be
 *
 * To ensure that the screenshots make sense when viewed consecutively, they're all
 * generated in one test.
 */
public class AppearanceTest extends ActivityInstrumentationTestCase2<GaugeListActivity> {

    private final static String TAG = "AppearanceTest";
    
    private Solo solo;

    public AppearanceTest() {
        super(GaugeListActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation());
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @LargeTest
    public void testAppearance() throws Exception {
        driveSignInActivity();

        if (!ensureValidGaugesAccountAvailable(getInstrumentation())) {
            Log.d(TAG, "No Gauges account available, cutting test short.");
            return;
        }

        driveGaugeListAndGaugeView();
        driveAirTrafficActivity();
    }

    /**
     * This enters text into the Sign-In activity to simulate a user logging in, but doesn't actually use valid
     * credentials:
     *
     * <ul>
     * <li>This means real credentials won't get captured in screenshots
     * <li>It's actually difficult to enter arbitrary characters by sending key-presses - entering an upper-case
     * character requires controlling the 'shift' key setting.
     * </ul>
     */
    private void driveSignInActivity() {
        Activity signInActivity = startActivitySync(GaugesAuthenticatorActivity.class);

        EditText emailField = (EditText) signInActivity.findViewById(R.id.et_email);
        EditText passwordField = (EditText) signInActivity.findViewById(R.id.et_password);
        poseForScreenshot();
        solo.clickOnView(emailField);
        poseForScreenshot();
        solo.enterText(emailField, "lorem@ipsum.com");
        poseForScreenshot();
        solo.clickOnView(passwordField);
        poseForScreenshot();
        solo.sendKey(KEYCODE_A);
        poseForScreenshot();
        solo.sendKey(KEYCODE_B);
        poseForScreenshot();

//        solo.clickOnView(signInActivity.findViewById(R.id.b_signin));
//        poseForScreenshot();

        signInActivity.finish();
    }

    private void driveGaugeListAndGaugeView() {
        Activity activity = startActivitySync(GaugeListActivity.class);

        poseForScreenshot();
        solo.sleep(4000); // the list will take at least 4 seconds to load...
        displayFullList(); // ...and this times out after 10 seconds

        solo.clickInList(0); // show the first gauge
        displayFullList();

        solo.goBack();
        poseForScreenshot();

        solo.clickInList(2);  // show another gauge
        poseForScreenshot();

        activity.finish();
    }

    private void driveAirTrafficActivity() {
        Activity activity = startActivitySync(AirTrafficActivity.class);

        poseForScreenshot();
        solo.sleep(8000); // wait for push channel subscriptions to start
        for (int i=0;i<8;++i) {
            poseForScreenshot();
        }

        activity.finish();
    }


    private <T extends Activity> T startActivitySync(Class<T> clazz) {
        Intent intent = new Intent(getInstrumentation().getTargetContext(), clazz);
        intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_NEW_TASK);
        return (T) getInstrumentation().startActivitySync(intent);
    }

    private void displayFullList() {
        poseForScreenshot();
        poseForScreenshot();
        while (solo.scrollDown()) {}
        poseForScreenshot();
        while (solo.scrollUp()) {}
        poseForScreenshot();
        poseForScreenshot();
    }
}
