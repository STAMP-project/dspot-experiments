/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.web.filter;


import WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockFilterConfig;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Rob Winch
 * @since 08.05.2005
 */
public class DelegatingFilterProxyTests {
    @Test
    public void testDelegatingFilterProxy() throws IOException, ServletException {
        ServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.registerSingleton("targetFilter", DelegatingFilterProxyTests.MockFilter.class);
        wac.refresh();
        sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        DelegatingFilterProxyTests.MockFilter targetFilter = ((DelegatingFilterProxyTests.MockFilter) (wac.getBean("targetFilter")));
        MockFilterConfig proxyConfig = new MockFilterConfig(sc);
        proxyConfig.addInitParameter("targetBeanName", "targetFilter");
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy();
        filterProxy.init(proxyConfig);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);
        Assert.assertNull(targetFilter.filterConfig);
        Assert.assertEquals(Boolean.TRUE, request.getAttribute("called"));
        filterProxy.destroy();
        Assert.assertNull(targetFilter.filterConfig);
    }

    @Test
    public void testDelegatingFilterProxyAndCustomContextAttribute() throws IOException, ServletException {
        ServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.registerSingleton("targetFilter", DelegatingFilterProxyTests.MockFilter.class);
        wac.refresh();
        sc.setAttribute("CUSTOM_ATTR", wac);
        DelegatingFilterProxyTests.MockFilter targetFilter = ((DelegatingFilterProxyTests.MockFilter) (wac.getBean("targetFilter")));
        MockFilterConfig proxyConfig = new MockFilterConfig(sc);
        proxyConfig.addInitParameter("targetBeanName", "targetFilter");
        proxyConfig.addInitParameter("contextAttribute", "CUSTOM_ATTR");
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy();
        filterProxy.init(proxyConfig);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);
        Assert.assertNull(targetFilter.filterConfig);
        Assert.assertEquals(Boolean.TRUE, request.getAttribute("called"));
        filterProxy.destroy();
        Assert.assertNull(targetFilter.filterConfig);
    }

    @Test
    public void testDelegatingFilterProxyWithFilterDelegateInstance() throws IOException, ServletException {
        DelegatingFilterProxyTests.MockFilter targetFilter = new DelegatingFilterProxyTests.MockFilter();
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy(targetFilter);
        filterProxy.init(new MockFilterConfig(new MockServletContext()));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);
        Assert.assertNull(targetFilter.filterConfig);
        Assert.assertEquals(Boolean.TRUE, request.getAttribute("called"));
        filterProxy.destroy();
        Assert.assertNull(targetFilter.filterConfig);
    }

    @Test
    public void testDelegatingFilterProxyWithTargetBeanName() throws IOException, ServletException {
        MockServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.registerSingleton("targetFilter", DelegatingFilterProxyTests.MockFilter.class);
        wac.refresh();
        sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        DelegatingFilterProxyTests.MockFilter targetFilter = ((DelegatingFilterProxyTests.MockFilter) (wac.getBean("targetFilter")));
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy("targetFilter");
        filterProxy.init(new MockFilterConfig(sc));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);
        Assert.assertNull(targetFilter.filterConfig);
        Assert.assertEquals(Boolean.TRUE, request.getAttribute("called"));
        filterProxy.destroy();
        Assert.assertNull(targetFilter.filterConfig);
    }

    @Test
    public void testDelegatingFilterProxyWithTargetBeanNameAndNotYetRefreshedApplicationContext() throws IOException, ServletException {
        MockServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.registerSingleton("targetFilter", DelegatingFilterProxyTests.MockFilter.class);
        // wac.refresh();
        // note that the context is not set as the ROOT attribute in the ServletContext!
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy("targetFilter", wac);
        filterProxy.init(new MockFilterConfig(sc));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);
        DelegatingFilterProxyTests.MockFilter targetFilter = ((DelegatingFilterProxyTests.MockFilter) (wac.getBean("targetFilter")));
        Assert.assertNull(targetFilter.filterConfig);
        Assert.assertEquals(Boolean.TRUE, request.getAttribute("called"));
        filterProxy.destroy();
        Assert.assertNull(targetFilter.filterConfig);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelegatingFilterProxyWithTargetBeanNameAndNoApplicationContext() throws IOException, ServletException {
        MockServletContext sc = new MockServletContext();
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy("targetFilter", null);
        filterProxy.init(new MockFilterConfig(sc));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);// throws

    }

    @Test
    public void testDelegatingFilterProxyWithFilterName() throws IOException, ServletException {
        ServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.registerSingleton("targetFilter", DelegatingFilterProxyTests.MockFilter.class);
        wac.refresh();
        sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        DelegatingFilterProxyTests.MockFilter targetFilter = ((DelegatingFilterProxyTests.MockFilter) (wac.getBean("targetFilter")));
        MockFilterConfig proxyConfig = new MockFilterConfig(sc, "targetFilter");
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy();
        filterProxy.init(proxyConfig);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);
        Assert.assertNull(targetFilter.filterConfig);
        Assert.assertEquals(Boolean.TRUE, request.getAttribute("called"));
        filterProxy.destroy();
        Assert.assertNull(targetFilter.filterConfig);
    }

    @Test
    public void testDelegatingFilterProxyWithLazyContextStartup() throws IOException, ServletException {
        ServletContext sc = new MockServletContext();
        MockFilterConfig proxyConfig = new MockFilterConfig(sc);
        proxyConfig.addInitParameter("targetBeanName", "targetFilter");
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy();
        filterProxy.init(proxyConfig);
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.registerSingleton("targetFilter", DelegatingFilterProxyTests.MockFilter.class);
        wac.refresh();
        sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        DelegatingFilterProxyTests.MockFilter targetFilter = ((DelegatingFilterProxyTests.MockFilter) (wac.getBean("targetFilter")));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);
        Assert.assertNull(targetFilter.filterConfig);
        Assert.assertEquals(Boolean.TRUE, request.getAttribute("called"));
        filterProxy.destroy();
        Assert.assertNull(targetFilter.filterConfig);
    }

    @Test
    public void testDelegatingFilterProxyWithTargetFilterLifecycle() throws IOException, ServletException {
        ServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.registerSingleton("targetFilter", DelegatingFilterProxyTests.MockFilter.class);
        wac.refresh();
        sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        DelegatingFilterProxyTests.MockFilter targetFilter = ((DelegatingFilterProxyTests.MockFilter) (wac.getBean("targetFilter")));
        MockFilterConfig proxyConfig = new MockFilterConfig(sc);
        proxyConfig.addInitParameter("targetBeanName", "targetFilter");
        proxyConfig.addInitParameter("targetFilterLifecycle", "true");
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy();
        filterProxy.init(proxyConfig);
        Assert.assertEquals(proxyConfig, targetFilter.filterConfig);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);
        Assert.assertEquals(proxyConfig, targetFilter.filterConfig);
        Assert.assertEquals(Boolean.TRUE, request.getAttribute("called"));
        filterProxy.destroy();
        Assert.assertNull(targetFilter.filterConfig);
    }

    @Test
    public void testDelegatingFilterProxyWithFrameworkServletContext() throws IOException, ServletException {
        ServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.registerSingleton("targetFilter", DelegatingFilterProxyTests.MockFilter.class);
        wac.refresh();
        sc.setAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher", wac);
        DelegatingFilterProxyTests.MockFilter targetFilter = ((DelegatingFilterProxyTests.MockFilter) (wac.getBean("targetFilter")));
        MockFilterConfig proxyConfig = new MockFilterConfig(sc);
        proxyConfig.addInitParameter("targetBeanName", "targetFilter");
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy();
        filterProxy.init(proxyConfig);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);
        Assert.assertNull(targetFilter.filterConfig);
        Assert.assertEquals(Boolean.TRUE, request.getAttribute("called"));
        filterProxy.destroy();
        Assert.assertNull(targetFilter.filterConfig);
    }

    @Test
    public void testDelegatingFilterProxyInjectedPreferred() throws IOException, ServletException {
        ServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.refresh();
        sc.setAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher", wac);
        StaticWebApplicationContext injectedWac = new StaticWebApplicationContext();
        injectedWac.setServletContext(sc);
        String beanName = "targetFilter";
        injectedWac.registerSingleton(beanName, DelegatingFilterProxyTests.MockFilter.class);
        injectedWac.refresh();
        DelegatingFilterProxyTests.MockFilter targetFilter = ((DelegatingFilterProxyTests.MockFilter) (injectedWac.getBean(beanName)));
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy(beanName, injectedWac);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);
        Assert.assertNull(targetFilter.filterConfig);
        Assert.assertEquals(Boolean.TRUE, request.getAttribute("called"));
        filterProxy.destroy();
        Assert.assertNull(targetFilter.filterConfig);
    }

    @Test
    public void testDelegatingFilterProxyNotInjectedWacServletAttrPreferred() throws IOException, ServletException {
        ServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.refresh();
        sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        sc.setAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher", wac);
        StaticWebApplicationContext wacToUse = new StaticWebApplicationContext();
        wacToUse.setServletContext(sc);
        String beanName = "targetFilter";
        String attrName = "customAttrName";
        wacToUse.registerSingleton(beanName, DelegatingFilterProxyTests.MockFilter.class);
        wacToUse.refresh();
        sc.setAttribute(attrName, wacToUse);
        DelegatingFilterProxyTests.MockFilter targetFilter = ((DelegatingFilterProxyTests.MockFilter) (wacToUse.getBean(beanName)));
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy(beanName);
        filterProxy.setContextAttribute(attrName);
        filterProxy.setServletContext(sc);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);
        Assert.assertNull(targetFilter.filterConfig);
        Assert.assertEquals(Boolean.TRUE, request.getAttribute("called"));
        filterProxy.destroy();
        Assert.assertNull(targetFilter.filterConfig);
    }

    @Test
    public void testDelegatingFilterProxyNotInjectedWithRootPreferred() throws IOException, ServletException {
        ServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.refresh();
        sc.setAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher", wac);
        sc.setAttribute("another", wac);
        StaticWebApplicationContext wacToUse = new StaticWebApplicationContext();
        wacToUse.setServletContext(sc);
        String beanName = "targetFilter";
        wacToUse.registerSingleton(beanName, DelegatingFilterProxyTests.MockFilter.class);
        wacToUse.refresh();
        sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wacToUse);
        DelegatingFilterProxyTests.MockFilter targetFilter = ((DelegatingFilterProxyTests.MockFilter) (wacToUse.getBean(beanName)));
        DelegatingFilterProxy filterProxy = new DelegatingFilterProxy(beanName);
        filterProxy.setServletContext(sc);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filterProxy.doFilter(request, response, null);
        Assert.assertNull(targetFilter.filterConfig);
        Assert.assertEquals(Boolean.TRUE, request.getAttribute("called"));
        filterProxy.destroy();
        Assert.assertNull(targetFilter.filterConfig);
    }

    public static class MockFilter implements Filter {
        public FilterConfig filterConfig;

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            this.filterConfig = filterConfig;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
            request.setAttribute("called", Boolean.TRUE);
        }

        @Override
        public void destroy() {
            this.filterConfig = null;
        }
    }
}

