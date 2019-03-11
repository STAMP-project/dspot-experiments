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
package org.springframework.security.config.annotation.web.configurers;


import HttpHeaders.ACCEPT;
import MediaType.ALL;
import MediaType.APPLICATION_JSON;
import MediaType.TEXT_HTML;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Tests for {@link RequestCacheConfigurer}
 *
 * @author Rob Winch
 * @author Josh Cummings
 */
public class RequestCacheConfigurerTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    MockMvc mvc;

    @Test
    public void configureWhenRegisteringObjectPostProcessorThenInvokedOnExceptionTranslationFilter() {
        this.spring.register(RequestCacheConfigurerTests.ObjectPostProcessorConfig.class, RequestCacheConfigurerTests.DefaultSecurityConfig.class).autowire();
        Mockito.verify(RequestCacheConfigurerTests.ObjectPostProcessorConfig.objectPostProcessor).postProcess(ArgumentMatchers.any(RequestCacheAwareFilter.class));
    }

    @EnableWebSecurity
    static class ObjectPostProcessorConfig extends WebSecurityConfigurerAdapter {
        static ObjectPostProcessor<Object> objectPostProcessor = Mockito.spy(RequestCacheConfigurerTests.ReflectingObjectPostProcessor.class);

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.requestCache();
            // @formatter:on
        }

        @Bean
        static ObjectPostProcessor<Object> objectPostProcessor() {
            return RequestCacheConfigurerTests.ObjectPostProcessorConfig.objectPostProcessor;
        }
    }

    static class ReflectingObjectPostProcessor implements ObjectPostProcessor<Object> {
        @Override
        public <O> O postProcess(O object) {
            return object;
        }
    }

    @Test
    public void getWhenInvokingExceptionHandlingTwiceThenOriginalEntryPointUsed() throws Exception {
        this.spring.register(RequestCacheConfigurerTests.InvokeTwiceDoesNotOverrideConfig.class).autowire();
        this.mvc.perform(get("/"));
        Mockito.verify(RequestCacheConfigurerTests.InvokeTwiceDoesNotOverrideConfig.requestCache).getMatchingRequest(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @EnableWebSecurity
    static class InvokeTwiceDoesNotOverrideConfig extends WebSecurityConfigurerAdapter {
        static RequestCache requestCache = Mockito.mock(RequestCache.class);

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.requestCache().requestCache(RequestCacheConfigurerTests.InvokeTwiceDoesNotOverrideConfig.requestCache).and().requestCache();
            // @formatter:on
        }
    }

    @Test
    public void getWhenBookmarkedUrlIsFaviconIcoThenPostAuthenticationRedirectsToRoot() throws Exception {
        this.spring.register(RequestCacheConfigurerTests.RequestCacheDefaultsConfig.class, RequestCacheConfigurerTests.DefaultSecurityConfig.class).autowire();
        MockHttpSession session = ((MockHttpSession) (this.mvc.perform(get("/favicon.ico")).andExpect(redirectedUrl("http://localhost/login")).andReturn().getRequest().getSession()));
        this.mvc.perform(RequestCacheConfigurerTests.formLogin(session)).andExpect(redirectedUrl("/"));// ignores favicon.ico

    }

    @Test
    public void getWhenBookmarkedUrlIsFaviconPngThenPostAuthenticationRedirectsToRoot() throws Exception {
        this.spring.register(RequestCacheConfigurerTests.RequestCacheDefaultsConfig.class, RequestCacheConfigurerTests.DefaultSecurityConfig.class).autowire();
        MockHttpSession session = ((MockHttpSession) (this.mvc.perform(get("/favicon.png")).andExpect(redirectedUrl("http://localhost/login")).andReturn().getRequest().getSession()));
        this.mvc.perform(RequestCacheConfigurerTests.formLogin(session)).andExpect(redirectedUrl("/"));// ignores favicon.png

    }

    // SEC-2321
    @Test
    public void getWhenBookmarkedRequestIsApplicationJsonThenPostAuthenticationRedirectsToRoot() throws Exception {
        this.spring.register(RequestCacheConfigurerTests.RequestCacheDefaultsConfig.class, RequestCacheConfigurerTests.DefaultSecurityConfig.class).autowire();
        MockHttpSession session = ((MockHttpSession) (this.mvc.perform(get("/messages").header(ACCEPT, APPLICATION_JSON)).andExpect(redirectedUrl("http://localhost/login")).andReturn().getRequest().getSession()));
        this.mvc.perform(RequestCacheConfigurerTests.formLogin(session)).andExpect(redirectedUrl("/"));// ignores application/json

        // This is desirable since JSON requests are typically not invoked directly from the browser and we don't want the browser to replay them
    }

    // SEC-2321
    @Test
    public void getWhenBookmarkedRequestIsXRequestedWithThenPostAuthenticationRedirectsToRoot() throws Exception {
        this.spring.register(RequestCacheConfigurerTests.RequestCacheDefaultsConfig.class, RequestCacheConfigurerTests.DefaultSecurityConfig.class).autowire();
        MockHttpSession session = ((MockHttpSession) (this.mvc.perform(get("/messages").header("X-Requested-With", "XMLHttpRequest")).andExpect(redirectedUrl("http://localhost/login")).andReturn().getRequest().getSession()));
        this.mvc.perform(RequestCacheConfigurerTests.formLogin(session)).andExpect(redirectedUrl("/"));
        // This is desirable since XHR requests are typically not invoked directly from the browser and we don't want the browser to replay them
    }

    @Test
    public void getWhenBookmarkedRequestIsAllMediaTypeThenPostAuthenticationRemembers() throws Exception {
        this.spring.register(RequestCacheConfigurerTests.RequestCacheDefaultsConfig.class, RequestCacheConfigurerTests.DefaultSecurityConfig.class).autowire();
        MockHttpSession session = ((MockHttpSession) (this.mvc.perform(get("/messages").header(ACCEPT, ALL)).andExpect(redirectedUrl("http://localhost/login")).andReturn().getRequest().getSession()));
        this.mvc.perform(RequestCacheConfigurerTests.formLogin(session)).andExpect(redirectedUrl("http://localhost/messages"));
    }

    @Test
    public void getWhenBookmarkedRequestIsTextHtmlThenPostAuthenticationRemembers() throws Exception {
        this.spring.register(RequestCacheConfigurerTests.RequestCacheDefaultsConfig.class, RequestCacheConfigurerTests.DefaultSecurityConfig.class).autowire();
        MockHttpSession session = ((MockHttpSession) (this.mvc.perform(get("/messages").header(ACCEPT, TEXT_HTML)).andExpect(redirectedUrl("http://localhost/login")).andReturn().getRequest().getSession()));
        this.mvc.perform(RequestCacheConfigurerTests.formLogin(session)).andExpect(redirectedUrl("http://localhost/messages"));
    }

    @Test
    public void getWhenBookmarkedRequestIsChromeThenPostAuthenticationRemembers() throws Exception {
        this.spring.register(RequestCacheConfigurerTests.RequestCacheDefaultsConfig.class, RequestCacheConfigurerTests.DefaultSecurityConfig.class).autowire();
        MockHttpSession session = ((MockHttpSession) (this.mvc.perform(get("/messages").header(ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")).andExpect(redirectedUrl("http://localhost/login")).andReturn().getRequest().getSession()));
        this.mvc.perform(RequestCacheConfigurerTests.formLogin(session)).andExpect(redirectedUrl("http://localhost/messages"));
    }

    @Test
    public void getWhenBookmarkedRequestIsRequestedWithAndroidThenPostAuthenticationRemembers() throws Exception {
        this.spring.register(RequestCacheConfigurerTests.RequestCacheDefaultsConfig.class, RequestCacheConfigurerTests.DefaultSecurityConfig.class).autowire();
        MockHttpSession session = ((MockHttpSession) (this.mvc.perform(get("/messages").header("X-Requested-With", "com.android")).andExpect(redirectedUrl("http://localhost/login")).andReturn().getRequest().getSession()));
        this.mvc.perform(RequestCacheConfigurerTests.formLogin(session)).andExpect(redirectedUrl("http://localhost/messages"));
    }

    @EnableWebSecurity
    static class RequestCacheDefaultsConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated().and().formLogin();
        }
    }

    // gh-6102
    @Test
    public void getWhenRequestCacheIsDisabledThenExceptionTranslationFilterDoesNotStoreRequest() throws Exception {
        this.spring.register(RequestCacheConfigurerTests.RequestCacheDisabledConfig.class, ExceptionHandlingConfigurerTests.DefaultSecurityConfig.class).autowire();
        MockHttpSession session = ((MockHttpSession) (this.mvc.perform(get("/bob")).andReturn().getRequest().getSession()));
        this.mvc.perform(RequestCacheConfigurerTests.formLogin(session)).andExpect(redirectedUrl("/"));
    }

    @EnableWebSecurity
    static class RequestCacheDisabledConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            http.requestCache().disable();
        }
    }

    @EnableWebSecurity
    static class DefaultSecurityConfig {
        @Bean
        public InMemoryUserDetailsManager userDetailsManager() {
            return new InMemoryUserDetailsManager(User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build());
        }
    }
}

