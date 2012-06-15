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

import java.util.Date;

/**
 * Dated view summary
 */
public class DatedViewSummary extends ViewSummary {

    private static final long serialVersionUID = 7356147548009255795L;

    private Date date;

    /**
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date
     * @return this summary
     */
    public DatedViewSummary setDate(Date date) {
        this.date = date;
        return this;
    }
}
