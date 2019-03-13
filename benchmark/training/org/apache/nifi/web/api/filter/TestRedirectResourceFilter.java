/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.web.api.filter;


import java.net.URI;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestRedirectResourceFilter {
    private static Logger logger = LoggerFactory.getLogger(TestRedirectResourceFilter.class);

    @Test
    public void testUnmatched() throws Exception {
        String path = "unmatched";
        String baseUri = "http://example.com:8080/nifi-api/";
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getPath()).thenReturn(path);
        Mockito.when(uriInfo.getBaseUri()).thenReturn(new URI(baseUri));
        Mockito.when(uriInfo.getRequestUri()).thenReturn(new URI((baseUri + path)));
        ContainerRequestContext request = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(request.getUriInfo()).thenReturn(uriInfo);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Assert.fail("setUris shouldn't be called");
                return null;
            }
        }).when(request).setRequestUri(ArgumentMatchers.any(URI.class), ArgumentMatchers.any(URI.class));
        RedirectResourceFilter filter = new RedirectResourceFilter();
        filter.filter(request);
    }

    @Test
    public void testController() throws Exception {
        String path = "controller";
        String baseUri = "http://example.com:8080/nifi-api/";
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getPath()).thenReturn(path);
        Mockito.when(uriInfo.getBaseUri()).thenReturn(new URI(baseUri));
        Mockito.when(uriInfo.getRequestUri()).thenReturn(new URI((baseUri + path)));
        ContainerRequestContext request = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(request.getUriInfo()).thenReturn(uriInfo);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Assert.assertEquals("base uri should be retained", new URI(baseUri), invocation.getArguments()[0]);
                Assert.assertEquals("request uri should be redirected", new URI((baseUri + "site-to-site")), invocation.getArguments()[1]);
                return null;
            }
        }).when(request).setRequestUri(ArgumentMatchers.any(URI.class), ArgumentMatchers.any(URI.class));
        RedirectResourceFilter filter = new RedirectResourceFilter();
        filter.filter(request);
    }

    @Test
    public void testControllerWithParams() throws Exception {
        String path = "controller";
        String baseUri = "http://example.com:8080/nifi-api/";
        String query = "?a=1&b=23&cde=456";
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getPath()).thenReturn(path);
        Mockito.when(uriInfo.getBaseUri()).thenReturn(new URI(baseUri));
        Mockito.when(uriInfo.getRequestUri()).thenReturn(new URI(((baseUri + path) + query)));
        ContainerRequestContext request = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(request.getUriInfo()).thenReturn(uriInfo);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Assert.assertEquals("base uri should be retained", new URI(baseUri), invocation.getArguments()[0]);
                Assert.assertEquals("request uri should be redirected with query parameters", new URI(((baseUri + "site-to-site") + query)), invocation.getArguments()[1]);
                return null;
            }
        }).when(request).setRequestUri(ArgumentMatchers.any(URI.class), ArgumentMatchers.any(URI.class));
        RedirectResourceFilter filter = new RedirectResourceFilter();
        filter.filter(request);
    }
}

