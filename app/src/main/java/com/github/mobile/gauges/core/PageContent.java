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
 * Content entry for a {@link Gauge}
 */
public class PageContent implements Serializable {

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
     * @return this content
     */
    public PageContent setViews(long views) {
        this.views = views;
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
     * @return this content
     */
    public PageContent setPath(String path) {
        this.path = path;
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
     * @return this content
     */
    public PageContent setTitle(String title) {
        this.title = title;
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
     * @return this content
     */
    public PageContent setUrl(String url) {
        this.url = url;
        return this;
    }
}
