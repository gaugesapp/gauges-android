package com.github.mobile.gauges.core;

import static com.github.mobile.gauges.core.GaugesConstants.URL_CLIENTS;
import static com.github.mobile.gauges.core.GaugesConstants.URL_EMBEDDED;
import static com.github.mobile.gauges.core.GaugesConstants.URL_GAUGES;
import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Gauges API service
 */
public class GaugesService {

    /**
     * GSON instance to use for all request
     */
    public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd")
            .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();

    private final String apiKey;

    private final String username;

    private final String password;

    /**
     * Create gauges service
     *
     * @param username
     * @param password
     */
    public GaugesService(final String username, final String password) {
        this.username = username;
        this.password = password;
        this.apiKey = null;
    }

    /**
     * Create gauges service
     *
     * @param apiKey
     */
    public GaugesService(final String apiKey) {
        this.apiKey = apiKey;
        this.username = null;
        this.password = null;
    }

    /**
     * Execute request
     *
     * @param request
     * @return request
     * @throws IOException
     */
    protected HttpRequest execute(HttpRequest request) throws IOException {
        if (!addCredentialsTo(request).ok())
            throw new IOException("Unexpected response code: " + request.code());
        return request;
    }

    private HttpRequest addCredentialsTo(HttpRequest request) {
        if (apiKey != null)
            return request.header("X-Gauges-Token", apiKey);
        else
            return request.basic(username, password);
    }

    /**
     * Get all gauges
     *
     * @return non-null but possibly empty list of gauges
     * @throws IOException
     */
    public List<Gauge> getGauges() throws IOException {
        try {
            HttpRequest request = execute(HttpRequest.get(URL_EMBEDDED));
            Gauges gauges = GSON.fromJson(request.reader(), Gauges.class);
            return gauges != null ? gauges.getGauges() : Collections.<Gauge> emptyList();
        } catch (HttpRequestException e) {
            throw e.getCause();
        }
    }

    /**
     * Get content for gauge id
     *
     * @param gaugeId
     * @return non-null but possibly empty list of page content information
     * @throws IOException
     */
    public List<PageContent> getContent(String gaugeId) throws IOException {
        try {
            HttpRequest request = execute(HttpRequest.get(URL_GAUGES + gaugeId + "/content"));
            GaugeContent content = GSON.fromJson(request.reader(), GaugeContent.class);
            return content != null ? content.getContent() : Collections.<PageContent> emptyList();
        } catch (HttpRequestException e) {
            throw e.getCause();
        }
    }

    /**
     * Get referrers for gauge id
     *
     * @param gaugeId
     * @return non-null but possibly empty list of referrers
     * @throws IOException
     */
    public List<Referrer> getReferrers(String gaugeId) throws IOException {
        try {
            HttpRequest request = execute(HttpRequest.get(URL_GAUGES + gaugeId + "/referrers"));
            GaugeReferrers referrers = GSON.fromJson(request.reader(), GaugeReferrers.class);
            return referrers != null ? referrers.getReferrers() : Collections.<Referrer> emptyList();
        } catch (HttpRequestException e) {
            throw e.getCause();
        }
    }

    /**
     * Create API client with description
     *
     * @param description
     * @return created client
     * @throws IOException
     */
    public Client createClient(String description) throws IOException {
        try {
            HttpRequest request = addCredentialsTo(HttpRequest.post(URL_CLIENTS));
            request.form("description", description);
            if (!request.created())
                throw new IOException("Unexpected response code: " + request.code());
            CreateClientResponse response = GSON.fromJson(request.reader(), CreateClientResponse.class);
            return response != null ? response.client : null;
        } catch (HttpRequestException e) {
            throw e.getCause();
        }
    }

    /**
     * Get client with description
     *
     * @param description
     * @return client or null if none found matching description
     * @throws IOException
     */
    public Client getClient(String description) throws IOException {
        try {
            HttpRequest request = execute(HttpRequest.get(URL_CLIENTS));
            GaugesClients clients = GSON.fromJson(request.reader(), GaugesClients.class);
            if (clients == null)
                return null;
            for (Client client : clients)
                if (description.equals(client.getDescription()))
                    return client;
        } catch (HttpRequestException e) {
            throw e.getCause();
        }
        return null;
    }
}
