package com.github.mobile.gauges.core;


import static com.github.mobile.gauges.core.GaugesApiClientUtil.createClientDataWithDescription;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

public class GaugesApiClientUtilTest {

    @Test @Ignore("requires valid credentials")
    public void shouldCreateClient() {
        EmailPasswordCredentials credentials = new EmailPasswordCredentials("someone@example.com", "mypassword");

        ClientData clientData = createClientDataWithDescription(credentials, "DroidThing");

        assertThat(clientData.key, notNullValue());
        assertThat(clientData.description, equalTo("DroidThing"));
    }
}
