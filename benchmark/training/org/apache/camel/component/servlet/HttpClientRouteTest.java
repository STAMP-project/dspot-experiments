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
package org.apache.camel.component.servlet;


import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletUnitClient;
import java.io.ByteArrayInputStream;
import org.apache.camel.FailedToCreateProducerException;
import org.apache.camel.FailedToCreateRouteException;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;


public class HttpClientRouteTest extends ServletCamelRouterTestSupport {
    private static final String POST_DATA = "<request> hello world </request>";

    private static final String CONTENT_TYPE = "text/xml; charset=UTF-8";

    private static final String UNICODE_TEXT = "B\u00fce W\u00f6rld";

    @Test
    public void testHttpClient() throws Exception {
        WebRequest req = new PostMethodWebRequest(((ServletCamelRouterTestSupport.CONTEXT_URL) + "/services/hello"), new ByteArrayInputStream(HttpClientRouteTest.POST_DATA.getBytes()), HttpClientRouteTest.CONTENT_TYPE);
        ServletUnitClient client = newClient();
        WebResponse response = client.getResponse(req);
        assertEquals("Get wrong content type", "text/xml", response.getContentType());
        assertTrue("UTF-8".equalsIgnoreCase(response.getCharacterSet()));
        assertEquals("Get a wrong message header", "/hello", response.getHeaderField("PATH"));
        assertEquals("The response message is wrong ", "OK", response.getResponseMessage());
        req = new PostMethodWebRequest(((ServletCamelRouterTestSupport.CONTEXT_URL) + "/services/helloworld"), new ByteArrayInputStream(HttpClientRouteTest.POST_DATA.getBytes()), HttpClientRouteTest.CONTENT_TYPE);
        response = client.getResponse(req);
        assertEquals("Get wrong content type", "text/xml", response.getContentType());
        assertTrue("UTF-8".equalsIgnoreCase(response.getCharacterSet()));
        assertEquals("Get a wrong message header", "/helloworld", response.getHeaderField("PATH"));
        assertEquals("The response message is wrong ", "OK", response.getResponseMessage());
        client.setExceptionsThrownOnErrorStatus(false);
    }

    @Test
    public void testHttpRestricMethod() throws Exception {
        WebRequest req = new PostMethodWebRequest(((ServletCamelRouterTestSupport.CONTEXT_URL) + "/services/testHttpMethodRestrict"), new ByteArrayInputStream(HttpClientRouteTest.POST_DATA.getBytes()), "text/xml; charset=UTF-8");
        ServletUnitClient client = newClient();
        WebResponse response = client.getResponse(req);
        assertEquals("The response message is wrong ", "OK", response.getResponseMessage());
        assertEquals("The response body is wrong", HttpClientRouteTest.POST_DATA, response.getText());
        // Send other web method request
        req = new GetMethodWebRequest(((ServletCamelRouterTestSupport.CONTEXT_URL) + "/services/testHttpMethodRestrict"));
        try {
            response = client.getResponse(req);
            fail("Expect the exception here");
        } catch (Exception ex) {
            HttpException httpException = ((HttpException) (ex));
            assertEquals("Get a wrong response code", 405, httpException.getResponseCode());
        }
    }

    @Test
    public void testHttpConverter() throws Exception {
        WebRequest req = new PostMethodWebRequest(((ServletCamelRouterTestSupport.CONTEXT_URL) + "/services/testConverter"), new ByteArrayInputStream(HttpClientRouteTest.POST_DATA.getBytes()), "text/xml; charset=UTF-8");
        ServletUnitClient client = newClient();
        client.setExceptionsThrownOnErrorStatus(false);
        WebResponse response = client.getResponse(req);
        assertEquals("The response message is wrong ", "OK", response.getResponseMessage());
        assertEquals("The response body is wrong", "Bye World", response.getText());
    }

    @Test
    public void testHttpUnicodeResponseWithStringResponse() throws Exception {
        WebRequest req = new PostMethodWebRequest(((ServletCamelRouterTestSupport.CONTEXT_URL) + "/services/testUnicodeWithStringResponse"), new ByteArrayInputStream(HttpClientRouteTest.POST_DATA.getBytes()), "text/xml; charset=UTF-8");
        ServletUnitClient client = newClient();
        client.setExceptionsThrownOnErrorStatus(false);
        WebResponse response = client.getResponse(req);
        assertEquals("The response message is wrong ", "OK", response.getResponseMessage());
        assertEquals("The response body is wrong", HttpClientRouteTest.UNICODE_TEXT, response.getText());
    }

    @Test
    public void testHttpUnicodeResponseWithObjectResponse() throws Exception {
        WebRequest req = new PostMethodWebRequest(((ServletCamelRouterTestSupport.CONTEXT_URL) + "/services/testUnicodeWithObjectResponse"), new ByteArrayInputStream(HttpClientRouteTest.POST_DATA.getBytes()), "text/xml; charset=UTF-8");
        ServletUnitClient client = newClient();
        client.setExceptionsThrownOnErrorStatus(false);
        WebResponse response = client.getResponse(req);
        assertEquals("The response message is wrong ", "OK", response.getResponseMessage());
        assertEquals("The response body is wrong", HttpClientRouteTest.UNICODE_TEXT, response.getText());
    }

    @Test
    public void testCreateSerlvetEndpointProducer() throws Exception {
        if (!(startCamelContext)) {
            // don't test it with web.xml configure
            return;
        }
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:start").to("servlet:///testworld");
                }
            });
            fail("Excepts exception here");
        } catch (FailedToCreateRouteException ex) {
            assertTrue("Get a wrong exception.", ((ex.getCause()) instanceof FailedToCreateProducerException));
            assertTrue("Get a wrong cause of exception.", ((ex.getCause().getCause()) instanceof UnsupportedOperationException));
        }
    }

    public static class MyServletRoute extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            errorHandler(noErrorHandler());
            // START SNIPPET: route
            from("servlet:hello?matchOnUriPrefix=true").process(new Processor() {
                public void process(org.apache.camel.Exchange exchange) throws Exception {
                    String contentType = exchange.getIn().getHeader(Exchange.CONTENT_TYPE, String.class);
                    String path = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
                    path = path.substring(path.lastIndexOf("/"));
                    assertEquals("Get a wrong content type", HttpClientRouteTest.CONTENT_TYPE, contentType);
                    // assert camel http header
                    String charsetEncoding = exchange.getIn().getHeader(Exchange.HTTP_CHARACTER_ENCODING, String.class);
                    assertEquals("Get a wrong charset name from the message heaer", "UTF-8", charsetEncoding);
                    // assert exchange charset
                    assertEquals("Get a wrong charset naem from the exchange property", "UTF-8", exchange.getProperty(Exchange.CHARSET_NAME));
                    exchange.getOut().setHeader(Exchange.CONTENT_TYPE, (contentType + "; charset=UTF-8"));
                    exchange.getOut().setHeader("PATH", path);
                    exchange.getOut().setBody("<b>Hello World</b>");
                }
            });
            // END SNIPPET: route
            from("servlet:testHttpMethodRestrict?httpMethodRestrict=POST").process(new Processor() {
                public void process(org.apache.camel.Exchange exchange) throws Exception {
                    String request = exchange.getIn().getBody(String.class);
                    exchange.getOut().setBody(request);
                }
            });
            from("servlet:testConverter?matchOnUriPrefix=true").convertBodyTo(String.class).process(new Processor() {
                public void process(org.apache.camel.Exchange exchange) throws Exception {
                    javax.servlet.http.HttpServletRequest request = exchange.getIn(javax.servlet.http.HttpServletRequest.class);
                    assertNotNull("We should get request object here", request);
                    javax.servlet.http.HttpServletResponse response = exchange.getIn(javax.servlet.http.HttpServletResponse.class);
                    assertNotNull("We should get response object here", response);
                    String s = exchange.getIn().getBody(String.class);
                    assertEquals("<request> hello world </request>", s);
                }
            }).transform(constant("Bye World"));
            from("servlet:testUnicodeWithStringResponse?matchOnUriPrefix=true").process(new Processor() {
                public void process(org.apache.camel.Exchange exchange) throws Exception {
                    String contentType = exchange.getIn().getHeader(Exchange.CONTENT_TYPE, String.class);
                    exchange.getOut().setHeader(Exchange.CONTENT_TYPE, (contentType + "; charset=UTF-8"));
                }
            }).transform(constant(HttpClientRouteTest.UNICODE_TEXT));
            from("servlet:testUnicodeWithObjectResponse?matchOnUriPrefix=true").process(new Processor() {
                public void process(org.apache.camel.Exchange exchange) throws Exception {
                    String contentType = exchange.getIn().getHeader(Exchange.CONTENT_TYPE, String.class);
                    exchange.getOut().setHeader(Exchange.CONTENT_TYPE, (contentType + "; charset=UTF-8"));
                }
            }).transform(constant(new Object() {
                @Override
                public String toString() {
                    return HttpClientRouteTest.UNICODE_TEXT;
                }
            }));
        }
    }
}

