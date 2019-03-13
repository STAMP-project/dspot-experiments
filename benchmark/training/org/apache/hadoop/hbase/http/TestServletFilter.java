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
package org.apache.hadoop.hbase.http;


import HttpServer.FILTER_INITIALIZERS_PROPERTY;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MiscTests.class, SmallTests.class })
public class TestServletFilter extends HttpServerFunctionalTest {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestServletFilter.class);

    private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);

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

    public static class ErrorFilter extends TestServletFilter.SimpleFilter {
        @Override
        public void init(FilterConfig arg0) throws ServletException {
            throw new ServletException("Throwing the exception from Filter init");
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
        conf.set(FILTER_INITIALIZERS_PROPERTY, TestServletFilter.ErrorFilter.Initializer.class.getName());
        HttpServer http = HttpServerFunctionalTest.createTestServer(conf);
        try {
            http.start();
            Assert.fail("expecting exception");
        } catch (IOException e) {
            TestServletFilter.assertExceptionContains("Problem starting http server", e);
        }
    }

    /**
     * Similar to the above test case, except that it uses a different API to add the
     * filter. Regression test for HADOOP-8786.
     */
    @Test
    public void testContextSpecificServletFilterWhenInitThrowsException() throws Exception {
        Configuration conf = new Configuration();
        HttpServer http = HttpServerFunctionalTest.createTestServer(conf);
        HttpServer.defineFilter(http.webAppContext, "ErrorFilter", TestServletFilter.ErrorFilter.class.getName(), null, null);
        try {
            http.start();
            Assert.fail("expecting exception");
        } catch (IOException e) {
            TestServletFilter.assertExceptionContains("Unable to initialize WebAppContext", e);
        }
    }
}

