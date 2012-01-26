package com.github.mobile.gauges.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Collection of {@link Gauge} referrers
 */
public class GaugeReferrers implements Iterable<Referrer> {

	private List<Referrer> referrers;

	/**
	 * @return referrers
	 */
	public List<Referrer> getReferrers() {
		return referrers != null ? referrers : Collections
				.<Referrer> emptyList();
	}

	/**
	 * @param referrers
	 */
	public void setReferrers(List<Referrer> referrers) {
		this.referrers = referrers;
	}

	@Override
	public Iterator<Referrer> iterator() {
		return getReferrers().iterator();
	}
}
