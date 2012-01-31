package com.github.mobile.gauges.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Gauge information
 */
public class Gauge implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -8423297822694434626L;

	private DatedViewSummary today;

	private List<DatedViewSummary> recentDays;

	private String id;

	private String title;

	/**
	 * @param recentDays
	 */
	public void setRecentMonths(List<DatedViewSummary> recentDays) {
		this.recentDays = recentDays;
	}

	/**
	 * @return recentDays
	 */
	public List<DatedViewSummary> getRecentDays() {
		return recentDays != null ? recentDays : Collections
				.<DatedViewSummary> emptyList();
	}

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
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
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
