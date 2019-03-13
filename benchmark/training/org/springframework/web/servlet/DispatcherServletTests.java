/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.web.servlet;


import ContextLoader.GLOBAL_INITIALIZER_CLASSES_PARAM;
import DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;
import HttpServletResponse.SC_NOT_FOUND;
import SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE;
import WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE;
import java.io.IOException;
import java.util.Locale;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.DummyEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletConfig;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ServletConfigAwareBean;
import org.springframework.web.context.ServletContextAwareBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StandardServletEnvironment;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static FrameworkServlet.DEFAULT_NAMESPACE_SUFFIX;
import static FrameworkServlet.SERVLET_CONTEXT_PREFIX;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 */
public class DispatcherServletTests {
    private static final String URL_KNOWN_ONLY_PARENT = "/knownOnlyToParent.do";

    private final MockServletConfig servletConfig = new MockServletConfig(new MockServletContext(), "simple");

    private DispatcherServlet simpleDispatcherServlet;

    private DispatcherServlet complexDispatcherServlet;

    @Test
    public void configuredDispatcherServlets() {
        Assert.assertTrue("Correct namespace", ("simple" + (DEFAULT_NAMESPACE_SUFFIX)).equals(simpleDispatcherServlet.getNamespace()));
        Assert.assertTrue("Correct attribute", ((SERVLET_CONTEXT_PREFIX) + "simple").equals(simpleDispatcherServlet.getServletContextAttributeName()));
        Assert.assertTrue("Context published", ((simpleDispatcherServlet.getWebApplicationContext()) == (getServletContext().getAttribute(((SERVLET_CONTEXT_PREFIX) + "simple")))));
        Assert.assertTrue("Correct namespace", "test".equals(complexDispatcherServlet.getNamespace()));
        Assert.assertTrue("Correct attribute", ((SERVLET_CONTEXT_PREFIX) + "complex").equals(complexDispatcherServlet.getServletContextAttributeName()));
        Assert.assertTrue("Context not published", ((getServletContext().getAttribute(((SERVLET_CONTEXT_PREFIX) + "complex"))) == null));
        simpleDispatcherServlet.destroy();
        complexDispatcherServlet.destroy();
    }

    @Test
    public void invalidRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/invalid.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        simpleDispatcherServlet.service(request, response);
        Assert.assertTrue("Not forwarded", ((response.getForwardedUrl()) == null));
        Assert.assertTrue("correct error code", ((response.getStatus()) == (HttpServletResponse.SC_NOT_FOUND)));
    }

    @Test
    public void requestHandledEvent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        ComplexWebApplicationContext.TestApplicationListener listener = ((ComplexWebApplicationContext.TestApplicationListener) (complexDispatcherServlet.getWebApplicationContext().getBean("testListener")));
        Assert.assertEquals(1, listener.counter);
    }

    @Test
    public void publishEventsOff() throws Exception {
        complexDispatcherServlet.setPublishEvents(false);
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        ComplexWebApplicationContext.TestApplicationListener listener = ((ComplexWebApplicationContext.TestApplicationListener) (complexDispatcherServlet.getWebApplicationContext().getBean("testListener")));
        Assert.assertEquals(0, listener.counter);
    }

    @Test
    public void parameterizableViewController() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/view.do");
        request.addUserRole("role1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertTrue("forwarded to form", "myform.jsp".equals(response.getForwardedUrl()));
    }

    @Test
    public void handlerInterceptorSuppressesView() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/view.do");
        request.addUserRole("role1");
        request.addParameter("noView", "true");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertTrue("Not forwarded", ((response.getForwardedUrl()) == null));
    }

    @Test
    public void localeRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        request.addPreferredLocale(Locale.CANADA);
        MockHttpServletResponse response = new MockHttpServletResponse();
        simpleDispatcherServlet.service(request, response);
        Assert.assertTrue("Not forwarded", ((response.getForwardedUrl()) == null));
        Assert.assertEquals("Wed, 01 Apr 2015 00:00:00 GMT", response.getHeader("Last-Modified"));
    }

    @Test
    public void unknownRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/unknown.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals("forwarded to failed", "failed0.jsp", response.getForwardedUrl());
        Assert.assertTrue("Exception exposed", request.getAttribute("exception").getClass().equals(ServletException.class));
    }

    @Test
    public void anotherLocaleRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do;abc=def");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertTrue("Not forwarded", ((response.getForwardedUrl()) == null));
        Assert.assertTrue(((request.getAttribute("test1")) != null));
        Assert.assertTrue(((request.getAttribute("test1x")) == null));
        Assert.assertTrue(((request.getAttribute("test1y")) == null));
        Assert.assertTrue(((request.getAttribute("test2")) != null));
        Assert.assertTrue(((request.getAttribute("test2x")) == null));
        Assert.assertTrue(((request.getAttribute("test2y")) == null));
        Assert.assertTrue(((request.getAttribute("test3")) != null));
        Assert.assertTrue(((request.getAttribute("test3x")) != null));
        Assert.assertTrue(((request.getAttribute("test3y")) != null));
        Assert.assertEquals("Wed, 01 Apr 2015 00:00:01 GMT", response.getHeader("Last-Modified"));
    }

    @Test
    public void existingMultipartRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do;abc=def");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        ComplexWebApplicationContext.MockMultipartResolver multipartResolver = ((ComplexWebApplicationContext.MockMultipartResolver) (complexDispatcherServlet.getWebApplicationContext().getBean("multipartResolver")));
        MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);
        complexDispatcherServlet.service(multipartRequest, response);
        multipartResolver.cleanupMultipart(multipartRequest);
        Assert.assertNull(request.getAttribute(DEFAULT_EXCEPTION_ATTRIBUTE));
        Assert.assertNotNull(request.getAttribute("cleanedUp"));
    }

    @Test
    public void existingMultipartRequestButWrapped() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do;abc=def");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        ComplexWebApplicationContext.MockMultipartResolver multipartResolver = ((ComplexWebApplicationContext.MockMultipartResolver) (complexDispatcherServlet.getWebApplicationContext().getBean("multipartResolver")));
        MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);
        complexDispatcherServlet.service(new javax.servlet.http.HttpServletRequestWrapper(multipartRequest), response);
        multipartResolver.cleanupMultipart(multipartRequest);
        Assert.assertNull(request.getAttribute(DEFAULT_EXCEPTION_ATTRIBUTE));
        Assert.assertNotNull(request.getAttribute("cleanedUp"));
    }

    @Test
    public void multipartResolutionFailed() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do;abc=def");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        request.setAttribute("fail", Boolean.TRUE);
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertTrue("forwarded to failed", "failed0.jsp".equals(response.getForwardedUrl()));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue("correct exception", ((request.getAttribute(DEFAULT_EXCEPTION_ATTRIBUTE)) instanceof MaxUploadSizeExceededException));
    }

    @Test
    public void handlerInterceptorAbort() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        request.addParameter("abort", "true");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertTrue("Not forwarded", ((response.getForwardedUrl()) == null));
        Assert.assertTrue(((request.getAttribute("test1")) != null));
        Assert.assertTrue(((request.getAttribute("test1x")) != null));
        Assert.assertTrue(((request.getAttribute("test1y")) == null));
        Assert.assertTrue(((request.getAttribute("test2")) == null));
        Assert.assertTrue(((request.getAttribute("test2x")) == null));
        Assert.assertTrue(((request.getAttribute("test2y")) == null));
    }

    @Test
    public void modelAndViewDefiningException() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        request.addParameter("fail", "yes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            complexDispatcherServlet.service(request, response);
            Assert.assertEquals(200, response.getStatus());
            Assert.assertTrue("forwarded to failed", "failed1.jsp".equals(response.getForwardedUrl()));
        } catch (ServletException ex) {
            Assert.fail(("Should not have thrown ServletException: " + (ex.getMessage())));
        }
    }

    @Test
    public void simpleMappingExceptionResolverWithSpecificHandler1() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        request.addParameter("access", "yes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("forwarded to failed", "failed2.jsp", response.getForwardedUrl());
        Assert.assertTrue("Exception exposed", ((request.getAttribute("exception")) instanceof IllegalAccessException));
    }

    @Test
    public void simpleMappingExceptionResolverWithSpecificHandler2() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        request.addParameter("servlet", "yes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("forwarded to failed", "failed3.jsp", response.getForwardedUrl());
        Assert.assertTrue("Exception exposed", ((request.getAttribute("exception")) instanceof ServletException));
    }

    @Test
    public void simpleMappingExceptionResolverWithAllHandlers1() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/loc.do");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        request.addParameter("access", "yes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals(500, response.getStatus());
        Assert.assertEquals("forwarded to failed", "failed1.jsp", response.getForwardedUrl());
        Assert.assertTrue("Exception exposed", ((request.getAttribute("exception")) instanceof IllegalAccessException));
    }

    @Test
    public void simpleMappingExceptionResolverWithAllHandlers2() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/loc.do");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        request.addParameter("servlet", "yes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals(500, response.getStatus());
        Assert.assertEquals("forwarded to failed", "failed1.jsp", response.getForwardedUrl());
        Assert.assertTrue("Exception exposed", ((request.getAttribute("exception")) instanceof ServletException));
    }

    @Test
    public void simpleMappingExceptionResolverWithDefaultErrorView() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        request.addParameter("exception", "yes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("forwarded to failed", "failed0.jsp", response.getForwardedUrl());
        Assert.assertTrue("Exception exposed", request.getAttribute("exception").getClass().equals(RuntimeException.class));
    }

    @Test
    public void localeChangeInterceptor1() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        request.addPreferredLocale(Locale.GERMAN);
        request.addUserRole("role2");
        request.addParameter("locale", "en");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("forwarded to failed", "failed0.jsp", response.getForwardedUrl());
        Assert.assertTrue("Exception exposed", request.getAttribute("exception").getClass().equals(ServletException.class));
    }

    @Test
    public void localeChangeInterceptor2() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        request.addPreferredLocale(Locale.GERMAN);
        request.addUserRole("role2");
        request.addParameter("locale", "en");
        request.addParameter("locale2", "en_CA");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertTrue("Not forwarded", ((response.getForwardedUrl()) == null));
    }

    @Test
    public void themeChangeInterceptor1() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        request.addParameter("theme", "mytheme");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("forwarded to failed", "failed0.jsp", response.getForwardedUrl());
        Assert.assertTrue("Exception exposed", request.getAttribute("exception").getClass().equals(ServletException.class));
    }

    @Test
    public void themeChangeInterceptor2() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        request.addPreferredLocale(Locale.CANADA);
        request.addUserRole("role1");
        request.addParameter("theme", "mytheme");
        request.addParameter("theme2", "theme");
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            complexDispatcherServlet.service(request, response);
            Assert.assertTrue("Not forwarded", ((response.getForwardedUrl()) == null));
        } catch (ServletException ex) {
            Assert.fail(("Should not have thrown ServletException: " + (ex.getMessage())));
        }
    }

    @Test
    public void notAuthorized() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/locale.do");
        request.addPreferredLocale(Locale.CANADA);
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            complexDispatcherServlet.service(request, response);
            Assert.assertTrue("Correct response", ((response.getStatus()) == (HttpServletResponse.SC_FORBIDDEN)));
        } catch (ServletException ex) {
            Assert.fail(("Should not have thrown ServletException: " + (ex.getMessage())));
        }
    }

    @Test
    public void headMethodWithExplicitHandling() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "HEAD", "/head.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals(5, response.getContentLength());
        request = new MockHttpServletRequest(getServletContext(), "GET", "/head.do");
        response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals("", response.getContentAsString());
    }

    @Test
    public void headMethodWithNoBodyResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "HEAD", "/body.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals(4, response.getContentLength());
        request = new MockHttpServletRequest(getServletContext(), "GET", "/body.do");
        response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals("body", response.getContentAsString());
    }

    @Test
    public void notDetectAllHandlerMappings() throws IOException, ServletException {
        DispatcherServlet complexDispatcherServlet = new DispatcherServlet();
        complexDispatcherServlet.setContextClass(ComplexWebApplicationContext.class);
        complexDispatcherServlet.setNamespace("test");
        complexDispatcherServlet.setDetectAllHandlerMappings(false);
        complexDispatcherServlet.init(new MockServletConfig(getServletContext(), "complex"));
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/unknown.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertTrue(((response.getStatus()) == (HttpServletResponse.SC_NOT_FOUND)));
    }

    @Test
    public void handlerNotMappedWithAutodetect() throws IOException, ServletException {
        DispatcherServlet complexDispatcherServlet = new DispatcherServlet();
        // no parent
        complexDispatcherServlet.setContextClass(ComplexWebApplicationContext.class);
        complexDispatcherServlet.setNamespace("test");
        complexDispatcherServlet.init(new MockServletConfig(getServletContext(), "complex"));
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", DispatcherServletTests.URL_KNOWN_ONLY_PARENT);
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals(SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void detectHandlerMappingFromParent() throws IOException, ServletException {
        // create a parent context that includes a mapping
        StaticWebApplicationContext parent = new StaticWebApplicationContext();
        parent.setServletContext(getServletContext());
        parent.registerSingleton("parentHandler", DispatcherServletTests.ControllerFromParent.class, new MutablePropertyValues());
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("mappings", ((DispatcherServletTests.URL_KNOWN_ONLY_PARENT) + "=parentHandler")));
        parent.registerSingleton("parentMapping", SimpleUrlHandlerMapping.class, pvs);
        parent.refresh();
        DispatcherServlet complexDispatcherServlet = new DispatcherServlet();
        // will have parent
        complexDispatcherServlet.setContextClass(ComplexWebApplicationContext.class);
        complexDispatcherServlet.setNamespace("test");
        ServletConfig config = new MockServletConfig(getServletContext(), "complex");
        config.getServletContext().setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, parent);
        complexDispatcherServlet.init(config);
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", DispatcherServletTests.URL_KNOWN_ONLY_PARENT);
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertFalse(("Matched through parent controller/handler pair: not response=" + (response.getStatus())), ((response.getStatus()) == (HttpServletResponse.SC_NOT_FOUND)));
    }

    @Test
    public void detectAllHandlerAdapters() throws IOException, ServletException {
        DispatcherServlet complexDispatcherServlet = new DispatcherServlet();
        complexDispatcherServlet.setContextClass(ComplexWebApplicationContext.class);
        complexDispatcherServlet.setNamespace("test");
        complexDispatcherServlet.init(new MockServletConfig(getServletContext(), "complex"));
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/servlet.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals("body", response.getContentAsString());
        request = new MockHttpServletRequest(getServletContext(), "GET", "/form.do");
        response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
    }

    @Test
    public void notDetectAllHandlerAdapters() throws IOException, ServletException {
        DispatcherServlet complexDispatcherServlet = new DispatcherServlet();
        complexDispatcherServlet.setContextClass(ComplexWebApplicationContext.class);
        complexDispatcherServlet.setNamespace("test");
        complexDispatcherServlet.setDetectAllHandlerAdapters(false);
        complexDispatcherServlet.init(new MockServletConfig(getServletContext(), "complex"));
        // only ServletHandlerAdapter with bean name "handlerAdapter" detected
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/servlet.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals("body", response.getContentAsString());
        // SimpleControllerHandlerAdapter not detected
        request = new MockHttpServletRequest(getServletContext(), "GET", "/form.do");
        response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals("forwarded to failed", "failed0.jsp", response.getForwardedUrl());
        Assert.assertTrue("Exception exposed", request.getAttribute("exception").getClass().equals(ServletException.class));
    }

    @Test
    public void notDetectAllHandlerExceptionResolvers() throws IOException, ServletException {
        DispatcherServlet complexDispatcherServlet = new DispatcherServlet();
        complexDispatcherServlet.setContextClass(ComplexWebApplicationContext.class);
        complexDispatcherServlet.setNamespace("test");
        complexDispatcherServlet.setDetectAllHandlerExceptionResolvers(false);
        complexDispatcherServlet.init(new MockServletConfig(getServletContext(), "complex"));
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/unknown.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            complexDispatcherServlet.service(request, response);
            Assert.fail("Should have thrown ServletException");
        } catch (ServletException ex) {
            // expected
            Assert.assertTrue(ex.getMessage().contains("No adapter for handler"));
        }
    }

    @Test
    public void notDetectAllViewResolvers() throws IOException, ServletException {
        DispatcherServlet complexDispatcherServlet = new DispatcherServlet();
        complexDispatcherServlet.setContextClass(ComplexWebApplicationContext.class);
        complexDispatcherServlet.setNamespace("test");
        complexDispatcherServlet.setDetectAllViewResolvers(false);
        complexDispatcherServlet.init(new MockServletConfig(getServletContext(), "complex"));
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/unknown.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            complexDispatcherServlet.service(request, response);
            Assert.fail("Should have thrown ServletException");
        } catch (ServletException ex) {
            // expected
            Assert.assertTrue(ex.getMessage().contains("failed0"));
        }
    }

    @Test
    public void throwExceptionIfNoHandlerFound() throws IOException, ServletException {
        DispatcherServlet complexDispatcherServlet = new DispatcherServlet();
        complexDispatcherServlet.setContextClass(SimpleWebApplicationContext.class);
        complexDispatcherServlet.setNamespace("test");
        complexDispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        complexDispatcherServlet.init(new MockServletConfig(getServletContext(), "complex"));
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/unknown");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertTrue("correct error code", ((response.getStatus()) == (HttpServletResponse.SC_NOT_FOUND)));
    }

    // SPR-12984
    @Test
    public void noHandlerFoundExceptionMessage() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("foo", "bar");
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/foo", headers);
        Assert.assertTrue((!(ex.getMessage().contains("bar"))));
        Assert.assertTrue((!(ex.toString().contains("bar"))));
    }

    @Test
    public void cleanupAfterIncludeWithRemove() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/main.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute("test1", "value1");
        request.setAttribute("test2", "value2");
        WebApplicationContext wac = new StaticWebApplicationContext();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        request.setAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE, "/form.do");
        simpleDispatcherServlet.service(request, response);
        Assert.assertEquals("value1", request.getAttribute("test1"));
        Assert.assertEquals("value2", request.getAttribute("test2"));
        Assert.assertEquals(wac, request.getAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE));
        Assert.assertNull(request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
        Assert.assertNull(request.getAttribute("command"));
    }

    @Test
    public void cleanupAfterIncludeWithRestore() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/main.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute("test1", "value1");
        request.setAttribute("test2", "value2");
        WebApplicationContext wac = new StaticWebApplicationContext();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        TestBean command = new TestBean();
        request.setAttribute("command", command);
        request.setAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE, "/form.do");
        simpleDispatcherServlet.service(request, response);
        Assert.assertEquals("value1", request.getAttribute("test1"));
        Assert.assertEquals("value2", request.getAttribute("test2"));
        Assert.assertSame(wac, request.getAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE));
    }

    @Test
    public void noCleanupAfterInclude() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/main.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute("test1", "value1");
        request.setAttribute("test2", "value2");
        WebApplicationContext wac = new StaticWebApplicationContext();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        TestBean command = new TestBean();
        request.setAttribute("command", command);
        request.setAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE, "/form.do");
        simpleDispatcherServlet.setCleanupAfterInclude(false);
        simpleDispatcherServlet.service(request, response);
        Assert.assertEquals("value1", request.getAttribute("test1"));
        Assert.assertEquals("value2", request.getAttribute("test2"));
        Assert.assertSame(wac, request.getAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE));
    }

    @Test
    public void servletHandlerAdapter() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "GET", "/servlet.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals("body", response.getContentAsString());
        Servlet myServlet = ((Servlet) (complexDispatcherServlet.getWebApplicationContext().getBean("myServlet")));
        Assert.assertEquals("complex", myServlet.getServletConfig().getServletName());
        Assert.assertEquals(getServletContext(), myServlet.getServletConfig().getServletContext());
        complexDispatcherServlet.destroy();
        Assert.assertNull(myServlet.getServletConfig());
    }

    @Test
    public void withNoView() throws Exception {
        MockServletContext servletContext = new MockServletContext();
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext, "GET", "/noview.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals("noview.jsp", response.getForwardedUrl());
    }

    @Test
    public void withNoViewNested() throws Exception {
        MockServletContext servletContext = new MockServletContext();
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext, "GET", "/noview/simple.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        complexDispatcherServlet.service(request, response);
        Assert.assertEquals("noview/simple.jsp", response.getForwardedUrl());
    }

    @Test
    public void withNoViewAndSamePath() throws Exception {
        InternalResourceViewResolver vr = ((InternalResourceViewResolver) (complexDispatcherServlet.getWebApplicationContext().getBean("viewResolver2")));
        vr.setSuffix("");
        MockServletContext servletContext = new MockServletContext();
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext, "GET", "/noview");
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            complexDispatcherServlet.service(request, response);
            Assert.fail("Should have thrown ServletException");
        } catch (ServletException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void dispatcherServletRefresh() throws ServletException {
        MockServletContext servletContext = new MockServletContext("org/springframework/web/context");
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.init(new MockServletConfig(servletContext, "empty"));
        ServletContextAwareBean contextBean = ((ServletContextAwareBean) (servlet.getWebApplicationContext().getBean("servletContextAwareBean")));
        ServletConfigAwareBean configBean = ((ServletConfigAwareBean) (servlet.getWebApplicationContext().getBean("servletConfigAwareBean")));
        Assert.assertSame(servletContext, contextBean.getServletContext());
        Assert.assertSame(servlet.getServletConfig(), configBean.getServletConfig());
        MultipartResolver multipartResolver = servlet.getMultipartResolver();
        Assert.assertNotNull(multipartResolver);
        servlet.refresh();
        ServletContextAwareBean contextBean2 = ((ServletContextAwareBean) (servlet.getWebApplicationContext().getBean("servletContextAwareBean")));
        ServletConfigAwareBean configBean2 = ((ServletConfigAwareBean) (servlet.getWebApplicationContext().getBean("servletConfigAwareBean")));
        Assert.assertSame(servletContext, contextBean2.getServletContext());
        Assert.assertSame(servlet.getServletConfig(), configBean2.getServletConfig());
        Assert.assertNotSame(contextBean, contextBean2);
        Assert.assertNotSame(configBean, configBean2);
        MultipartResolver multipartResolver2 = servlet.getMultipartResolver();
        Assert.assertNotSame(multipartResolver, multipartResolver2);
        servlet.destroy();
    }

    @Test
    public void dispatcherServletContextRefresh() throws ServletException {
        MockServletContext servletContext = new MockServletContext("org/springframework/web/context");
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.init(new MockServletConfig(servletContext, "empty"));
        ServletContextAwareBean contextBean = ((ServletContextAwareBean) (servlet.getWebApplicationContext().getBean("servletContextAwareBean")));
        ServletConfigAwareBean configBean = ((ServletConfigAwareBean) (servlet.getWebApplicationContext().getBean("servletConfigAwareBean")));
        Assert.assertSame(servletContext, contextBean.getServletContext());
        Assert.assertSame(servlet.getServletConfig(), configBean.getServletConfig());
        MultipartResolver multipartResolver = servlet.getMultipartResolver();
        Assert.assertNotNull(multipartResolver);
        refresh();
        ServletContextAwareBean contextBean2 = ((ServletContextAwareBean) (servlet.getWebApplicationContext().getBean("servletContextAwareBean")));
        ServletConfigAwareBean configBean2 = ((ServletConfigAwareBean) (servlet.getWebApplicationContext().getBean("servletConfigAwareBean")));
        Assert.assertSame(servletContext, contextBean2.getServletContext());
        Assert.assertSame(servlet.getServletConfig(), configBean2.getServletConfig());
        Assert.assertTrue((contextBean != contextBean2));
        Assert.assertTrue((configBean != configBean2));
        MultipartResolver multipartResolver2 = servlet.getMultipartResolver();
        Assert.assertTrue((multipartResolver != multipartResolver2));
        servlet.destroy();
    }

    @Test
    public void environmentOperations() {
        DispatcherServlet servlet = new DispatcherServlet();
        ConfigurableEnvironment defaultEnv = servlet.getEnvironment();
        Assert.assertThat(defaultEnv, CoreMatchers.notNullValue());
        ConfigurableEnvironment env1 = new StandardServletEnvironment();
        servlet.setEnvironment(env1);// should succeed

        Assert.assertThat(servlet.getEnvironment(), CoreMatchers.sameInstance(env1));
        try {
            servlet.setEnvironment(new DummyEnvironment());
            Assert.fail("expected IllegalArgumentException for non-configurable Environment");
        } catch (IllegalArgumentException ex) {
        }
        class CustomServletEnvironment extends StandardServletEnvironment {}
        @SuppressWarnings("serial")
        DispatcherServlet custom = new DispatcherServlet() {
            @Override
            protected ConfigurableWebEnvironment createEnvironment() {
                return new CustomServletEnvironment();
            }
        };
        Assert.assertThat(custom.getEnvironment(), CoreMatchers.instanceOf(CustomServletEnvironment.class));
    }

    @Test
    public void allowedOptionsIncludesPatchMethod() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getServletContext(), "OPTIONS", "/foo");
        MockHttpServletResponse response = Mockito.spy(new MockHttpServletResponse());
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.setDispatchOptionsRequest(false);
        servlet.service(request, response);
        Mockito.verify(response, Mockito.never()).getHeader(ArgumentMatchers.anyString());// SPR-10341

        Assert.assertThat(response.getHeader("Allow"), CoreMatchers.equalTo("GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH"));
    }

    @Test
    public void contextInitializers() throws Exception {
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.setContextClass(SimpleWebApplicationContext.class);
        servlet.setContextInitializers(new DispatcherServletTests.TestWebContextInitializer(), new DispatcherServletTests.OtherWebContextInitializer());
        servlet.init(servletConfig);
        Assert.assertEquals("true", getServletContext().getAttribute("initialized"));
        Assert.assertEquals("true", getServletContext().getAttribute("otherInitialized"));
    }

    @Test
    public void contextInitializerClasses() throws Exception {
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.setContextClass(SimpleWebApplicationContext.class);
        servlet.setContextInitializerClasses((((DispatcherServletTests.TestWebContextInitializer.class.getName()) + ",") + (DispatcherServletTests.OtherWebContextInitializer.class.getName())));
        servlet.init(servletConfig);
        Assert.assertEquals("true", getServletContext().getAttribute("initialized"));
        Assert.assertEquals("true", getServletContext().getAttribute("otherInitialized"));
    }

    @Test
    public void globalInitializerClasses() throws Exception {
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.setContextClass(SimpleWebApplicationContext.class);
        getServletContext().setInitParameter(GLOBAL_INITIALIZER_CLASSES_PARAM, (((DispatcherServletTests.TestWebContextInitializer.class.getName()) + ",") + (DispatcherServletTests.OtherWebContextInitializer.class.getName())));
        servlet.init(servletConfig);
        Assert.assertEquals("true", getServletContext().getAttribute("initialized"));
        Assert.assertEquals("true", getServletContext().getAttribute("otherInitialized"));
    }

    @Test
    public void mixedInitializerClasses() throws Exception {
        DispatcherServlet servlet = new DispatcherServlet();
        servlet.setContextClass(SimpleWebApplicationContext.class);
        getServletContext().setInitParameter(GLOBAL_INITIALIZER_CLASSES_PARAM, DispatcherServletTests.TestWebContextInitializer.class.getName());
        servlet.setContextInitializerClasses(DispatcherServletTests.OtherWebContextInitializer.class.getName());
        servlet.init(servletConfig);
        Assert.assertEquals("true", getServletContext().getAttribute("initialized"));
        Assert.assertEquals("true", getServletContext().getAttribute("otherInitialized"));
    }

    public static class ControllerFromParent implements Controller {
        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return new ModelAndView(DispatcherServletTests.ControllerFromParent.class.getName());
        }
    }

    private static class TestWebContextInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {
        @Override
        public void initialize(ConfigurableWebApplicationContext applicationContext) {
            applicationContext.getServletContext().setAttribute("initialized", "true");
        }
    }

    private static class OtherWebContextInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {
        @Override
        public void initialize(ConfigurableWebApplicationContext applicationContext) {
            applicationContext.getServletContext().setAttribute("otherInitialized", "true");
        }
    }
}

