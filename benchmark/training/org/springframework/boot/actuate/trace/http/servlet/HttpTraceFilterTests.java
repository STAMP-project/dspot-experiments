/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.actuate.trace.http.servlet;


import java.io.IOException;
import java.security.Principal;
import java.util.EnumSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.trace.http.HttpExchangeTracer;
import org.springframework.boot.actuate.trace.http.HttpTrace.Session;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.actuate.trace.http.Include;
import org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Tests for {@link HttpTraceFilter}.
 *
 * @author Dave Syer
 * @author Wallace Wadge
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Venil Noronha
 * @author Stephane Nicoll
 * @author Madhura Bhave
 */
public class HttpTraceFilterTests {
    private final InMemoryHttpTraceRepository repository = new InMemoryHttpTraceRepository();

    private final HttpExchangeTracer tracer = new HttpExchangeTracer(EnumSet.allOf(Include.class));

    private final HttpTraceFilter filter = new HttpTraceFilter(this.repository, this.tracer);

    @Test
    public void filterTracesExchange() throws IOException, ServletException {
        this.filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());
        assertThat(this.repository.findAll()).hasSize(1);
    }

    @Test
    public void filterCapturesSessionId() throws IOException, ServletException {
        this.filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                req.getSession(true);
            }
        }));
        assertThat(this.repository.findAll()).hasSize(1);
        Session session = this.repository.findAll().get(0).getSession();
        assertThat(session).isNotNull();
        assertThat(session.getId()).isNotNull();
    }

    @Test
    public void filterCapturesPrincipal() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Principal principal = Mockito.mock(Principal.class);
        BDDMockito.given(principal.getName()).willReturn("alice");
        request.setUserPrincipal(principal);
        this.filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        assertThat(this.repository.findAll()).hasSize(1);
        org.springframework.boot.actuate.trace.http.HttpTrace.Principal tracedPrincipal = this.repository.findAll().get(0).getPrincipal();
        assertThat(tracedPrincipal).isNotNull();
        assertThat(tracedPrincipal.getName()).isEqualTo("alice");
    }

    @Test
    public void statusIsAssumedToBe500WhenChainFails() throws IOException, ServletException {
        assertThatIOException().isThrownBy(() -> this.filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                throw new IOException();
            }
        }))).satisfies(( ex) -> {
            assertThat(this.repository.findAll()).hasSize(1);
            assertThat(this.repository.findAll().get(0).getResponse().getStatus()).isEqualTo(500);
        });
    }

    @Test
    public void filterRejectsInvalidRequests() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("<script>alert(document.domain)</script>");
        this.filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        assertThat(this.repository.findAll()).hasSize(0);
    }
}

