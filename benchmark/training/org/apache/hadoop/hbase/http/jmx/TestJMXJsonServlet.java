/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.http.jmx;


import HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import HttpServletResponse.SC_OK;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.http.HttpServer;
import org.apache.hadoop.hbase.http.HttpServerFunctionalTest;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MiscTests.class, SmallTests.class })
public class TestJMXJsonServlet extends HttpServerFunctionalTest {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestJMXJsonServlet.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestJMXJsonServlet.class);

    private static HttpServer server;

    private static URL baseUrl;

    @Test
    public void testQuery() throws Exception {
        String result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx?qry=java.lang:type=Runtime"));
        TestJMXJsonServlet.LOG.info(("/jmx?qry=java.lang:type=Runtime RESULT: " + result));
        TestJMXJsonServlet.assertReFind("\"name\"\\s*:\\s*\"java.lang:type=Runtime\"", result);
        TestJMXJsonServlet.assertReFind("\"modelerType\"", result);
        result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx?qry=java.lang:type=Memory"));
        TestJMXJsonServlet.LOG.info(("/jmx?qry=java.lang:type=Memory RESULT: " + result));
        TestJMXJsonServlet.assertReFind("\"name\"\\s*:\\s*\"java.lang:type=Memory\"", result);
        TestJMXJsonServlet.assertReFind("\"modelerType\"", result);
        result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx"));
        TestJMXJsonServlet.LOG.info(("/jmx RESULT: " + result));
        TestJMXJsonServlet.assertReFind("\"name\"\\s*:\\s*\"java.lang:type=Memory\"", result);
        // test to get an attribute of a mbean
        result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx?get=java.lang:type=Memory::HeapMemoryUsage"));
        TestJMXJsonServlet.LOG.info(("/jmx RESULT: " + result));
        TestJMXJsonServlet.assertReFind("\"name\"\\s*:\\s*\"java.lang:type=Memory\"", result);
        TestJMXJsonServlet.assertReFind("\"committed\"\\s*:", result);
        // negative test to get an attribute of a mbean
        result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx?get=java.lang:type=Memory::"));
        TestJMXJsonServlet.LOG.info(("/jmx RESULT: " + result));
        TestJMXJsonServlet.assertReFind("\"ERROR\"", result);
        // test to get JSONP result
        result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx?qry=java.lang:type=Memory&callback=mycallback1"));
        TestJMXJsonServlet.LOG.info(("/jmx?qry=java.lang:type=Memory&callback=mycallback RESULT: " + result));
        TestJMXJsonServlet.assertReFind("^mycallback1\\(\\{", result);
        TestJMXJsonServlet.assertReFind("\\}\\);$", result);
        // negative test to get an attribute of a mbean as JSONP
        result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx?get=java.lang:type=Memory::&callback=mycallback2"));
        TestJMXJsonServlet.LOG.info(("/jmx RESULT: " + result));
        TestJMXJsonServlet.assertReFind("^mycallback2\\(\\{", result);
        TestJMXJsonServlet.assertReFind("\"ERROR\"", result);
        TestJMXJsonServlet.assertReFind("\\}\\);$", result);
        // test to get an attribute of a mbean as JSONP
        result = HttpServerFunctionalTest.readOutput(new URL(TestJMXJsonServlet.baseUrl, "/jmx?get=java.lang:type=Memory::HeapMemoryUsage&callback=mycallback3"));
        TestJMXJsonServlet.LOG.info(("/jmx RESULT: " + result));
        TestJMXJsonServlet.assertReFind("^mycallback3\\(\\{", result);
        TestJMXJsonServlet.assertReFind("\"name\"\\s*:\\s*\"java.lang:type=Memory\"", result);
        TestJMXJsonServlet.assertReFind("\"committed\"\\s*:", result);
        TestJMXJsonServlet.assertReFind("\\}\\);$", result);
    }

    @Test
    public void testDisallowedJSONPCallback() throws Exception {
        String callback = "function(){alert('bigproblems!')};foo";
        URL url = new URL(TestJMXJsonServlet.baseUrl, ("/jmx?qry=java.lang:type=Memory&callback=" + (URLEncoder.encode(callback, "UTF-8"))));
        HttpURLConnection cnxn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(SC_INTERNAL_SERVER_ERROR, cnxn.getResponseCode());
    }

    @Test
    public void testUnderscoresInJSONPCallback() throws Exception {
        String callback = "my_function";
        URL url = new URL(TestJMXJsonServlet.baseUrl, ("/jmx?qry=java.lang:type=Memory&callback=" + (URLEncoder.encode(callback, "UTF-8"))));
        HttpURLConnection cnxn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(SC_OK, cnxn.getResponseCode());
    }
}

