package com.github.mobile.gauges.core;

/**
 * gaug.es constants
 */
public interface GaugesConstants {

	/**
	 * Base URL for all requests
	 */
	String URL_BASE = "https://secure.gaug.es/";

	/**
	 * Authentication URL
	 */
	String URL_AUTH = URL_BASE + "authenticate";

	/**
	 * Gauges URL
	 */
	String URL_GAUGES = URL_BASE + "gauges/";

	/**
	 * Embedded Gauges URL
	 */
	String URL_EMBEDDED = URL_GAUGES + "embedded";

	/**
	 * Clients URL
	 */
	String URL_CLIENTS = URL_BASE + "clients";
}
