/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.rackhack;


import DelegatingListener.DELEGATE_SERVLET;
import com.thoughtworks.go.server.util.ServletRequest;
import java.io.IOException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;


public class DelegatingServletTest {
    private ServletRequest servletRequestWrapper;

    private HttpServletRequest httpServletRequest;

    @Test
    public void shouldDelegateToTheGivenServlet() throws IOException, ServletException {
        MockServletContext ctx = new MockServletContext();
        ctx.addInitParameter(DELEGATE_SERVLET, DummyServlet.class.getCanonicalName());
        ServletContextEvent evt = new ServletContextEvent(ctx);
        DelegatingListener listener = new DelegatingListener();
        listener.contextInitialized(evt);
        Assert.assertThat(((DummyServlet) (ctx.getAttribute(DELEGATE_SERVLET))), Matchers.isA(DummyServlet.class));
        DelegatingServlet servlet = new DelegatingServlet();
        servlet.init(new org.springframework.mock.web.MockServletConfig(ctx));
        servlet.service(httpServletRequest, new MockHttpServletResponse());
        Mockito.verify(servletRequestWrapper).setRequestURI("/go/stuff/action");
    }
}

