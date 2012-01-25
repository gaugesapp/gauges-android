package com.github.mobile.gauges.core;

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

	private static final String URL_BASE = "https://secure.gaug.es/";

	private static final String URL_GAUGES = URL_BASE + "/gauges/embedded";

	private final Gson gson;

	private final String password;

	private final String username;

	/**
	 * Create gauges service
	 * 
	 * @param username
	 * @param password
	 */
	public GaugesService(final String username, final String password) {
		this.username = username;
		this.password = password;
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	}

	/**
	 * Get all gauges
	 * 
	 * @return non-null but possibly empty list of gauges
	 * @throws IOException
	 */
	public List<Gauge> getGauges() throws IOException {
		try {
			HttpRequest request = HttpRequest.get(URL_GAUGES).basic(username,
					password);
			if (!request.ok())
				throw new IOException("Unexpected response code: "
						+ request.code());
			Gauges gauges = gson.fromJson(request.reader(), Gauges.class);
			return gauges != null ? gauges.getGauges() : Collections
					.<Gauge> emptyList();
		} catch (HttpRequestException e) {
			throw e.getCause();
		}
	}
}
