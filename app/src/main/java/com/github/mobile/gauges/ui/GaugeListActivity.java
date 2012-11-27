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
package com.github.mobile.gauges.ui;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.mobile.gauges.R.layout;

/**
 * Activity to display list of gauge summaries
 */
public class GaugeListActivity extends PagerActivity implements
        FragmentProvider {

    private SherlockFragment selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(layout.gauge_list);
    }

    @Override
    protected FragmentProvider getProvider() {
        return this;
    }

    @Override
    public SherlockFragment getSelected() {
        if (selected == null)
            selected = (SherlockFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.list);
        return selected;
    }
}
