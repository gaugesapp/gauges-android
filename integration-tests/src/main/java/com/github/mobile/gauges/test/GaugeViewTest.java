package com.github.mobile.gauges.test;

import static com.github.mobile.gauges.IntentConstants.GAUGE;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.github.mobile.gauges.core.Gauge;
import com.github.mobile.gauges.ui.GaugeViewActivity;

/**
 * Tests for displaying a specific {@link Gauge}
 */
public class GaugeViewTest extends ActivityInstrumentationTestCase2<GaugeViewActivity> {

    /**
     * Create test for {@link GaugeViewActivity}
     */
    public GaugeViewTest() {
        super("com.github.mobile.gauges.ui", GaugeViewActivity.class);
    }

    /**
     * Configure intent used to display a {@link Gauge}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.putExtra(GAUGE, new Gauge().setTitle("a gauge").setId("123"));
        setActivityIntent(intent);
    }

    /**
     * Verify activity exists
     */
    public void testActivityExists() {
        assertNotNull(getActivity());
    }
}
