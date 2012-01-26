package com.github.mobile.gauges.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Container for all {@link Gauge} contents
 */
public class GaugeContent implements Iterable<PageContent> {

	private List<PageContent> content;

	/**
	 * Is collection empty?
	 *
	 * @return true if empty, false otherwise
	 */
	public boolean isEmpty() {
		return content == null || content.isEmpty();
	}

	/**
	 * @return contents
	 */
	public List<PageContent> getContent() {
		return content != null ? content : Collections
				.<PageContent> emptyList();
	}

	public Iterator<PageContent> iterator() {
		return getContent().iterator();
	}

}
