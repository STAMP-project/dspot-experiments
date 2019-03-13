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
package org.apache.hadoop.http;


import HttpServer2.FILTER_INITIALIZER_PROPERTY;
import java.io.IOException;
import java.util.Random;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestServletFilter extends HttpServerFunctionalTest {
    static final Logger LOG = LoggerFactory.getLogger(HttpServer2.class);

    static volatile String uri = null;

    /**
     * A very simple filter which record the uri filtered.
     */
    public static class SimpleFilter implements Filter {
        private FilterConfig filterConfig = null;

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            this.filterConfig = filterConfig;
        }

        @Override
        public void destroy() {
            this.filterConfig = null;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            if ((filterConfig) == null)
                return;

            TestServletFilter.uri = getRequestURI();
            TestServletFilter.LOG.info(("filtering " + (TestServletFilter.uri)));
            chain.doFilter(request, response);
        }

        /**
         * Configuration for the filter
         */
        public static class Initializer extends FilterInitializer {
            public Initializer() {
            }

            @Override
            public void initFilter(FilterContainer container, Configuration conf) {
                container.addFilter("simple", TestServletFilter.SimpleFilter.class.getName(), null);
            }
        }
    }

    @Test
    public void testServletFilter() throws Exception {
        Configuration conf = new Configuration();
        // start a http server with CountingFilter
        conf.set(FILTER_INITIALIZER_PROPERTY, TestServletFilter.SimpleFilter.Initializer.class.getName());
        HttpServer2 http = HttpServerFunctionalTest.createTestServer(conf);
        http.start();
        final String fsckURL = "/fsck";
        final String stacksURL = "/stacks";
        final String ajspURL = "/a.jsp";
        final String logURL = "/logs/a.log";
        final String hadooplogoURL = "/static/hadoop-logo.jpg";
        final String[] urls = new String[]{ fsckURL, stacksURL, ajspURL, logURL, hadooplogoURL };
        final Random ran = new Random();
        final int[] sequence = new int[50];
        // generate a random sequence and update counts
        for (int i = 0; i < (sequence.length); i++) {
            sequence[i] = ran.nextInt(urls.length);
        }
        // access the urls as the sequence
        final String prefix = "http://" + (NetUtils.getHostPortString(http.getConnectorAddress(0)));
        try {
            for (int i = 0; i < (sequence.length); i++) {
                TestServletFilter.access((prefix + (urls[sequence[i]])));
                // make sure everything except fsck get filtered
                if ((sequence[i]) == 0) {
                    Assert.assertEquals(null, TestServletFilter.uri);
                } else {
                    Assert.assertEquals(urls[sequence[i]], TestServletFilter.uri);
                    TestServletFilter.uri = null;
                }
            }
        } finally {
            http.stop();
        }
    }

    public static class ErrorFilter extends TestServletFilter.SimpleFilter {
        static final String EXCEPTION_MESSAGE = "Throwing the exception from Filter init";

        @Override
        public void init(FilterConfig arg0) throws ServletException {
            throw new ServletException(TestServletFilter.ErrorFilter.EXCEPTION_MESSAGE);
        }

        /**
         * Configuration for the filter
         */
        public static class Initializer extends FilterInitializer {
            public Initializer() {
            }

            @Override
            public void initFilter(FilterContainer container, Configuration conf) {
                container.addFilter("simple", TestServletFilter.ErrorFilter.class.getName(), null);
            }
        }
    }

    @Test
    public void testServletFilterWhenInitThrowsException() throws Exception {
        Configuration conf = new Configuration();
        // start a http server with ErrorFilter
        conf.set(FILTER_INITIALIZER_PROPERTY, TestServletFilter.ErrorFilter.Initializer.class.getName());
        HttpServer2 http = HttpServerFunctionalTest.createTestServer(conf);
        try {
            http.start();
            Assert.fail("expecting exception");
        } catch (IOException e) {
            Assert.assertEquals("Problem starting http server", e.getMessage());
            Assert.assertEquals(TestServletFilter.ErrorFilter.EXCEPTION_MESSAGE, e.getCause().getMessage());
        }
    }

    /**
     * Similar to the above test case, except that it uses a different API to add the
     * filter. Regression test for HADOOP-8786.
     */
    @Test
    public void testContextSpecificServletFilterWhenInitThrowsException() throws Exception {
        Configuration conf = new Configuration();
        HttpServer2 http = HttpServerFunctionalTest.createTestServer(conf);
        HttpServer2.defineFilter(http.webAppContext, "ErrorFilter", TestServletFilter.ErrorFilter.class.getName(), null, null);
        try {
            http.start();
            Assert.fail("expecting exception");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Unable to initialize WebAppContext", e);
        }
    }
}

