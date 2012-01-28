package com.github.mobile.gauges.core;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit tests of client API
 */
public class GaugesApiClientUtilTest {

    @Test
    @Ignore("requires valid credentials")
    public void shouldCreateClient() throws Exception {
        Client clientData = new GaugesService("someone@example.com", "mypassword").createClient("DroidThing");

        assertThat(clientData.getKey(), notNullValue());
        assertThat(clientData.getDescription(), equalTo("DroidThing"));
    }
}
