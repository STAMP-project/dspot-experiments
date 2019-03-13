/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.http.common.cookie;


import java.io.IOException;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CookieHandlerTest extends CamelTestSupport {
    private CookieHandler cookieHandler;

    private CookiePolicy cookiePolicy;

    private int expectedNumberOfCookieValues;

    private String uriStr;

    private Exchange exchange;

    public CookieHandlerTest(CookieHandler cookieHandler, CookiePolicy cookiePolicy, String uri, int expectedNumberOfCookieValues, String description) {
        this.cookieHandler = cookieHandler;
        this.cookiePolicy = cookiePolicy;
        this.uriStr = uri;
        this.expectedNumberOfCookieValues = expectedNumberOfCookieValues;
    }

    @Test
    public void setReceiveAndTestCookie() throws IOException, URISyntaxException {
        URI uri = new URI(uriStr);
        cookieHandler.setCookiePolicy(cookiePolicy);
        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Set-Cookie", Collections.singletonList("Customer=\"WILE_E_COYOTE\";Version=1;Path=\"/acme\";Domain=\".example.com\""));
        cookieHandler.storeCookies(exchange, uri, headerMap);
        Map<String, List<String>> cookieHeaders = cookieHandler.loadCookies(exchange, uri);
        assertNotNull(cookieHeaders);
        assertNotNull(cookieHeaders.get("Cookie"));
        assertEquals(expectedNumberOfCookieValues, cookieHeaders.get("Cookie").size());
    }
}

