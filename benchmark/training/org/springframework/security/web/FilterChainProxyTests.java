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
package org.springframework.security.web;


import java.util.Collections;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 *
 *
 * @author Luke Taylor
 * @author Rob Winch
 */
public class FilterChainProxyTests {
    private FilterChainProxy fcp;

    private RequestMatcher matcher;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private FilterChain chain;

    private Filter filter;

    @Test
    public void toStringCallSucceeds() throws Exception {
        fcp.afterPropertiesSet();
        fcp.toString();
    }

    @Test
    public void securityFilterChainIsNotInvokedIfMatchFails() throws Exception {
        Mockito.when(matcher.matches(ArgumentMatchers.any(HttpServletRequest.class))).thenReturn(false);
        fcp.doFilter(request, response, chain);
        assertThat(fcp.getFilterChains()).hasSize(1);
        assertThat(fcp.getFilterChains().get(0).getFilters().get(0)).isSameAs(filter);
        Mockito.verifyZeroInteractions(filter);
        // The actual filter chain should be invoked though
        Mockito.verify(chain).doFilter(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void originalChainIsInvokedAfterSecurityChainIfMatchSucceeds() throws Exception {
        Mockito.when(matcher.matches(ArgumentMatchers.any(HttpServletRequest.class))).thenReturn(true);
        fcp.doFilter(request, response, chain);
        Mockito.verify(filter).doFilter(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(FilterChain.class));
        Mockito.verify(chain).doFilter(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void originalFilterChainIsInvokedIfMatchingSecurityChainIsEmpty() throws Exception {
        List<Filter> noFilters = Collections.emptyList();
        fcp = new FilterChainProxy(new DefaultSecurityFilterChain(matcher, noFilters));
        Mockito.when(matcher.matches(ArgumentMatchers.any(HttpServletRequest.class))).thenReturn(true);
        fcp.doFilter(request, response, chain);
        Mockito.verify(chain).doFilter(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void requestIsWrappedForMatchingAndFilteringWhenMatchIsFound() throws Exception {
        Mockito.when(matcher.matches(ArgumentMatchers.any())).thenReturn(true);
        fcp.doFilter(request, response, chain);
        Mockito.verify(matcher).matches(ArgumentMatchers.any(FirewalledRequest.class));
        Mockito.verify(filter).doFilter(ArgumentMatchers.any(FirewalledRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(FilterChain.class));
        Mockito.verify(chain).doFilter(ArgumentMatchers.any(), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void requestIsWrappedForMatchingAndFilteringWhenMatchIsNotFound() throws Exception {
        Mockito.when(matcher.matches(ArgumentMatchers.any(HttpServletRequest.class))).thenReturn(false);
        fcp.doFilter(request, response, chain);
        Mockito.verify(matcher).matches(ArgumentMatchers.any(FirewalledRequest.class));
        Mockito.verifyZeroInteractions(filter);
        Mockito.verify(chain).doFilter(ArgumentMatchers.any(FirewalledRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void wrapperIsResetWhenNoMatchingFilters() throws Exception {
        HttpFirewall fw = Mockito.mock(HttpFirewall.class);
        FirewalledRequest fwr = Mockito.mock(FirewalledRequest.class);
        Mockito.when(fwr.getRequestURI()).thenReturn("/");
        Mockito.when(fwr.getContextPath()).thenReturn("");
        fcp.setFirewall(fw);
        Mockito.when(fw.getFirewalledRequest(request)).thenReturn(fwr);
        Mockito.when(matcher.matches(ArgumentMatchers.any(HttpServletRequest.class))).thenReturn(false);
        fcp.doFilter(request, response, chain);
        Mockito.verify(fwr).reset();
    }

    // SEC-1639
    @Test
    public void bothWrappersAreResetWithNestedFcps() throws Exception {
        HttpFirewall fw = Mockito.mock(HttpFirewall.class);
        FilterChainProxy firstFcp = new FilterChainProxy(new DefaultSecurityFilterChain(matcher, fcp));
        firstFcp.setFirewall(fw);
        fcp.setFirewall(fw);
        FirewalledRequest firstFwr = Mockito.mock(FirewalledRequest.class, "firstFwr");
        Mockito.when(firstFwr.getRequestURI()).thenReturn("/");
        Mockito.when(firstFwr.getContextPath()).thenReturn("");
        FirewalledRequest fwr = Mockito.mock(FirewalledRequest.class, "fwr");
        Mockito.when(fwr.getRequestURI()).thenReturn("/");
        Mockito.when(fwr.getContextPath()).thenReturn("");
        Mockito.when(fw.getFirewalledRequest(request)).thenReturn(firstFwr);
        Mockito.when(fw.getFirewalledRequest(firstFwr)).thenReturn(fwr);
        Mockito.when(fwr.getRequest()).thenReturn(firstFwr);
        Mockito.when(firstFwr.getRequest()).thenReturn(request);
        Mockito.when(matcher.matches(ArgumentMatchers.any())).thenReturn(true);
        firstFcp.doFilter(request, response, chain);
        Mockito.verify(firstFwr).reset();
        Mockito.verify(fwr).reset();
    }

    @Test
    public void doFilterClearsSecurityContextHolder() throws Exception {
        Mockito.when(matcher.matches(ArgumentMatchers.any(HttpServletRequest.class))).thenReturn(true);
        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock inv) throws Throwable {
                SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("username", "password"));
                return null;
            }
        }).when(filter).doFilter(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(FilterChain.class));
        fcp.doFilter(request, response, chain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void doFilterClearsSecurityContextHolderWithException() throws Exception {
        Mockito.when(matcher.matches(ArgumentMatchers.any(HttpServletRequest.class))).thenReturn(true);
        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock inv) throws Throwable {
                SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("username", "password"));
                throw new ServletException("oops");
            }
        }).when(filter).doFilter(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(FilterChain.class));
        try {
            fcp.doFilter(request, response, chain);
            fail("Expected Exception");
        } catch (ServletException success) {
        }
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // SEC-2027
    @Test
    public void doFilterClearsSecurityContextHolderOnceOnForwards() throws Exception {
        final FilterChain innerChain = Mockito.mock(FilterChain.class);
        Mockito.when(matcher.matches(ArgumentMatchers.any(HttpServletRequest.class))).thenReturn(true);
        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock inv) throws Throwable {
                TestingAuthenticationToken expected = new TestingAuthenticationToken("username", "password");
                SecurityContextHolder.getContext().setAuthentication(expected);
                Mockito.doAnswer(new Answer<Object>() {
                    public Object answer(InvocationOnMock inv) throws Throwable {
                        innerChain.doFilter(request, response);
                        return null;
                    }
                }).when(filter).doFilter(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(FilterChain.class));
                fcp.doFilter(request, response, innerChain);
                assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(expected);
                return null;
            }
        }).when(filter).doFilter(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(FilterChain.class));
        fcp.doFilter(request, response, chain);
        Mockito.verify(innerChain).doFilter(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}

