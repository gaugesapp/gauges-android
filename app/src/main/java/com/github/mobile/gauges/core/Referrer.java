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

import java.io.Serializable;

/**
 * Referrer to a page on a {@link Gauge}
 */
public class Referrer implements Serializable {

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
     * @return this referrer
     */
    public Referrer setViews(long views) {
        this.views = views;
        return this;
    }

    /**
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     * @return this referrer
     */
    public Referrer setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     * @return this referrer
     */
    public Referrer setPath(String path) {
        this.path = path;
        return this;
    }

    /**
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     * @return this referrer
     */
    public Referrer setUrl(String url) {
        this.url = url;
        return this;
    }
}
