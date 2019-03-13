/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.web.debug;


import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.FilterChainProxy;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest(Logger.class)
public class DebugFilterTest {
    @Captor
    private ArgumentCaptor<HttpServletRequest> requestCaptor;

    @Captor
    private ArgumentCaptor<String> logCaptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private FilterChainProxy fcp;

    @Mock
    private Logger logger;

    private String requestAttr;

    private DebugFilter filter;

    @Test
    public void doFilterProcessesRequests() throws Exception {
        filter.doFilter(request, response, filterChain);
        Mockito.verify(logger).info(ArgumentMatchers.anyString());
        Mockito.verify(request).setAttribute(requestAttr, Boolean.TRUE);
        Mockito.verify(fcp).doFilter(requestCaptor.capture(), ArgumentMatchers.eq(response), ArgumentMatchers.eq(filterChain));
        assertThat(requestCaptor.getValue().getClass()).isEqualTo(DebugRequestWrapper.class);
        Mockito.verify(request).removeAttribute(requestAttr);
    }

    // SEC-1901
    @Test
    public void doFilterProcessesForwardedRequests() throws Exception {
        Mockito.when(request.getAttribute(requestAttr)).thenReturn(Boolean.TRUE);
        HttpServletRequest request = new DebugRequestWrapper(this.request);
        filter.doFilter(request, response, filterChain);
        Mockito.verify(logger).info(ArgumentMatchers.anyString());
        Mockito.verify(fcp).doFilter(request, response, filterChain);
        Mockito.verify(this.request, Mockito.never()).removeAttribute(requestAttr);
    }

    @Test
    public void doFilterDoesNotWrapWithDebugRequestWrapperAgain() throws Exception {
        Mockito.when(request.getAttribute(requestAttr)).thenReturn(Boolean.TRUE);
        HttpServletRequest fireWalledRequest = new HttpServletRequestWrapper(new DebugRequestWrapper(this.request));
        filter.doFilter(fireWalledRequest, response, filterChain);
        Mockito.verify(fcp).doFilter(fireWalledRequest, response, filterChain);
    }

    @Test
    public void doFilterLogsProperly() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setServletPath("/path");
        request.setPathInfo("/");
        request.addHeader("A", "A Value");
        request.addHeader("A", "Another Value");
        request.addHeader("B", "B Value");
        filter.doFilter(request, response, filterChain);
        Mockito.verify(logger).info(logCaptor.capture());
        assertThat(logCaptor.getValue()).isEqualTo((((((((((((("Request received for GET \'/path/\':\n" + "\n") + request) + "\n") + "\n") + "servletPath:/path\n") + "pathInfo:/\n") + "headers: \n") + "A: A Value, Another Value\n") + "B: B Value\n") + "\n") + "\n") + "Security filter chain: no match"));
    }
}

