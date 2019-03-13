/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.test.web.servlet.samples.spr;


import RequestAttributes.SCOPE_REQUEST;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Integration tests for the following use cases.
 * <ul>
 * <li>SPR-10025: Access to request attributes via RequestContextHolder</li>
 * <li>SPR-13217: Populate RequestAttributes before invoking Filters in MockMvc</li>
 * <li>SPR-13260: No reuse of mock requests</li>
 * </ul>
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @see CustomRequestAttributesRequestContextHolderTests
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration
@DirtiesContext
public class RequestContextHolderTests {
    private static final String FROM_TCF_MOCK = "fromTestContextFrameworkMock";

    private static final String FROM_MVC_TEST_DEFAULT = "fromSpringMvcTestDefault";

    private static final String FROM_MVC_TEST_MOCK = "fromSpringMvcTestMock";

    private static final String FROM_REQUEST_FILTER = "fromRequestFilter";

    private static final String FROM_REQUEST_ATTRIBUTES_FILTER = "fromRequestAttributesFilter";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockHttpServletRequest mockRequest;

    @Autowired
    private RequestContextHolderTests.RequestScopedController requestScopedController;

    @Autowired
    private RequestContextHolderTests.RequestScopedService requestScopedService;

    @Autowired
    private RequestContextHolderTests.SessionScopedService sessionScopedService;

    @Autowired
    private RequestContextHolderTests.FilterWithSessionScopedService filterWithSessionScopedService;

    private MockMvc mockMvc;

    @Test
    public void singletonController() throws Exception {
        this.mockMvc.perform(get("/singletonController").requestAttr(RequestContextHolderTests.FROM_MVC_TEST_MOCK, RequestContextHolderTests.FROM_MVC_TEST_MOCK));
    }

    @Test
    public void requestScopedController() throws Exception {
        Assert.assertTrue("request-scoped controller must be a CGLIB proxy", AopUtils.isCglibProxy(this.requestScopedController));
        this.mockMvc.perform(get("/requestScopedController").requestAttr(RequestContextHolderTests.FROM_MVC_TEST_MOCK, RequestContextHolderTests.FROM_MVC_TEST_MOCK));
    }

    @Test
    public void requestScopedService() throws Exception {
        Assert.assertTrue("request-scoped service must be a CGLIB proxy", AopUtils.isCglibProxy(this.requestScopedService));
        this.mockMvc.perform(get("/requestScopedService").requestAttr(RequestContextHolderTests.FROM_MVC_TEST_MOCK, RequestContextHolderTests.FROM_MVC_TEST_MOCK));
    }

    @Test
    public void sessionScopedService() throws Exception {
        Assert.assertTrue("session-scoped service must be a CGLIB proxy", AopUtils.isCglibProxy(this.sessionScopedService));
        this.mockMvc.perform(get("/sessionScopedService").requestAttr(RequestContextHolderTests.FROM_MVC_TEST_MOCK, RequestContextHolderTests.FROM_MVC_TEST_MOCK));
    }

    // -------------------------------------------------------------------
    @Configuration
    @EnableWebMvc
    static class WebConfig implements WebMvcConfigurer {
        @Bean
        public RequestContextHolderTests.SingletonController singletonController() {
            return new RequestContextHolderTests.SingletonController();
        }

        @Bean
        @Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
        public RequestContextHolderTests.RequestScopedController requestScopedController() {
            return new RequestContextHolderTests.RequestScopedController();
        }

        @Bean
        @RequestScope
        public RequestContextHolderTests.RequestScopedService requestScopedService() {
            return new RequestContextHolderTests.RequestScopedService();
        }

        @Bean
        public RequestContextHolderTests.ControllerWithRequestScopedService controllerWithRequestScopedService() {
            return new RequestContextHolderTests.ControllerWithRequestScopedService();
        }

        @Bean
        @SessionScope
        public RequestContextHolderTests.SessionScopedService sessionScopedService() {
            return new RequestContextHolderTests.SessionScopedService();
        }

        @Bean
        public RequestContextHolderTests.ControllerWithSessionScopedService controllerWithSessionScopedService() {
            return new RequestContextHolderTests.ControllerWithSessionScopedService();
        }

        @Bean
        public RequestContextHolderTests.FilterWithSessionScopedService filterWithSessionScopedService() {
            return new RequestContextHolderTests.FilterWithSessionScopedService();
        }
    }

    @RestController
    static class SingletonController {
        @RequestMapping("/singletonController")
        public void handle() {
            RequestContextHolderTests.assertRequestAttributes();
        }
    }

    @RestController
    static class RequestScopedController {
        @Autowired
        private ServletRequest request;

        @RequestMapping("/requestScopedController")
        public void handle() {
            RequestContextHolderTests.assertRequestAttributes(request);
            RequestContextHolderTests.assertRequestAttributes();
        }
    }

    static class RequestScopedService {
        @Autowired
        private ServletRequest request;

        void process() {
            RequestContextHolderTests.assertRequestAttributes(request);
        }
    }

    static class SessionScopedService {
        @Autowired
        private ServletRequest request;

        void process() {
            RequestContextHolderTests.assertRequestAttributes(this.request);
        }
    }

    @RestController
    static class ControllerWithRequestScopedService {
        @Autowired
        private RequestContextHolderTests.RequestScopedService service;

        @RequestMapping("/requestScopedService")
        public void handle() {
            this.service.process();
            RequestContextHolderTests.assertRequestAttributes();
        }
    }

    @RestController
    static class ControllerWithSessionScopedService {
        @Autowired
        private RequestContextHolderTests.SessionScopedService service;

        @RequestMapping("/sessionScopedService")
        public void handle() {
            this.service.process();
            RequestContextHolderTests.assertRequestAttributes();
        }
    }

    static class FilterWithSessionScopedService extends GenericFilterBean {
        @Autowired
        private RequestContextHolderTests.SessionScopedService service;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            this.service.process();
            RequestContextHolderTests.assertRequestAttributes(request);
            RequestContextHolderTests.assertRequestAttributes();
            chain.doFilter(request, response);
        }
    }

    static class RequestFilter extends GenericFilterBean {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            request.setAttribute(RequestContextHolderTests.FROM_REQUEST_FILTER, RequestContextHolderTests.FROM_REQUEST_FILTER);
            chain.doFilter(request, response);
        }
    }

    static class RequestAttributesFilter extends GenericFilterBean {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            RequestContextHolder.getRequestAttributes().setAttribute(RequestContextHolderTests.FROM_REQUEST_ATTRIBUTES_FILTER, RequestContextHolderTests.FROM_REQUEST_ATTRIBUTES_FILTER, SCOPE_REQUEST);
            chain.doFilter(request, response);
        }
    }
}

