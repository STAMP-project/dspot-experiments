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
package org.apache.shiro.web.servlet;


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Assert;
import org.junit.Test;

import static OncePerRequestFilter.ALREADY_FILTERED_SUFFIX;


/**
 * Unit tests for the {@link OncePerRequestFilter} implementation.
 *
 * @since 1.2
 */
public class OncePerRequestFilterTest {
    private static final boolean[] FILTERED = new boolean[1];

    private static final String NAME = "oncePerRequestFilter";

    private static final String ATTR_NAME = (OncePerRequestFilterTest.NAME) + (ALREADY_FILTERED_SUFFIX);

    private OncePerRequestFilter filter;

    private FilterChain chain;

    private ServletRequest request;

    private ServletResponse response;

    /**
     * Test asserting <a href="https://issues.apache.org/jira/browse/SHIRO-221">SHIRO-221<a/>.
     */
    @SuppressWarnings({ "JavaDoc" })
    @Test
    public void testEnabled() throws IOException, ServletException {
        expect(request.getAttribute(OncePerRequestFilterTest.ATTR_NAME)).andReturn(null).anyTimes();
        replay(request);
        filter.doFilter(request, response, chain);
        verify(request);
        Assert.assertTrue("Filter should have executed", OncePerRequestFilterTest.FILTERED[0]);
    }

    /**
     * Test asserting <a href="https://issues.apache.org/jira/browse/SHIRO-221">SHIRO-221<a/>.
     */
    @SuppressWarnings({ "JavaDoc" })
    @Test
    public void testDisabled() throws IOException, ServletException {
        filter.setEnabled(false);// test disabled

        expect(request.getAttribute(OncePerRequestFilterTest.ATTR_NAME)).andReturn(null).anyTimes();
        replay(request);
        filter.doFilter(request, response, chain);
        verify(request);
        Assert.assertFalse("Filter should NOT have executed", OncePerRequestFilterTest.FILTERED[0]);
    }
}

