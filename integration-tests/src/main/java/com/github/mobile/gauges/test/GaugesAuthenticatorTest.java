package com.github.mobile.gauges.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;

import com.github.mobile.gauges.R;
import com.github.mobile.gauges.authenticator.GaugesAuthenticatorActivity;

/**
 * Tests of displaying the authenticator activity
 */
public class GaugesAuthenticatorTest extends ActivityInstrumentationTestCase2<GaugesAuthenticatorActivity> {

    /**
     * Create test for {@link GaugesAuthenticatorActivity}
     */
    public GaugesAuthenticatorTest() {
        super("com.github.mobile.gauges.authenticator", GaugesAuthenticatorActivity.class);
    }

    /**
     * Verify activity exists
     */
    public void testActivityExists() {
        assertNotNull(getActivity());
    }

    /**
     * Verify sign in button is initially disabled
     */
    public void testSignInDisabled() {
        View view = getActivity().findViewById(R.id.b_signin);
        assertNotNull(view);
        assertTrue(view instanceof Button);
        assertFalse(((Button) view).isEnabled());
    }
}
