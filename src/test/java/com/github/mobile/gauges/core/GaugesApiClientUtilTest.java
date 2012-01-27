package com.github.mobile.gauges.core;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

public class GaugesApiClientUtilTest {

    @Test @Ignore("requires valid credentials")
    public void shouldCreateClient() {
        ClientData clientData = new GaugesService("someone@example.com", "mypassword").createClientData("DroidThing");

        assertThat(clientData.getKey(), notNullValue());
        assertThat(clientData.getDescription(), equalTo("DroidThing"));
    }
}
