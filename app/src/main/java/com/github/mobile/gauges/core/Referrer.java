package com.github.mobile.gauges.core;

import java.io.Serializable;

/**
 * Referrer to a page on a {@link Gauge}
 */
public class Referrer implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 7428556345275142034L;

	private long views;

	private String host;

	private String path;

	private String url;

	/**
	 * @return views
	 */
	public long getViews() {
		return views;
	}

	/**
	 * @param views
	 */
	public void setViews(long views) {
		this.views = views;
	}

	/**
	 * @return host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
