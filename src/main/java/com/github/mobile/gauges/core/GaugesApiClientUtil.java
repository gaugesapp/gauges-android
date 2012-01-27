package com.github.mobile.gauges.core;

import static com.github.kevinsawicki.http.HttpRequest.post;
import static com.github.mobile.gauges.core.GaugesConstants.URL_CLIENTS;

import com.github.kevinsawicki.http.HttpRequest;


public class GaugesApiClientUtil {


    public static ClientData createClientDataWithDescription(EmailPasswordCredentials credentials, String description) {
        HttpRequest request = post(URL_CLIENTS)
                .basic(credentials.emailAddress, credentials.password)
                .send("description=" + description); // description is not coming through in supplied key?

        int responseCode = request.code();
        if (responseCode / 100 != 2)
            throw new RuntimeException("Request for auth token returned bad http response code: "+responseCode);

        return GaugesService.GSON.fromJson(request.reader(), CreateClientResponse.class).client;
    }

}
