package com.github.mobile.gauges.core;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Gauges API service
 */
public class GaugesService {

	static final String URL_BASE = "https://secure.gaug.es/";

	private static final String URL_GAUGES = URL_BASE + "gauges/";

	private static final String URL_EMBEDDED = URL_GAUGES + "embedded";

	private final Gson gson= new GsonBuilder()
					.setDateFormat("yyyy-MM-dd")
					.setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();

	private String username, password, apiKey;

	/**
	 * Create gauges service
	 *
	 * @param username
	 * @param password
	 */
	public GaugesService(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

    public GaugesService(String apiKey) {
        this.apiKey = apiKey;
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
        if (apiKey!=null) {
            return request.header("X-Gauges-Token", apiKey);
        } else {
            return request.basic(username, password);
        }
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
			Gauges gauges = gson.fromJson(request.reader(), Gauges.class);
			return gauges != null ? gauges.getGauges() : Collections
					.<Gauge> emptyList();
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
			HttpRequest request = execute(HttpRequest.get(URL_GAUGES + gaugeId
					+ "/content"));
			GaugeContent content = gson.fromJson(request.reader(),
					GaugeContent.class);
			return content != null ? content.getContent() : Collections
					.<PageContent> emptyList();
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
			HttpRequest request = execute(HttpRequest.get(URL_GAUGES + gaugeId
					+ "/referrers"));
			GaugeReferrers referrers = gson.fromJson(request.reader(),
					GaugeReferrers.class);
			return referrers != null ? referrers.getReferrers() : Collections
					.<Referrer> emptyList();
		} catch (HttpRequestException e) {
			throw e.getCause();
		}
	}
}
