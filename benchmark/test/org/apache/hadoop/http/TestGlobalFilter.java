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
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestGlobalFilter extends HttpServerFunctionalTest {
    static final Logger LOG = LoggerFactory.getLogger(HttpServer2.class);

    static final Set<String> RECORDS = new TreeSet<String>();

    /**
     * A very simple filter that records accessed uri's
     */
    public static class RecordingFilter implements Filter {
        private FilterConfig filterConfig = null;

        @Override
        public void init(FilterConfig filterConfig) {
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

            String uri = getRequestURI();
            TestGlobalFilter.LOG.info(("filtering " + uri));
            TestGlobalFilter.RECORDS.add(uri);
            chain.doFilter(request, response);
        }

        /**
         * Configuration for RecordingFilter
         */
        public static class Initializer extends FilterInitializer {
            public Initializer() {
            }

            @Override
            public void initFilter(FilterContainer container, Configuration conf) {
                container.addGlobalFilter("recording", TestGlobalFilter.RecordingFilter.class.getName(), null);
            }
        }
    }

    @Test
    public void testServletFilter() throws Exception {
        Configuration conf = new Configuration();
        // start a http server with CountingFilter
        conf.set(FILTER_INITIALIZER_PROPERTY, TestGlobalFilter.RecordingFilter.Initializer.class.getName());
        HttpServer2 http = HttpServerFunctionalTest.createTestServer(conf);
        http.start();
        final String fsckURL = "/fsck";
        final String stacksURL = "/stacks";
        final String ajspURL = "/a.jsp";
        final String listPathsURL = "/listPaths";
        final String dataURL = "/data";
        final String streamFile = "/streamFile";
        final String rootURL = "/";
        final String allURL = "/*";
        final String outURL = "/static/a.out";
        final String logURL = "/logs/a.log";
        final String[] urls = new String[]{ fsckURL, stacksURL, ajspURL, listPathsURL, dataURL, streamFile, rootURL, allURL, outURL, logURL };
        // access the urls
        final String prefix = "http://" + (NetUtils.getHostPortString(http.getConnectorAddress(0)));
        try {
            for (int i = 0; i < (urls.length); i++) {
                TestGlobalFilter.access((prefix + (urls[i])));
            }
        } finally {
            http.stop();
        }
        TestGlobalFilter.LOG.info(("RECORDS = " + (TestGlobalFilter.RECORDS)));
        // verify records
        for (int i = 0; i < (urls.length); i++) {
            Assert.assertTrue(TestGlobalFilter.RECORDS.remove(urls[i]));
        }
        Assert.assertTrue(TestGlobalFilter.RECORDS.isEmpty());
    }
}

