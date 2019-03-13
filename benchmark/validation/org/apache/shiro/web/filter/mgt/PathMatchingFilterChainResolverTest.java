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
package org.apache.shiro.web.filter.mgt;


import WebUtils.INCLUDE_CONTEXT_PATH_ATTRIBUTE;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.web.WebTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver}.
 *
 * @since 1.0
 */
public class PathMatchingFilterChainResolverTest extends WebTest {
    private PathMatchingFilterChainResolver resolver;

    @Test
    public void testNewInstance() {
        Assert.assertNotNull(resolver.getPathMatcher());
        Assert.assertTrue(((resolver.getPathMatcher()) instanceof AntPathMatcher));
        Assert.assertNotNull(resolver.getFilterChainManager());
        Assert.assertTrue(((resolver.getFilterChainManager()) instanceof DefaultFilterChainManager));
    }

    @Test
    public void testNewInstanceWithFilterConfig() {
        FilterConfig mock = createNiceMockFilterConfig();
        replay(mock);
        resolver = new PathMatchingFilterChainResolver(mock);
        Assert.assertNotNull(resolver.getPathMatcher());
        Assert.assertTrue(((resolver.getPathMatcher()) instanceof AntPathMatcher));
        Assert.assertNotNull(resolver.getFilterChainManager());
        Assert.assertTrue(((resolver.getFilterChainManager()) instanceof DefaultFilterChainManager));
        Assert.assertEquals(getFilterConfig(), mock);
        verify(mock);
    }

    @Test
    public void testSetters() {
        resolver.setPathMatcher(new AntPathMatcher());
        Assert.assertNotNull(resolver.getPathMatcher());
        Assert.assertTrue(((resolver.getPathMatcher()) instanceof AntPathMatcher));
        resolver.setFilterChainManager(new DefaultFilterChainManager());
        Assert.assertNotNull(resolver.getFilterChainManager());
        Assert.assertTrue(((resolver.getFilterChainManager()) instanceof DefaultFilterChainManager));
    }

    @Test
    public void testGetChainsWithoutChains() {
        ServletRequest request = createNiceMock(HttpServletRequest.class);
        ServletResponse response = createNiceMock(HttpServletResponse.class);
        FilterChain chain = createNiceMock(FilterChain.class);
        FilterChain resolved = resolver.getChain(request, response, chain);
        Assert.assertNull(resolved);
    }

    @Test
    public void testGetChainsWithMatch() {
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);
        HttpServletResponse response = createNiceMock(HttpServletResponse.class);
        FilterChain chain = createNiceMock(FilterChain.class);
        // ensure at least one chain is defined:
        resolver.getFilterChainManager().addToChain("/index.html", "authcBasic");
        expect(request.getAttribute(INCLUDE_CONTEXT_PATH_ATTRIBUTE)).andReturn(null).anyTimes();
        expect(request.getContextPath()).andReturn("");
        expect(request.getRequestURI()).andReturn("/index.html");
        replay(request);
        FilterChain resolved = resolver.getChain(request, response, chain);
        Assert.assertNotNull(resolved);
        verify(request);
    }

    @Test
    public void testPathTraversalWithDot() {
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);
        HttpServletResponse response = createNiceMock(HttpServletResponse.class);
        FilterChain chain = createNiceMock(FilterChain.class);
        // ensure at least one chain is defined:
        resolver.getFilterChainManager().addToChain("/index.html", "authcBasic");
        expect(request.getAttribute(INCLUDE_CONTEXT_PATH_ATTRIBUTE)).andReturn(null).anyTimes();
        expect(request.getContextPath()).andReturn("");
        expect(request.getRequestURI()).andReturn("/./index.html");
        replay(request);
        FilterChain resolved = resolver.getChain(request, response, chain);
        Assert.assertNotNull(resolved);
        verify(request);
    }

    @Test
    public void testPathTraversalWithDotDot() {
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);
        HttpServletResponse response = createNiceMock(HttpServletResponse.class);
        FilterChain chain = createNiceMock(FilterChain.class);
        // ensure at least one chain is defined:
        resolver.getFilterChainManager().addToChain("/index.html", "authcBasic");
        expect(request.getAttribute(INCLUDE_CONTEXT_PATH_ATTRIBUTE)).andReturn(null).anyTimes();
        expect(request.getContextPath()).andReturn("");
        expect(request.getRequestURI()).andReturn("/public/../index.html");
        replay(request);
        FilterChain resolved = resolver.getChain(request, response, chain);
        Assert.assertNotNull(resolved);
        verify(request);
    }

    @Test
    public void testGetChainsWithoutMatch() {
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);
        HttpServletResponse response = createNiceMock(HttpServletResponse.class);
        FilterChain chain = createNiceMock(FilterChain.class);
        // ensure at least one chain is defined:
        resolver.getFilterChainManager().addToChain("/index.html", "authcBasic");
        expect(request.getAttribute(INCLUDE_CONTEXT_PATH_ATTRIBUTE)).andReturn(null).anyTimes();
        expect(request.getContextPath()).andReturn("");
        expect(request.getRequestURI()).andReturn("/");
        replay(request);
        FilterChain resolved = resolver.getChain(request, response, chain);
        Assert.assertNull(resolved);
        verify(request);
    }
}

