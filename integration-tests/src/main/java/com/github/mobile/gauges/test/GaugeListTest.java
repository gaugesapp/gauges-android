package com.github.mobile.gauges.test;

import android.test.ActivityInstrumentationTestCase2;

import com.github.mobile.gauges.ui.GaugeListActivity;

/**
 * Tests of displaying a list of gauges
 */
public class GaugeListTest extends ActivityInstrumentationTestCase2<GaugeListActivity> {

    /**
     * Create test for {@link GaugeListActivity}
     */
    public GaugeListTest() {
        super("com.github.mobile.gauges.ui", GaugeListActivity.class);
    }

    /**
     * Verify activity exists
     */
    public void testActivity() {
        assertNotNull(getActivity());
    }
}
