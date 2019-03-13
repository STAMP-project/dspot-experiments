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
package org.apache.camel.component.jetty;


import Exchange.CONTENT_TYPE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Test;


public class HttpRouteTest extends BaseJettyTest {
    protected static final String POST_MESSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + "<test>Hello World</test>";

    protected String expectedBody = "<hello>world!</hello>";

    private int port1;

    private int port2;

    private int port3;

    private int port4;

    @Test
    public void testEndpoint() throws Exception {
        MockEndpoint mockEndpoint = getMockEndpoint("mock:a");
        mockEndpoint.expectedBodiesReceived(expectedBody);
        invokeHttpEndpoint();
        mockEndpoint.assertIsSatisfied();
        List<Exchange> list = mockEndpoint.getReceivedExchanges();
        Exchange exchange = list.get(0);
        assertNotNull("exchange", exchange);
        Message in = exchange.getIn();
        assertNotNull("in", in);
        Map<String, Object> headers = in.getHeaders();
        log.info(("Headers: " + headers));
        assertTrue(("Should be more than one header but was: " + headers), ((headers.size()) > 0));
    }

    @Test
    public void testHelloEndpoint() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStream is = new URL((("http://localhost:" + (port2)) + "/hello")).openStream();
        int c;
        while ((c = is.read()) >= 0) {
            os.write(c);
        } 
        String data = new String(os.toByteArray());
        assertEquals("<b>Hello World</b>", data);
    }

    @Test
    public void testEchoEndpoint() throws Exception {
        String out = template.requestBody((("http://localhost:" + (port1)) + "/echo"), "HelloWorld", String.class);
        assertEquals("Get a wrong output ", "HelloWorld", out);
    }

    @Test
    public void testEchoEndpointWithIgnoreResponseBody() throws Exception {
        String out = template.requestBody((("http://localhost:" + (port1)) + "/echo?ignoreResponseBody=true"), "HelloWorld", String.class);
        assertNull("Get a wrong output ", out);
    }

    @Test
    public void testPostParameter() throws Exception {
        NameValuePair[] data = new NameValuePair[]{ new NameValuePair("request", "PostParameter"), new NameValuePair("others", "bloggs") };
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod((("http://localhost:" + (port1)) + "/parameter"));
        post.setRequestBody(data);
        client.executeMethod(post);
        InputStream response = post.getResponseBodyAsStream();
        String out = context.getTypeConverter().convertTo(String.class, response);
        assertEquals("Get a wrong output ", "PostParameter", out);
    }

    @Test
    public void testPostXMLMessage() throws Exception {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod((("http://localhost:" + (port1)) + "/postxml"));
        StringRequestEntity entity = new StringRequestEntity(HttpRouteTest.POST_MESSAGE, "application/xml", "UTF-8");
        post.setRequestEntity(entity);
        client.executeMethod(post);
        InputStream response = post.getResponseBodyAsStream();
        String out = context.getTypeConverter().convertTo(String.class, response);
        assertEquals("Get a wrong output ", "OK", out);
    }

    @Test
    public void testPostParameterInURI() throws Exception {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod((("http://localhost:" + (port1)) + "/parameter?request=PostParameter&others=bloggs"));
        StringRequestEntity entity = new StringRequestEntity(HttpRouteTest.POST_MESSAGE, "application/xml", "UTF-8");
        post.setRequestEntity(entity);
        client.executeMethod(post);
        InputStream response = post.getResponseBodyAsStream();
        String out = context.getTypeConverter().convertTo(String.class, response);
        assertEquals("Get a wrong output ", "PostParameter", out);
    }

    @Test
    public void testPutParameterInURI() throws Exception {
        HttpClient client = new HttpClient();
        PutMethod put = new PutMethod((("http://localhost:" + (port1)) + "/parameter?request=PutParameter&others=bloggs"));
        StringRequestEntity entity = new StringRequestEntity(HttpRouteTest.POST_MESSAGE, "application/xml", "UTF-8");
        put.setRequestEntity(entity);
        client.executeMethod(put);
        InputStream response = put.getResponseBodyAsStream();
        String out = context.getTypeConverter().convertTo(String.class, response);
        assertEquals("Get a wrong output ", "PutParameter", out);
    }

    @Test
    public void testDisableStreamCache() throws Exception {
        String response = template.requestBodyAndHeader((("http://localhost:" + (port3)) + "/noStreamCache"), new ByteArrayInputStream("This is a test".getBytes()), "Content-Type", "application/xml", String.class);
        assertEquals("Get a wrong output ", "OK", response);
    }

    @Test
    public void testRequestBufferSize() throws Exception {
        InputStream in = this.getClass().getResourceAsStream("/META-INF/LICENSE.txt");
        int fileSize = in.available();
        String response = template.requestBodyAndHeader((("http://localhost:" + (port4)) + "/requestBufferSize"), in, CONTENT_TYPE, "application/txt", String.class);
        assertEquals("Got a wrong response.", fileSize, response.length());
    }

    @Test
    public void testResponseCode() throws Exception {
        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod((("http://localhost:" + (port1)) + "/responseCode"));
        client.executeMethod(get);
        // just make sure we get the right
        assertEquals("Get a wrong status code.", 400, get.getStatusCode());
    }
}

