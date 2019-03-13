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
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_WWW_FORM;
import MediaType.APPLICATION_XML;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;


public class RestletRouteBuilderTest extends RestletTestSupport {
    private static final String ID = "89531";

    private static final String JSON = "{\"document type\": \"JSON\"}";

    @Test
    public void testProducer() throws IOException {
        Map<String, Object> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, APPLICATION_XML);
        String response = template.requestBodyAndHeaders("direct:start", "<order foo='1'/>", headers, String.class);
        assertEquals(("received [<order foo='1'/>] as an order id = " + (RestletRouteBuilderTest.ID)), response);
        headers.put("id", "89531");
        response = template.requestBodyAndHeaders((("restlet:http://localhost:" + (RestletTestSupport.portNum)) + "/orders?restletMethod=post&foo=bar"), "<order foo='1'/>", headers, String.class);
        assertEquals(("received [<order foo='1'/>] as an order id = " + (RestletRouteBuilderTest.ID)), response);
    }

    @Test
    public void testProducerJSON() throws IOException {
        String response = template.requestBodyAndHeader((("restlet:http://localhost:" + (RestletTestSupport.portNum)) + "/ordersJSON?restletMethod=post&foo=bar"), RestletRouteBuilderTest.JSON, CONTENT_TYPE, APPLICATION_JSON, String.class);
        assertEquals(RestletRouteBuilderTest.JSON, response);
    }

    @Test
    public void testProducerJSONFailure() throws IOException {
        String response = template.requestBodyAndHeader((("restlet:http://localhost:" + (RestletTestSupport.portNum)) + "/ordersJSON?restletMethod=post&foo=bar"), "{'JSON'}", CONTENT_TYPE, APPLICATION_JSON, String.class);
        assertEquals("{'JSON'}", response);
    }

    @Test
    public void testConsumer() throws IOException {
        Client client = new Client(Protocol.HTTP);
        Response response = client.handle(new org.restlet.Request(Method.GET, (("http://localhost:" + (RestletTestSupport.portNum)) + "/orders/99991/6")));
        assertEquals("received GET request with id=99991 and x=6", response.getEntity().getText());
    }

    @Test
    public void testUnhandledConsumer() throws IOException {
        Client client = new Client(Protocol.HTTP);
        Response response = client.handle(new org.restlet.Request(Method.POST, (("http://localhost:" + (RestletTestSupport.portNum)) + "/orders/99991/6")));
        // expect error status as no Restlet consumer to handle POST method
        assertEquals(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, response.getStatus());
        assertNotNull(response.getEntity().getText());
    }

    @Test
    public void testNotFound() throws IOException {
        Client client = new Client(Protocol.HTTP);
        Response response = client.handle(new org.restlet.Request(Method.POST, (("http://localhost:" + (RestletTestSupport.portNum)) + "/unknown")));
        // expect error status as no Restlet consumer to handle POST method
        assertEquals(Status.CLIENT_ERROR_NOT_FOUND, response.getStatus());
        assertNotNull(response.getEntity().getText());
    }

    @Test
    public void testFormsProducer() throws IOException {
        Map<String, Object> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, APPLICATION_WWW_FORM);
        String response = template.requestBodyAndHeaders((("restlet:http://localhost:" + (RestletTestSupport.portNum)) + "/login?restletMethod=post"), "user=jaymandawg&passwd=secret$%", headers, String.class);
        assertEquals("received user: jaymandawgpassword: secret$%", response);
    }

    @Test
    public void testFormsProducerMapBody() throws IOException {
        Map<String, Object> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, APPLICATION_WWW_FORM);
        Map<String, String> body = new HashMap<>();
        body.put("user", "jaymandawg");
        body.put("passwd", "secret$%");
        String response = template.requestBodyAndHeaders((("restlet:http://localhost:" + (RestletTestSupport.portNum)) + "/login?restletMethod=post"), body, headers, String.class);
        assertEquals("received user: jaymandawgpassword: secret$%", response);
    }
}

