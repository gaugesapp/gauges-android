/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    /**
     * Pusher authentication URL
     */
    String URL_PUSHER_AUTH = URL_BASE + "pusher/auth";
}
