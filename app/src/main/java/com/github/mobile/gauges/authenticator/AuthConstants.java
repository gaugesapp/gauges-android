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

package com.github.mobile.gauges.authenticator;

/**
 * Constants for authenticating
 */
public interface AuthConstants {

    /**
     * Account type id
     */
    String GAUGES_ACCOUNT_TYPE = "com.github.gauges";

    /**
     * Account name
     */
    String GAUGES_ACCOUNT_NAME = "Gaug.es";

    /**
     * Provider id
     */
    String GAUGES_PROVIDER_AUTHORITY = "com.github.gauges.sync";

    /**
     * Auth token type
     */
    String AUTHTOKEN_TYPE = GAUGES_ACCOUNT_TYPE;
}
