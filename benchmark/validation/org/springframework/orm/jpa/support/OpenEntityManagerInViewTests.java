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
package org.springframework.orm.jpa.support;


import WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.AsyncListener;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.mock.web.test.MockAsyncContext;
import org.springframework.mock.web.test.MockFilterConfig;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.AsyncWebRequest;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.context.support.StaticWebApplicationContext;


/**
 *
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Phillip Webb
 */
public class OpenEntityManagerInViewTests {
    private EntityManager manager;

    private EntityManagerFactory factory;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private ServletWebRequest webRequest;

    @Test
    public void testOpenEntityManagerInViewInterceptor() throws Exception {
        OpenEntityManagerInViewInterceptor interceptor = new OpenEntityManagerInViewInterceptor();
        interceptor.setEntityManagerFactory(this.factory);
        MockServletContext sc = new MockServletContext();
        MockHttpServletRequest request = new MockHttpServletRequest(sc);
        interceptor.preHandle(new ServletWebRequest(request));
        Assert.assertTrue(TransactionSynchronizationManager.hasResource(this.factory));
        // check that further invocations simply participate
        interceptor.preHandle(new ServletWebRequest(request));
        interceptor.preHandle(new ServletWebRequest(request));
        interceptor.postHandle(new ServletWebRequest(request), null);
        interceptor.afterCompletion(new ServletWebRequest(request), null);
        interceptor.postHandle(new ServletWebRequest(request), null);
        interceptor.afterCompletion(new ServletWebRequest(request), null);
        interceptor.preHandle(new ServletWebRequest(request));
        interceptor.postHandle(new ServletWebRequest(request), null);
        interceptor.afterCompletion(new ServletWebRequest(request), null);
        interceptor.postHandle(new ServletWebRequest(request), null);
        Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
        BDDMockito.given(manager.isOpen()).willReturn(true);
        interceptor.afterCompletion(new ServletWebRequest(request), null);
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory));
        Mockito.verify(manager).close();
    }

    @Test
    public void testOpenEntityManagerInViewInterceptorAsyncScenario() throws Exception {
        // Initial request thread
        OpenEntityManagerInViewInterceptor interceptor = new OpenEntityManagerInViewInterceptor();
        interceptor.setEntityManagerFactory(factory);
        BDDMockito.given(factory.createEntityManager()).willReturn(this.manager);
        interceptor.preHandle(this.webRequest);
        Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
        AsyncWebRequest asyncWebRequest = new org.springframework.web.context.request.async.StandardServletAsyncWebRequest(this.request, this.response);
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(this.webRequest);
        asyncManager.setTaskExecutor(new OpenEntityManagerInViewTests.SyncTaskExecutor());
        asyncManager.setAsyncWebRequest(asyncWebRequest);
        asyncManager.startCallableProcessing(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "anything";
            }
        });
        interceptor.afterConcurrentHandlingStarted(this.webRequest);
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory));
        // Async dispatch thread
        interceptor.preHandle(this.webRequest);
        Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
        asyncManager.clearConcurrentResult();
        // check that further invocations simply participate
        interceptor.preHandle(new ServletWebRequest(request));
        interceptor.preHandle(new ServletWebRequest(request));
        interceptor.postHandle(new ServletWebRequest(request), null);
        interceptor.afterCompletion(new ServletWebRequest(request), null);
        interceptor.postHandle(new ServletWebRequest(request), null);
        interceptor.afterCompletion(new ServletWebRequest(request), null);
        interceptor.preHandle(new ServletWebRequest(request));
        interceptor.postHandle(new ServletWebRequest(request), null);
        interceptor.afterCompletion(new ServletWebRequest(request), null);
        interceptor.postHandle(this.webRequest, null);
        Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
        BDDMockito.given(this.manager.isOpen()).willReturn(true);
        interceptor.afterCompletion(this.webRequest, null);
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory));
        Mockito.verify(this.manager).close();
    }

    @Test
    public void testOpenEntityManagerInViewInterceptorAsyncTimeoutScenario() throws Exception {
        // Initial request thread
        OpenEntityManagerInViewInterceptor interceptor = new OpenEntityManagerInViewInterceptor();
        interceptor.setEntityManagerFactory(factory);
        BDDMockito.given(this.factory.createEntityManager()).willReturn(this.manager);
        interceptor.preHandle(this.webRequest);
        Assert.assertTrue(TransactionSynchronizationManager.hasResource(this.factory));
        AsyncWebRequest asyncWebRequest = new org.springframework.web.context.request.async.StandardServletAsyncWebRequest(this.request, this.response);
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(this.request);
        asyncManager.setTaskExecutor(new OpenEntityManagerInViewTests.SyncTaskExecutor());
        asyncManager.setAsyncWebRequest(asyncWebRequest);
        asyncManager.startCallableProcessing(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "anything";
            }
        });
        interceptor.afterConcurrentHandlingStarted(this.webRequest);
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(this.factory));
        // Async request timeout
        BDDMockito.given(this.manager.isOpen()).willReturn(true);
        MockAsyncContext asyncContext = ((MockAsyncContext) (this.request.getAsyncContext()));
        for (AsyncListener listener : asyncContext.getListeners()) {
            listener.onTimeout(new javax.servlet.AsyncEvent(asyncContext));
        }
        for (AsyncListener listener : asyncContext.getListeners()) {
            listener.onComplete(new javax.servlet.AsyncEvent(asyncContext));
        }
        Mockito.verify(this.manager).close();
    }

    @Test
    public void testOpenEntityManagerInViewInterceptorAsyncErrorScenario() throws Exception {
        // Initial request thread
        OpenEntityManagerInViewInterceptor interceptor = new OpenEntityManagerInViewInterceptor();
        interceptor.setEntityManagerFactory(factory);
        BDDMockito.given(this.factory.createEntityManager()).willReturn(this.manager);
        interceptor.preHandle(this.webRequest);
        Assert.assertTrue(TransactionSynchronizationManager.hasResource(this.factory));
        AsyncWebRequest asyncWebRequest = new org.springframework.web.context.request.async.StandardServletAsyncWebRequest(this.request, this.response);
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(this.request);
        asyncManager.setTaskExecutor(new OpenEntityManagerInViewTests.SyncTaskExecutor());
        asyncManager.setAsyncWebRequest(asyncWebRequest);
        asyncManager.startCallableProcessing(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "anything";
            }
        });
        interceptor.afterConcurrentHandlingStarted(this.webRequest);
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(this.factory));
        // Async request timeout
        BDDMockito.given(this.manager.isOpen()).willReturn(true);
        MockAsyncContext asyncContext = ((MockAsyncContext) (this.request.getAsyncContext()));
        for (AsyncListener listener : asyncContext.getListeners()) {
            listener.onError(new javax.servlet.AsyncEvent(asyncContext, new Exception()));
        }
        for (AsyncListener listener : asyncContext.getListeners()) {
            listener.onComplete(new javax.servlet.AsyncEvent(asyncContext));
        }
        Mockito.verify(this.manager).close();
    }

    @Test
    public void testOpenEntityManagerInViewFilter() throws Exception {
        BDDMockito.given(manager.isOpen()).willReturn(true);
        final EntityManagerFactory factory2 = Mockito.mock(EntityManagerFactory.class);
        final EntityManager manager2 = Mockito.mock(EntityManager.class);
        BDDMockito.given(factory2.createEntityManager()).willReturn(manager2);
        BDDMockito.given(manager2.isOpen()).willReturn(true);
        MockServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", factory);
        wac.getDefaultListableBeanFactory().registerSingleton("myEntityManagerFactory", factory2);
        wac.refresh();
        sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        MockHttpServletRequest request = new MockHttpServletRequest(sc);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterConfig filterConfig = new MockFilterConfig(wac.getServletContext(), "filter");
        MockFilterConfig filterConfig2 = new MockFilterConfig(wac.getServletContext(), "filter2");
        filterConfig2.addInitParameter("entityManagerFactoryBeanName", "myEntityManagerFactory");
        final OpenEntityManagerInViewFilter filter = new OpenEntityManagerInViewFilter();
        filter.init(filterConfig);
        final OpenEntityManagerInViewFilter filter2 = new OpenEntityManagerInViewFilter();
        filter2.init(filterConfig2);
        final FilterChain filterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) {
                Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                servletRequest.setAttribute("invoked", Boolean.TRUE);
            }
        };
        final FilterChain filterChain2 = new FilterChain() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
                Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory2));
                filter.doFilter(servletRequest, servletResponse, filterChain);
            }
        };
        FilterChain filterChain3 = new org.springframework.mock.web.test.PassThroughFilterChain(filter2, filterChain2);
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory));
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory2));
        filter2.doFilter(request, response, filterChain3);
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory));
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory2));
        Assert.assertNotNull(request.getAttribute("invoked"));
        Mockito.verify(manager).close();
        Mockito.verify(manager2).close();
        wac.close();
    }

    @Test
    public void testOpenEntityManagerInViewFilterAsyncScenario() throws Exception {
        BDDMockito.given(manager.isOpen()).willReturn(true);
        final EntityManagerFactory factory2 = Mockito.mock(EntityManagerFactory.class);
        final EntityManager manager2 = Mockito.mock(EntityManager.class);
        BDDMockito.given(factory2.createEntityManager()).willReturn(manager2);
        BDDMockito.given(manager2.isOpen()).willReturn(true);
        MockServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.getDefaultListableBeanFactory().registerSingleton("entityManagerFactory", factory);
        wac.getDefaultListableBeanFactory().registerSingleton("myEntityManagerFactory", factory2);
        wac.refresh();
        sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        MockFilterConfig filterConfig = new MockFilterConfig(wac.getServletContext(), "filter");
        MockFilterConfig filterConfig2 = new MockFilterConfig(wac.getServletContext(), "filter2");
        filterConfig2.addInitParameter("entityManagerFactoryBeanName", "myEntityManagerFactory");
        final OpenEntityManagerInViewFilter filter = new OpenEntityManagerInViewFilter();
        filter.init(filterConfig);
        final OpenEntityManagerInViewFilter filter2 = new OpenEntityManagerInViewFilter();
        filter2.init(filterConfig2);
        final AtomicInteger count = new AtomicInteger(0);
        final FilterChain filterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) {
                Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory));
                servletRequest.setAttribute("invoked", Boolean.TRUE);
                count.incrementAndGet();
            }
        };
        final AtomicInteger count2 = new AtomicInteger(0);
        final FilterChain filterChain2 = new FilterChain() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
                Assert.assertTrue(TransactionSynchronizationManager.hasResource(factory2));
                filter.doFilter(servletRequest, servletResponse, filterChain);
                count2.incrementAndGet();
            }
        };
        FilterChain filterChain3 = new org.springframework.mock.web.test.PassThroughFilterChain(filter2, filterChain2);
        AsyncWebRequest asyncWebRequest = Mockito.mock(AsyncWebRequest.class);
        BDDMockito.given(asyncWebRequest.isAsyncStarted()).willReturn(true);
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(this.request);
        asyncManager.setTaskExecutor(new OpenEntityManagerInViewTests.SyncTaskExecutor());
        asyncManager.setAsyncWebRequest(asyncWebRequest);
        asyncManager.startCallableProcessing(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "anything";
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory));
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory2));
        filter2.doFilter(this.request, this.response, filterChain3);
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory));
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory2));
        Assert.assertEquals(1, count.get());
        Assert.assertEquals(1, count2.get());
        Assert.assertNotNull(request.getAttribute("invoked"));
        Mockito.verify(asyncWebRequest, Mockito.times(2)).addCompletionHandler(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(asyncWebRequest).addTimeoutHandler(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(asyncWebRequest, Mockito.times(2)).addCompletionHandler(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(asyncWebRequest).startAsync();
        // Async dispatch after concurrent handling produces result ...
        Mockito.reset(asyncWebRequest);
        BDDMockito.given(asyncWebRequest.isAsyncStarted()).willReturn(false);
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory));
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory2));
        filter.doFilter(this.request, this.response, filterChain3);
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory));
        Assert.assertFalse(TransactionSynchronizationManager.hasResource(factory2));
        Assert.assertEquals(2, count.get());
        Assert.assertEquals(2, count2.get());
        Mockito.verify(this.manager).close();
        Mockito.verify(manager2).close();
        wac.close();
    }

    @SuppressWarnings("serial")
    private static class SyncTaskExecutor extends SimpleAsyncTaskExecutor {
        @Override
        public void execute(Runnable task, long startTimeout) {
            task.run();
        }
    }
}

