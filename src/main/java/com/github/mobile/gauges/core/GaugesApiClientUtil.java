package com.github.mobile.gauges.core;

import static com.github.kevinsawicki.http.HttpRequest.post;
import static com.github.mobile.gauges.core.GaugesService.URL_BASE;
import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class GaugesApiClientUtil {

    private static final String URL_CLIENTS = URL_BASE + "clients";
    
    private static final Gson gson = new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();

    public static ClientData createClientDataWithDescription(EmailPasswordCredentials credentials, String description) {
        HttpRequest request = post(URL_CLIENTS)
                .basic(credentials.emailAddress, credentials.password)
                .send("description=" + description); // description is not coming through in supplied key?

        int responseCode = request.code();
        if (responseCode / 100 != 2)
            throw new RuntimeException("Request for auth token returned bad http response code: "+responseCode);

        return gson.fromJson(request.reader(), CreateClientResponse.class).client;
    }

}
