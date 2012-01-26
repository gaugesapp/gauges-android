package com.github.mobile.gauges.core;

import java.io.Serializable;

/**
 * Content entry for a {@link Gauge}
 */
public class PageContent implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 3578053478298223202L;

	private long views;

	private String path;

	private String title;

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
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
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
