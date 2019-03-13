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
package org.apache.shiro.guice.web;


import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.junit.Assert;
import org.junit.Test;


public class FilterConfigTest {
    @Test
    public void testSimple() throws Exception {
        FilterChainResolver resolver = setupResolver();
        HttpServletResponse response = createNiceMock(HttpServletResponse.class);
        FilterChain chain = createNiceMock(FilterChain.class);
        HttpServletRequest request = createMockRequest("/index.html");
        FilterChain resolved = resolver.getChain(request, response, chain);
        Assert.assertNotNull(resolved);
        verify(request);
    }

    @Test
    public void testWithConfig() throws Exception {
        FilterChainResolver resolver = setupResolver();
        HttpServletResponse response = createNiceMock(HttpServletResponse.class);
        FilterChain chain = createNiceMock(FilterChain.class);
        HttpServletRequest request = createMockRequest("/index2.html");
        FilterChain resolved = resolver.getChain(request, response, chain);
        Assert.assertNotNull(resolved);
        verify(request);
    }
}

