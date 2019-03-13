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
package org.apache.camel.component.http4;


import java.io.IOException;
import java.util.Map;
import org.apache.camel.component.http4.handler.HeaderValidationHandler;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;


public class HttpCamelHeadersTest extends BaseHttpTest {
    protected HttpServer localServer;

    @Test
    public void httpHeadersShouldPresent() throws Exception {
        assertExchange(doExchange());
    }

    private class MyHeaderValidationHandler extends HeaderValidationHandler {
        private String expectProtocolVersion;

        MyHeaderValidationHandler(String expectedMethod, String protocolVersion, String responseContent, Map<String, String> expectedHeaders) {
            super(expectedMethod, null, null, responseContent, expectedHeaders);
            expectProtocolVersion = protocolVersion;
        }

        public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws IOException, HttpException {
            if (!(expectProtocolVersion.equals(request.getProtocolVersion().toString()))) {
                response.setStatusCode(HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED);
                return;
            }
            super.handle(request, response, context);
        }
    }
}

