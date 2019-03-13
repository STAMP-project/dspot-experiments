/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.web.context.request;


import WebApplicationContext.SCOPE_APPLICATION;
import WebApplicationContext.SCOPE_REQUEST;
import WebApplicationContext.SCOPE_SESSION;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.tests.sample.beans.DerivedTestBean;
import org.springframework.web.context.ContextCleanupListener;
import org.springframework.web.context.WebApplicationContext;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class WebApplicationContextScopeTests {
    private static final String NAME = "scoped";

    @Test
    public void testRequestScope() {
        WebApplicationContext ac = initApplicationContext(SCOPE_REQUEST);
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        try {
            Assert.assertNull(request.getAttribute(WebApplicationContextScopeTests.NAME));
            DerivedTestBean bean = ac.getBean(WebApplicationContextScopeTests.NAME, DerivedTestBean.class);
            Assert.assertSame(bean, request.getAttribute(WebApplicationContextScopeTests.NAME));
            Assert.assertSame(bean, ac.getBean(WebApplicationContextScopeTests.NAME));
            requestAttributes.requestCompleted();
            Assert.assertTrue(bean.wasDestroyed());
        } finally {
            RequestContextHolder.setRequestAttributes(null);
        }
    }

    @Test
    public void testSessionScope() {
        WebApplicationContext ac = initApplicationContext(SCOPE_SESSION);
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        try {
            Assert.assertNull(request.getSession().getAttribute(WebApplicationContextScopeTests.NAME));
            DerivedTestBean bean = ac.getBean(WebApplicationContextScopeTests.NAME, DerivedTestBean.class);
            Assert.assertSame(bean, request.getSession().getAttribute(WebApplicationContextScopeTests.NAME));
            Assert.assertSame(bean, ac.getBean(WebApplicationContextScopeTests.NAME));
            request.getSession().invalidate();
            Assert.assertTrue(bean.wasDestroyed());
        } finally {
            RequestContextHolder.setRequestAttributes(null);
        }
    }

    @Test
    public void testApplicationScope() {
        WebApplicationContext ac = initApplicationContext(SCOPE_APPLICATION);
        Assert.assertNull(ac.getServletContext().getAttribute(WebApplicationContextScopeTests.NAME));
        DerivedTestBean bean = ac.getBean(WebApplicationContextScopeTests.NAME, DerivedTestBean.class);
        Assert.assertSame(bean, ac.getServletContext().getAttribute(WebApplicationContextScopeTests.NAME));
        Assert.assertSame(bean, ac.getBean(WebApplicationContextScopeTests.NAME));
        new ContextCleanupListener().contextDestroyed(new javax.servlet.ServletContextEvent(ac.getServletContext()));
        Assert.assertTrue(bean.wasDestroyed());
    }
}

