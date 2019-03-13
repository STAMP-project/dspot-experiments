/**
 * Copyright 2012-2016 the original author or authors.
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
package org.springframework.security.config.annotation.web.builders;


import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_UNAUTHORIZED;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
public class WebSecurityTests {
    AnnotationConfigWebApplicationContext context;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    MockFilterChain chain;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    @Test
    public void ignoringMvcMatcher() throws Exception {
        loadConfig(WebSecurityTests.MvcMatcherConfig.class);
        this.request.setRequestURI("/path");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_OK);
        setup();
        this.request.setRequestURI("/path.html");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_OK);
        setup();
        this.request.setRequestURI("/path/");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_OK);
        setup();
        this.request.setRequestURI("/other");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
    }

    @EnableWebSecurity
    @Configuration
    @EnableWebMvc
    static class MvcMatcherConfig extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(WebSecurity web) throws Exception {
            // @formatter:off
            web.ignoring().mvcMatchers("/path");
            // @formatter:on
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.httpBasic().and().authorizeRequests().anyRequest().denyAll();
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
    public void ignoringMvcMatcherServletPath() throws Exception {
        loadConfig(WebSecurityTests.MvcMatcherServletPathConfig.class);
        this.request.setServletPath("/spring");
        this.request.setRequestURI("/spring/path");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_OK);
        setup();
        this.request.setServletPath("/spring");
        this.request.setRequestURI("/spring/path.html");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_OK);
        setup();
        this.request.setServletPath("/spring");
        this.request.setRequestURI("/spring/path/");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_OK);
        setup();
        this.request.setServletPath("/other");
        this.request.setRequestURI("/other/path");
        this.springSecurityFilterChain.doFilter(this.request, this.response, this.chain);
        assertThat(this.response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
    }

    @EnableWebSecurity
    @Configuration
    @EnableWebMvc
    static class MvcMatcherServletPathConfig extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(WebSecurity web) throws Exception {
            // @formatter:off
            web.ignoring().mvcMatchers("/path").servletPath("/spring").mvcMatchers("/notused");
            // @formatter:on
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.httpBasic().and().authorizeRequests().anyRequest().denyAll();
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

