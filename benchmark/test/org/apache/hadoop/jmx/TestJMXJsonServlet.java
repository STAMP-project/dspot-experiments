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
package org.apache.hadoop.jmx;


import HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.hadoop.http.HttpServer2;
import org.apache.hadoop.http.HttpServerFunctionalTest;
import org.junit.Assert;
import org.junit.Test;


public class TestJMXJsonServlet extends HttpServerFunctionalTest {
    private static HttpServer2 server;

    private static URL baseUrl;

    @Test
    public void testQuery() throws Exception {
        String result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx?qry=java.lang:type=Runtime"));
        TestJMXJsonServlet.assertReFind("\"name\"\\s*:\\s*\"java.lang:type=Runtime\"", result);
        TestJMXJsonServlet.assertReFind("\"modelerType\"", result);
        result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx?qry=java.lang:type=Memory"));
        TestJMXJsonServlet.assertReFind("\"name\"\\s*:\\s*\"java.lang:type=Memory\"", result);
        TestJMXJsonServlet.assertReFind("\"modelerType\"", result);
        result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx"));
        TestJMXJsonServlet.assertReFind("\"name\"\\s*:\\s*\"java.lang:type=Memory\"", result);
        // test to get an attribute of a mbean
        result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx?get=java.lang:type=Memory::HeapMemoryUsage"));
        TestJMXJsonServlet.assertReFind("\"name\"\\s*:\\s*\"java.lang:type=Memory\"", result);
        TestJMXJsonServlet.assertReFind("\"committed\"\\s*:", result);
        // negative test to get an attribute of a mbean
        result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx?get=java.lang:type=Memory::"));
        TestJMXJsonServlet.assertReFind("\"ERROR\"", result);
        // test to CORS headers
        HttpURLConnection conn = ((HttpURLConnection) (new URL(TestJMXJsonServlet.baseUrl, "/jmx?qry=java.lang:type=Memory").openConnection()));
        Assert.assertEquals("GET", conn.getHeaderField(JMXJsonServlet.ACCESS_CONTROL_ALLOW_METHODS));
        Assert.assertNotNull(conn.getHeaderField(JMXJsonServlet.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testTraceRequest() throws IOException {
        URL url = new URL(TestJMXJsonServlet.baseUrl, "/jmx");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestMethod("TRACE");
        Assert.assertEquals("Unexpected response code", SC_METHOD_NOT_ALLOWED, conn.getResponseCode());
    }
}

