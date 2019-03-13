/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.test.support.rawhttp;


import HttpHeader.CONTENT_TYPE;
import HttpStatus.OK_200;
import HttpTester.Response;
import java.io.IOException;
import java.util.List;
import org.eclipse.jetty.http.HttpTester;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HttpResponseTesterTest {
    @Test
    public void testHttp11Response() throws IOException {
        StringBuffer rawResponse = new StringBuffer();
        rawResponse.append("HTTP/1.1 200 OK\n");
        rawResponse.append("Date: Mon, 08 Jun 2009 22:56:04 GMT\n");
        rawResponse.append("Content-Type: text/plain\n");
        rawResponse.append("Content-Length: 28\n");
        rawResponse.append("Last-Modified: Mon, 08 Jun 2009 17:06:22 GMT\n");
        rawResponse.append("Connection: close\n");
        rawResponse.append("Server: Jetty(7.0.y.z-SNAPSHOT\n");
        rawResponse.append("\n");
        rawResponse.append("ABCDEFGHIJKLMNOPQRSTTUVWXYZ\n");
        rawResponse.append("\n");
        HttpTester.Response response = HttpTester.parseResponse(rawResponse.toString());
        Assertions.assertEquals("HTTP/1.1", response.getVersion().asString(), "Response.version");
        Assertions.assertEquals(200, response.getStatus(), "Response.status");
        Assertions.assertEquals("OK", response.getReason(), "Response.reason");
        Assertions.assertEquals("text/plain", response.get(CONTENT_TYPE), "Response[Content-Type]");
        Assertions.assertEquals(28, response.getLongField("Content-Length"), "Response[Content-Length]");
        Assertions.assertEquals("close", response.get("Connection"), "Response[Connection]");
        String expected = "ABCDEFGHIJKLMNOPQRSTTUVWXYZ\n";
        Assertions.assertEquals(expected, response.getContent().toString(), "Response.content");
    }

    @Test
    public void testMultiHttp11Response() throws IOException {
        StringBuffer rawResponse = new StringBuffer();
        rawResponse.append("HTTP/1.1 200 OK\n");
        rawResponse.append("Date: Mon, 08 Jun 2009 23:05:26 GMT\n");
        rawResponse.append("Content-Type: text/plain\n");
        rawResponse.append("Content-Length: 28\n");
        rawResponse.append("Last-Modified: Mon, 08 Jun 2009 17:06:22 GMT\n");
        rawResponse.append("Server: Jetty(7.0.y.z-SNAPSHOT)\n");
        rawResponse.append("\n");
        rawResponse.append("ABCDEFGHIJKLMNOPQRSTTUVWXYZ\n");
        rawResponse.append("HTTP/1.1 200 OK\n");
        rawResponse.append("Date: Mon, 08 Jun 2009 23:05:26 GMT\n");
        rawResponse.append("Content-Type: text/plain\n");
        rawResponse.append("Content-Length: 25\n");
        rawResponse.append("Last-Modified: Mon, 08 Jun 2009 17:06:22 GMT\n");
        rawResponse.append("Server: Jetty(7.0.y.z-SNAPSHOT)\n");
        rawResponse.append("\n");
        rawResponse.append("Host=Default\n");
        rawResponse.append("Resource=R1\n");
        rawResponse.append("\n");
        rawResponse.append("HTTP/1.1 200 OK\n");
        rawResponse.append("Date: Mon, 08 Jun 2009 23:05:26 GMT\n");
        rawResponse.append("Content-Type: text/plain\n");
        rawResponse.append("Content-Length: 25\n");
        rawResponse.append("Last-Modified: Mon, 08 Jun 2009 17:06:22 GMT\n");
        rawResponse.append("Connection: close\n");
        rawResponse.append("Server: Jetty(7.0.y.z-SNAPSHOT)\n");
        rawResponse.append("\n");
        rawResponse.append("Host=Default\n");
        rawResponse.append("Resource=R2\n");
        List<HttpTester.Response> responses = HttpTesting.readResponses(rawResponse.toString());
        Assertions.assertNotNull(responses, "Responses should not be null");
        Assertions.assertEquals(3, responses.size(), "Responses.size");
        HttpTester.Response resp1 = responses.get(0);
        // System.err.println(resp1.toString());
        Assertions.assertEquals(OK_200, resp1.getStatus());
        Assertions.assertEquals("text/plain", resp1.get("Content-Type"));
        MatcherAssert.assertThat(resp1.getContent(), CoreMatchers.containsString("ABCDEFGHIJKLMNOPQRSTTUVWXYZ\n"));
        MatcherAssert.assertThat(resp1.get("Connection"), CoreMatchers.is(CoreMatchers.not("close")));
        HttpTester.Response resp2 = responses.get(1);
        // System.err.println(resp2.toString());
        Assertions.assertEquals(OK_200, resp2.getStatus());
        Assertions.assertEquals("text/plain", resp2.get("Content-Type"));
        MatcherAssert.assertThat(resp2.getContent(), CoreMatchers.containsString("Host=Default\nResource=R1\n"));
        MatcherAssert.assertThat(resp2.get("Connection"), CoreMatchers.is(CoreMatchers.not("close")));
        HttpTester.Response resp3 = responses.get(2);
        // System.err.println(resp3.toString());
        Assertions.assertEquals(OK_200, resp3.getStatus());
        Assertions.assertEquals("text/plain", resp3.get("Content-Type"));
        MatcherAssert.assertThat(resp3.getContent(), CoreMatchers.containsString("Host=Default\nResource=R2\n"));
        MatcherAssert.assertThat(resp3.get("Connection"), CoreMatchers.is("close"));
    }
}

