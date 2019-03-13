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


import HttpMethod.POST;
import HttpServletResponse.SC_FORBIDDEN;
import HttpServletResponse.SC_OK;
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
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


/**
 *
 *
 * @author Rob Winch
 */
public class HttpSecurityAntMatchersTests {
    AnnotationConfigWebApplicationContext context;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    MockFilterChain chain;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    // SEC-3135
    @Test
    public void antMatchersMethodAndNoPatterns() throws Exception {
        loadConfig(HttpSecurityAntMatchersTests.AntMatchersNoPatternsConfig.class);
        request.setMethod("POST");
        springSecurityFilterChain.doFilter(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(SC_FORBIDDEN);
    }

    @EnableWebSecurity
    @Configuration
    static class AntMatchersNoPatternsConfig extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers().antMatchers(POST).and().authorizeRequests().anyRequest().denyAll();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication();
        }
    }

    // SEC-3135
    @Test
    public void antMatchersMethodAndEmptyPatterns() throws Exception {
        loadConfig(HttpSecurityAntMatchersTests.AntMatchersEmptyPatternsConfig.class);
        request.setMethod("POST");
        springSecurityFilterChain.doFilter(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(SC_OK);
    }

    @EnableWebSecurity
    @Configuration
    static class AntMatchersEmptyPatternsConfig extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers().antMatchers("/never/").antMatchers(POST, new String[0]).and().authorizeRequests().anyRequest().denyAll();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication();
        }
    }
}

