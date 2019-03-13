/**
 * Copyright 2014-2017 the original author or authors.
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
package org.springframework.session.web.http;


import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class OncePerRequestFilterTests {
    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private MockFilterChain chain;

    private OncePerRequestFilter filter;

    private HttpServlet servlet;

    private List<OncePerRequestFilter> invocations;

    @Test
    public void doFilterOnce() throws IOException, ServletException {
        this.filter.doFilter(this.request, this.response, this.chain);
        assertThat(this.invocations).containsOnly(this.filter);
    }

    @Test
    public void doFilterMultiOnlyIvokesOnce() throws IOException, ServletException {
        this.filter.doFilter(this.request, this.response, new MockFilterChain(this.servlet, this.filter));
        assertThat(this.invocations).containsOnly(this.filter);
    }

    @Test
    public void doFilterOtherSubclassInvoked() throws IOException, ServletException {
        OncePerRequestFilter filter2 = new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
                OncePerRequestFilterTests.this.invocations.add(this);
                filterChain.doFilter(request, response);
            }
        };
        this.filter.doFilter(this.request, this.response, new MockFilterChain(this.servlet, filter2));
        assertThat(this.invocations).containsOnly(this.filter, filter2);
    }
}

