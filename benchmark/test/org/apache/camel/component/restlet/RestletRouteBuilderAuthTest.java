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
package org.apache.camel.component.restlet;


import Exchange.CONTENT_TYPE;
import MediaType.APPLICATION_XML;
import RestletConstants.RESTLET_LOGIN;
import RestletConstants.RESTLET_PASSWORD;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;


public class RestletRouteBuilderAuthTest extends CamelSpringTestSupport {
    @Test
    public void testBasicAuth() throws IOException {
        // START SNIPPET: auth_request
        final String id = "89531";
        Map<String, Object> headers = new HashMap<>();
        headers.put(RESTLET_LOGIN, "admin");
        headers.put(RESTLET_PASSWORD, "foo");
        headers.put(CONTENT_TYPE, APPLICATION_XML);
        headers.put("id", id);
        String response = template.requestBodyAndHeaders("direct:start-auth", "<order foo='1'/>", headers, String.class);
        // END SNIPPET: auth_request
        assertEquals(("received [<order foo='1'/>] as an order id = " + id), response);
    }

    @Test(expected = CamelExecutionException.class)
    public void testhBasicAuthError() throws IOException {
        Map<String, Object> headers = new HashMap<>();
        headers.put(RESTLET_LOGIN, "admin");
        headers.put(RESTLET_PASSWORD, "bad");
        headers.put("id", "xyz");
        String response = ((String) (template.requestBodyAndHeaders("direct:start-auth", "<order foo='1'/>", headers)));
        assertNotNull("No response", response);
        assertTrue(response.contains("requires user authentication"));
    }
}

