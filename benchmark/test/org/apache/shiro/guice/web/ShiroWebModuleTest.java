/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.guice.web;


import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.binder.AnnotatedBindingBuilder;
import java.io.IOException;
import java.util.Collection;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.env.Environment;
import org.apache.shiro.guice.ShiroModuleTest;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static ShiroWebModule.NAME;


public class ShiroWebModuleTest {
    @Test
    public void basicInstantiation() {
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        ServletContext servletContext = createMock(ServletContext.class);
        Injector injector = Guice.createInjector(new ShiroWebModule(servletContext) {
            @Override
            protected void configureShiroWeb() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
                expose(SessionManager.class);
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }
        });
        // we're not getting a WebSecurityManager here b/c it's not exposed.  There didn't seem to be a good reason to
        // expose it outside of the Shiro module.
        SecurityManager securityManager = injector.getInstance(SecurityManager.class);
        Assert.assertNotNull(securityManager);
        Assert.assertTrue((securityManager instanceof WebSecurityManager));
        SessionManager sessionManager = injector.getInstance(SessionManager.class);
        Assert.assertNotNull(sessionManager);
        Assert.assertTrue((sessionManager instanceof ServletContainerSessionManager));
        Assert.assertTrue(((getSessionManager()) instanceof ServletContainerSessionManager));
    }

    @Test
    public void testBindWebSecurityManager() throws Exception {
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        ServletContext servletContext = createMock(ServletContext.class);
        Injector injector = Guice.createInjector(new ShiroWebModule(servletContext) {
            @Override
            protected void configureShiroWeb() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
                expose(WebSecurityManager.class);
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }

            @Override
            protected void bindWebSecurityManager(AnnotatedBindingBuilder<? super WebSecurityManager> bind) {
                bind.to(ShiroWebModuleTest.MyDefaultWebSecurityManager.class).asEagerSingleton();
            }
        });
        SecurityManager securityManager = injector.getInstance(SecurityManager.class);
        Assert.assertNotNull(securityManager);
        Assert.assertTrue((securityManager instanceof ShiroWebModuleTest.MyDefaultWebSecurityManager));
        WebSecurityManager webSecurityManager = injector.getInstance(WebSecurityManager.class);
        Assert.assertNotNull(webSecurityManager);
        Assert.assertTrue((webSecurityManager instanceof ShiroWebModuleTest.MyDefaultWebSecurityManager));
        // SHIRO-435: Check both keys SecurityManager and WebSecurityManager are bound to the same instance
        Assert.assertTrue((securityManager == webSecurityManager));
    }

    @Test
    public void testBindWebEnvironment() throws Exception {
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        ServletContext servletContext = createMock(ServletContext.class);
        Injector injector = Guice.createInjector(new ShiroWebModule(servletContext) {
            @Override
            protected void configureShiroWeb() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
                expose(WebEnvironment.class);
                expose(Environment.class);
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }

            @Override
            protected void bindWebEnvironment(AnnotatedBindingBuilder<? super WebEnvironment> bind) {
                bind.to(ShiroWebModuleTest.MyWebEnvironment.class).asEagerSingleton();
            }
        });
        Environment environment = injector.getInstance(Environment.class);
        Assert.assertNotNull(environment);
        Assert.assertTrue((environment instanceof ShiroWebModuleTest.MyWebEnvironment));
        WebEnvironment webEnvironment = injector.getInstance(WebEnvironment.class);
        Assert.assertNotNull(webEnvironment);
        Assert.assertTrue((webEnvironment instanceof ShiroWebModuleTest.MyWebEnvironment));
        // SHIRO-435: Check both keys Environment and WebEnvironment are bound to the same instance
        Assert.assertTrue((environment == webEnvironment));
    }

    /**
     *
     *
     * @since 1.4
     */
    @Test
    public void testAddFilterChainGuice3and4() {
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        ServletContext servletContext = createMock(ServletContext.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        servletContext.setAttribute(eq(EnvironmentLoader.ENVIRONMENT_ATTRIBUTE_KEY), EasyMock.anyObject());
        expect(request.getAttribute("javax.servlet.include.context_path")).andReturn("").anyTimes();
        expect(request.getCharacterEncoding()).andReturn("UTF-8").anyTimes();
        expect(request.getAttribute("javax.servlet.include.request_uri")).andReturn("/test_authc");
        expect(request.getAttribute("javax.servlet.include.request_uri")).andReturn("/test_custom_filter");
        expect(request.getAttribute("javax.servlet.include.request_uri")).andReturn("/test_authc_basic");
        expect(request.getAttribute("javax.servlet.include.request_uri")).andReturn("/test_perms");
        expect(request.getAttribute("javax.servlet.include.request_uri")).andReturn("/multiple_configs");
        replay(servletContext, request);
        Injector injector = Guice.createInjector(new ShiroWebModule(servletContext) {
            @Override
            protected void configureShiroWeb() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
                expose(FilterChainResolver.class);
                this.addFilterChain("/test_authc/**", filterConfig(AUTHC));
                this.addFilterChain("/test_custom_filter/**", Key.get(ShiroWebModuleTest.CustomFilter.class));
                this.addFilterChain("/test_authc_basic/**", AUTHC_BASIC);
                this.addFilterChain("/test_perms/**", filterConfig(PERMS, "remote:invoke:lan,wan"));
                this.addFilterChain("/multiple_configs/**", filterConfig(AUTHC), filterConfig(ROLES, "b2bClient"), filterConfig(PERMS, "remote:invoke:lan,wan"));
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }
        });
        FilterChainResolver resolver = injector.getInstance(FilterChainResolver.class);
        Assert.assertThat(resolver, instanceOf(SimpleFilterChainResolver.class));
        SimpleFilterChainResolver simpleFilterChainResolver = ((SimpleFilterChainResolver) (resolver));
        // test the /test_authc resource
        FilterChain filterChain = simpleFilterChainResolver.getChain(request, null, null);
        Assert.assertThat(filterChain, instanceOf(SimpleFilterChain.class));
        Filter nextFilter = getNextFilter(((SimpleFilterChain) (filterChain)));
        Assert.assertThat(nextFilter, instanceOf(FormAuthenticationFilter.class));
        // test the /test_custom_filter resource
        filterChain = simpleFilterChainResolver.getChain(request, null, null);
        Assert.assertThat(filterChain, instanceOf(SimpleFilterChain.class));
        nextFilter = getNextFilter(((SimpleFilterChain) (filterChain)));
        Assert.assertThat(nextFilter, instanceOf(ShiroWebModuleTest.CustomFilter.class));
        // test the /test_authc_basic resource
        filterChain = simpleFilterChainResolver.getChain(request, null, null);
        Assert.assertThat(filterChain, instanceOf(SimpleFilterChain.class));
        nextFilter = getNextFilter(((SimpleFilterChain) (filterChain)));
        Assert.assertThat(nextFilter, instanceOf(BasicHttpAuthenticationFilter.class));
        // test the /test_perms resource
        filterChain = simpleFilterChainResolver.getChain(request, null, null);
        Assert.assertThat(filterChain, instanceOf(SimpleFilterChain.class));
        nextFilter = getNextFilter(((SimpleFilterChain) (filterChain)));
        Assert.assertThat(nextFilter, instanceOf(PermissionsAuthorizationFilter.class));
        // test the /multiple_configs resource
        filterChain = simpleFilterChainResolver.getChain(request, null, null);
        Assert.assertThat(filterChain, instanceOf(SimpleFilterChain.class));
        Assert.assertThat(getNextFilter(((SimpleFilterChain) (filterChain))), instanceOf(FormAuthenticationFilter.class));
        Assert.assertThat(getNextFilter(((SimpleFilterChain) (filterChain))), instanceOf(RolesAuthorizationFilter.class));
        Assert.assertThat(getNextFilter(((SimpleFilterChain) (filterChain))), instanceOf(PermissionsAuthorizationFilter.class));
        verify(servletContext, request);
    }

    /**
     *
     *
     * @since 1.4
     */
    @Test
    public void testAddFilterChainGuice3Only() {
        Assume.assumeTrue("This test only runs agains Guice 3.x", ShiroWebModule.isGuiceVersion3());
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        ServletContext servletContext = createMock(ServletContext.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        servletContext.setAttribute(eq(EnvironmentLoader.ENVIRONMENT_ATTRIBUTE_KEY), EasyMock.anyObject());
        expect(request.getAttribute("javax.servlet.include.context_path")).andReturn("").anyTimes();
        expect(request.getCharacterEncoding()).andReturn("UTF-8").anyTimes();
        expect(request.getAttribute("javax.servlet.include.request_uri")).andReturn("/test_authc");
        expect(request.getAttribute("javax.servlet.include.request_uri")).andReturn("/test_custom_filter");
        expect(request.getAttribute("javax.servlet.include.request_uri")).andReturn("/test_perms");
        expect(request.getAttribute("javax.servlet.include.request_uri")).andReturn("/multiple_configs");
        replay(servletContext, request);
        Injector injector = Guice.createInjector(new ShiroWebModule(servletContext) {
            @Override
            protected void configureShiroWeb() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
                expose(FilterChainResolver.class);
                this.addFilterChain("/test_authc/**", AUTHC);
                this.addFilterChain("/test_custom_filter/**", Key.get(ShiroWebModuleTest.CustomFilter.class));
                this.addFilterChain("/test_perms/**", config(PERMS, "remote:invoke:lan,wan"));
                this.addFilterChain("/multiple_configs/**", AUTHC, config(ROLES, "b2bClient"), config(PERMS, "remote:invoke:lan,wan"));
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }
        });
        FilterChainResolver resolver = injector.getInstance(FilterChainResolver.class);
        Assert.assertThat(resolver, instanceOf(SimpleFilterChainResolver.class));
        SimpleFilterChainResolver simpleFilterChainResolver = ((SimpleFilterChainResolver) (resolver));
        // test the /test_authc resource
        FilterChain filterChain = simpleFilterChainResolver.getChain(request, null, null);
        Assert.assertThat(filterChain, instanceOf(SimpleFilterChain.class));
        Filter nextFilter = getNextFilter(((SimpleFilterChain) (filterChain)));
        Assert.assertThat(nextFilter, instanceOf(FormAuthenticationFilter.class));
        // test the /test_custom_filter resource
        filterChain = simpleFilterChainResolver.getChain(request, null, null);
        Assert.assertThat(filterChain, instanceOf(SimpleFilterChain.class));
        nextFilter = getNextFilter(((SimpleFilterChain) (filterChain)));
        Assert.assertThat(nextFilter, instanceOf(ShiroWebModuleTest.CustomFilter.class));
        // test the /test_perms resource
        filterChain = simpleFilterChainResolver.getChain(request, null, null);
        Assert.assertThat(filterChain, instanceOf(SimpleFilterChain.class));
        nextFilter = getNextFilter(((SimpleFilterChain) (filterChain)));
        Assert.assertThat(nextFilter, instanceOf(PermissionsAuthorizationFilter.class));
        // test the /multiple_configs resource
        filterChain = simpleFilterChainResolver.getChain(request, null, null);
        Assert.assertThat(filterChain, instanceOf(SimpleFilterChain.class));
        Assert.assertThat(getNextFilter(((SimpleFilterChain) (filterChain))), instanceOf(FormAuthenticationFilter.class));
        Assert.assertThat(getNextFilter(((SimpleFilterChain) (filterChain))), instanceOf(RolesAuthorizationFilter.class));
        Assert.assertThat(getNextFilter(((SimpleFilterChain) (filterChain))), instanceOf(PermissionsAuthorizationFilter.class));
        verify(servletContext, request);
    }

    public static class MyDefaultWebSecurityManager extends DefaultWebSecurityManager {
        @Inject
        public MyDefaultWebSecurityManager(Collection<Realm> realms) {
            super(realms);
        }
    }

    public static class MyDefaultWebSessionManager extends DefaultWebSessionManager {}

    public static class MyWebEnvironment extends WebGuiceEnvironment {
        @Inject
        MyWebEnvironment(FilterChainResolver filterChainResolver, @Named(NAME)
        ServletContext servletContext, WebSecurityManager securityManager) {
            super(filterChainResolver, servletContext, securityManager);
        }
    }

    public static class CustomFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        }

        @Override
        public void destroy() {
        }
    }
}

