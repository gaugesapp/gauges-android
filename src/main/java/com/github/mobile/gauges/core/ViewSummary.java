package com.github.mobile.gauges.core;

import java.io.Serializable;

/**
 * View summary
 */
public class ViewSummary implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1916316095553659715L;

	private long people;

	private long views;

	/**
	 * @return people
	 */
	public long getPeople() {
		return people;
	}

	/**
	 * @param people
	 * @return this summary
	 */
	public ViewSummary setPeople(long people) {
		this.people = people;
		return this;
	}

	/**
	 * @return views
	 */
	public long getViews() {
		return views;
	}

	/**
	 * @param views
	 * @return this summary
	 */
	public ViewSummary setViews(long views) {
		this.views = views;
		return this;
	}
}
