package com.github.mobile.gauges.core;

/**
 * Gauge information
 */
public class Gauge {

	private DatedViewSummary today;

	private String title;

	/**
	 * @return today
	 */
	public DatedViewSummary getToday() {
		return today;
	}

	/**
	 * @param today
	 * @return this gauge
	 */
	public Gauge setToday(DatedViewSummary today) {
		this.today = today;
		return this;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 * @return this gauge
	 */
	public Gauge setTitle(String title) {
		this.title = title;
		return this;
	}

}
