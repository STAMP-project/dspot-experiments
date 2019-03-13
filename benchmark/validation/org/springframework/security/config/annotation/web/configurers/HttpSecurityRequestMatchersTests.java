/**
 * Copyright 2002-2016 the original author or authors.
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


import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_UNAUTHORIZED;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 *
 *
 * @author Rob Winch
 */
public class HttpSecurityRequestMatchersTests {
    AnnotationConfigWebApplicationContext context;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    MockFilterChain chain;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    @Test
    public void mvcMatcher() throws Exception {
        loadConfig(HttpSecurityRequestMatchersTests.MvcMatcherConfig.class);
        this.request.setServletPath("/path");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
        setup();
        this.request.setServletPath("/path.html");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
        setup();
        this.request.setServletPath("/path/");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
    }

    @Test
    public void mvcMatcherGetFiltersNoUnsupportedMethodExceptionFromDummyRequest() {
        loadConfig(HttpSecurityRequestMatchersTests.MvcMatcherConfig.class);
        assertThat(springSecurityFilterChain.getFilters("/path")).isNotEmpty();
    }

    @EnableWebSecurity
    @Configuration
    @EnableWebMvc
    static class MvcMatcherConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.mvcMatcher("/path").httpBasic().and().authorizeRequests().anyRequest().denyAll();
            // @formatter:on
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            // @formatter:off
            auth.inMemoryAuthentication();
            // @formatter:on
        }

        @RestController
        static class PathController {
            @RequestMapping("/path")
            public String path() {
                return "path";
            }
        }
    }

    @Test
    public void requestMatchersMvcMatcher() throws Exception {
        loadConfig(HttpSecurityRequestMatchersTests.RequestMatchersMvcMatcherConfig.class);
        this.request.setServletPath("/path");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
        setup();
        this.request.setServletPath("/path.html");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
        setup();
        this.request.setServletPath("/path/");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
    }

    @EnableWebSecurity
    @Configuration
    @EnableWebMvc
    static class RequestMatchersMvcMatcherConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.requestMatchers().mvcMatchers("/path").and().httpBasic().and().authorizeRequests().anyRequest().denyAll();
            // @formatter:on
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            // @formatter:off
            auth.inMemoryAuthentication();
            // @formatter:on
        }

        @RestController
        static class PathController {
            @RequestMapping("/path")
            public String path() {
                return "path";
            }
        }
    }

    @Test
    public void requestMatchersMvcMatcherServletPath() throws Exception {
        loadConfig(HttpSecurityRequestMatchersTests.RequestMatchersMvcMatcherServeltPathConfig.class);
        this.request.setServletPath("/spring");
        this.request.setRequestURI("/spring/path");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
        setup();
        this.request.setServletPath("");
        this.request.setRequestURI("/path");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_OK);
        setup();
        this.request.setServletPath("/other");
        this.request.setRequestURI("/other/path");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_OK);
    }

    @EnableWebSecurity
    @Configuration
    @EnableWebMvc
    static class RequestMatchersMvcMatcherServeltPathConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.requestMatchers().mvcMatchers("/path").servletPath("/spring").mvcMatchers("/never-match").and().httpBasic().and().authorizeRequests().anyRequest().denyAll();
            // @formatter:on
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            // @formatter:off
            auth.inMemoryAuthentication();
            // @formatter:on
        }

        @RestController
        static class PathController {
            @RequestMapping("/path")
            public String path() {
                return "path";
            }
        }
    }
}

