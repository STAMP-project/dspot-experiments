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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.web.filter;


import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the {@link PathMatchingFilter} implementation.
 */
public class PathMatchingFilterTest {
    private static final String CONTEXT_PATH = "/";

    private static final String ENABLED_PATH = (PathMatchingFilterTest.CONTEXT_PATH) + "enabled";

    private static final String DISABLED_PATH = (PathMatchingFilterTest.CONTEXT_PATH) + "disabled";

    HttpServletRequest request;

    ServletResponse response;

    PathMatchingFilter filter;

    /**
     * Test asserting <a href="https://issues.apache.org/jira/browse/SHIRO-221">SHIRO-221<a/>.
     */
    @SuppressWarnings({ "JavaDoc" })
    @Test
    public void testDisabledBasedOnPath() throws Exception {
        filter.processPathConfig(PathMatchingFilterTest.DISABLED_PATH, null);
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);
        ServletResponse response = createNiceMock(ServletResponse.class);
        expect(request.getContextPath()).andReturn(PathMatchingFilterTest.CONTEXT_PATH).anyTimes();
        expect(request.getRequestURI()).andReturn(PathMatchingFilterTest.DISABLED_PATH).anyTimes();
        replay(request);
        boolean continueFilterChain = filter.preHandle(request, response);
        Assert.assertTrue("FilterChain should continue.", continueFilterChain);
        verify(request);
    }

    /**
     * Test asserting <a href="https://issues.apache.org/jira/browse/SHIRO-221">SHIRO-221<a/>.
     */
    @SuppressWarnings({ "JavaDoc" })
    @Test
    public void testEnabled() throws Exception {
        // Configure the filter to reflect 2 configured paths.  This test will simulate a request to the
        // enabled path
        filter.processPathConfig(PathMatchingFilterTest.DISABLED_PATH, null);
        filter.processPathConfig(PathMatchingFilterTest.ENABLED_PATH, null);
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);
        ServletResponse response = createNiceMock(ServletResponse.class);
        expect(request.getContextPath()).andReturn(PathMatchingFilterTest.CONTEXT_PATH).anyTimes();
        expect(request.getRequestURI()).andReturn(PathMatchingFilterTest.ENABLED_PATH).anyTimes();
        replay(request);
        boolean continueFilterChain = filter.preHandle(request, response);
        Assert.assertFalse("FilterChain should NOT continue.", continueFilterChain);
        verify(request);
    }
}

