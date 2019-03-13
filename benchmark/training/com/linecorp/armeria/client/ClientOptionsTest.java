/**
 * Copyright 2015 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.client;


import ClientOption.DEFAULT_MAX_RESPONSE_LENGTH;
import ClientOption.DEFAULT_RESPONSE_TIMEOUT_MILLIS;
import ClientOption.DEFAULT_WRITE_TIMEOUT_MILLIS;
import ClientOption.HTTP_HEADERS;
import HttpHeaderNames.HOST;
import HttpHeaders.EMPTY_HEADERS;
import com.linecorp.armeria.common.HttpHeaders;
import org.junit.Test;

import static ClientOptions.DEFAULT;


public class ClientOptionsTest {
    @Test
    public void testSetHttpHeader() {
        final HttpHeaders httpHeader = HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("x-user-defined"), "HEADER_VALUE");
        final ClientOptions options = ClientOptions.of(HTTP_HEADERS.newValue(httpHeader));
        assertThat(options.get(HTTP_HEADERS)).contains(httpHeader);
        final ClientOptions options2 = DEFAULT;
        assertThat(options2.get(HTTP_HEADERS)).contains(EMPTY_HEADERS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBlackListHeader() {
        ClientOptions.of(HTTP_HEADERS.newValue(HttpHeaders.of(HOST, "localhost")));
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidDefaultWriteTimeoutMillis() {
        ClientOptions.of(DEFAULT_WRITE_TIMEOUT_MILLIS.newValue(null));
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidDefaultResponseTimeoutMillis() {
        ClientOptions.of(DEFAULT_RESPONSE_TIMEOUT_MILLIS.newValue(null));
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidDefaultMaxResponseLength() {
        ClientOptions.of(DEFAULT_MAX_RESPONSE_LENGTH.newValue(null));
    }
}

