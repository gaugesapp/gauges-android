package com.github.mobile.gauges.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Collection of all gauges
 */
public class Gauges implements Iterable<Gauge> {

	private List<Gauge> gauges;

	/**
	 * Is collection empty?
	 *
	 * @return true if empty, false otherwise
	 */
	public boolean isEmpty() {
		return gauges == null || gauges.isEmpty();
	}

	/**
	 * @return gauges
	 */
	public List<Gauge> getGauges() {
		return gauges != null ? gauges : Collections.<Gauge> emptyList();
	}

	public Iterator<Gauge> iterator() {
		return getGauges().iterator();
	}
}
