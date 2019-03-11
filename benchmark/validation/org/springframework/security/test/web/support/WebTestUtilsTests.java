/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.security.test.web.support;


import BeanIds.SPRING_SECURITY_FILTER_CHAIN;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;


@RunWith(MockitoJUnitRunner.class)
public class WebTestUtilsTests {
    @Mock
    private SecurityContextRepository contextRepo;

    @Mock
    private CsrfTokenRepository csrfRepo;

    private MockHttpServletRequest request;

    private ConfigurableApplicationContext context;

    @Test
    public void getCsrfTokenRepositorytNoWac() {
        assertThat(WebTestUtils.getCsrfTokenRepository(this.request)).isInstanceOf(HttpSessionCsrfTokenRepository.class);
    }

    @Test
    public void getCsrfTokenRepositorytNoSecurity() {
        loadConfig(WebTestUtilsTests.Config.class);
        assertThat(WebTestUtils.getCsrfTokenRepository(this.request)).isInstanceOf(HttpSessionCsrfTokenRepository.class);
    }

    @Test
    public void getCsrfTokenRepositorytSecurityNoCsrf() {
        loadConfig(WebTestUtilsTests.SecurityNoCsrfConfig.class);
        assertThat(WebTestUtils.getCsrfTokenRepository(this.request)).isInstanceOf(HttpSessionCsrfTokenRepository.class);
    }

    @Test
    public void getCsrfTokenRepositorytSecurityCustomRepo() {
        WebTestUtilsTests.CustomSecurityConfig.CONTEXT_REPO = this.contextRepo;
        WebTestUtilsTests.CustomSecurityConfig.CSRF_REPO = this.csrfRepo;
        loadConfig(WebTestUtilsTests.CustomSecurityConfig.class);
        assertThat(WebTestUtils.getCsrfTokenRepository(this.request)).isSameAs(this.csrfRepo);
    }

    // getSecurityContextRepository
    @Test
    public void getSecurityContextRepositoryNoWac() {
        assertThat(WebTestUtils.getSecurityContextRepository(this.request)).isInstanceOf(HttpSessionSecurityContextRepository.class);
    }

    @Test
    public void getSecurityContextRepositoryNoSecurity() {
        loadConfig(WebTestUtilsTests.Config.class);
        assertThat(WebTestUtils.getSecurityContextRepository(this.request)).isInstanceOf(HttpSessionSecurityContextRepository.class);
    }

    @Test
    public void getSecurityContextRepositorySecurityNoCsrf() {
        loadConfig(WebTestUtilsTests.SecurityNoCsrfConfig.class);
        assertThat(WebTestUtils.getSecurityContextRepository(this.request)).isInstanceOf(HttpSessionSecurityContextRepository.class);
    }

    @Test
    public void getSecurityContextRepositorySecurityCustomRepo() {
        WebTestUtilsTests.CustomSecurityConfig.CONTEXT_REPO = this.contextRepo;
        WebTestUtilsTests.CustomSecurityConfig.CSRF_REPO = this.csrfRepo;
        loadConfig(WebTestUtilsTests.CustomSecurityConfig.class);
        assertThat(WebTestUtils.getSecurityContextRepository(this.request)).isSameAs(this.contextRepo);
    }

    // gh-3343
    @Test
    public void findFilterNoMatchingFilters() {
        loadConfig(WebTestUtilsTests.PartialSecurityConfig.class);
        assertThat(WebTestUtils.findFilter(this.request, SecurityContextPersistenceFilter.class)).isNull();
    }

    @Test
    public void findFilterNoSpringSecurityFilterChainInContext() {
        loadConfig(WebTestUtilsTests.NoSecurityConfig.class);
        CsrfFilter toFind = new CsrfFilter(new HttpSessionCsrfTokenRepository());
        FilterChainProxy springSecurityFilterChain = new FilterChainProxy(new org.springframework.security.web.DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE, toFind));
        this.request.getServletContext().setAttribute(SPRING_SECURITY_FILTER_CHAIN, springSecurityFilterChain);
        assertThat(WebTestUtils.findFilter(this.request, toFind.getClass())).isEqualTo(toFind);
    }

    @Test
    public void findFilterExplicitWithSecurityFilterInContext() {
        loadConfig(WebTestUtilsTests.SecurityConfigWithDefaults.class);
        CsrfFilter toFind = new CsrfFilter(new HttpSessionCsrfTokenRepository());
        FilterChainProxy springSecurityFilterChain = new FilterChainProxy(new org.springframework.security.web.DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE, toFind));
        this.request.getServletContext().setAttribute(SPRING_SECURITY_FILTER_CHAIN, springSecurityFilterChain);
        assertThat(WebTestUtils.findFilter(this.request, toFind.getClass())).isSameAs(toFind);
    }

    @Configuration
    static class Config {}

    @EnableWebSecurity
    static class SecurityNoCsrfConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
        }
    }

    // @formatter:on
    @EnableWebSecurity
    static class CustomSecurityConfig extends WebSecurityConfigurerAdapter {
        static CsrfTokenRepository CSRF_REPO;

        static SecurityContextRepository CONTEXT_REPO;

        // @formatter:off
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().csrfTokenRepository(WebTestUtilsTests.CustomSecurityConfig.CSRF_REPO).and().securityContext().securityContextRepository(WebTestUtilsTests.CustomSecurityConfig.CONTEXT_REPO);
        }
    }

    // @formatter:on
    @EnableWebSecurity
    static class PartialSecurityConfig extends WebSecurityConfigurerAdapter {
        // @formatter:off
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/willnotmatchthis");
        }
    }

    @Configuration
    static class NoSecurityConfig {}

    @EnableWebSecurity
    static class SecurityConfigWithDefaults extends WebSecurityConfigurerAdapter {}
}

