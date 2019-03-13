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
package org.apache.camel.component.salesforce;


import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@Category(Standalone.class)
@RunWith(Parameterized.class)
public class RawPayloadTest extends AbstractSalesforceTestBase {
    @Parameterized.Parameter
    public static String format;

    @Parameterized.Parameter(1)
    public static String endpointUri;

    private static final String OAUTH2_TOKEN_PATH = "/services/oauth2/token";

    private static final String XML_RESPONSE = "<response/>";

    private static final String JSON_RESPONSE = "{ \"response\" : \"mock\" }";

    private static HttpUrl loginUrl;

    private static MockWebServer server;

    private static String lastFormat;

    private static String expectedResponse;

    private static String requestBody;

    private static Map<String, Object> headers;

    @Test
    public void testRestApi() throws Exception {
        final String responseBody = template().requestBodyAndHeaders(RawPayloadTest.endpointUri, RawPayloadTest.requestBody, RawPayloadTest.headers, String.class);
        assertNotNull(("Null response for endpoint " + (RawPayloadTest.endpointUri)), responseBody);
        assertEquals(("Unexpected response for endpoint " + (RawPayloadTest.endpointUri)), RawPayloadTest.expectedResponse, responseBody);
    }
}

