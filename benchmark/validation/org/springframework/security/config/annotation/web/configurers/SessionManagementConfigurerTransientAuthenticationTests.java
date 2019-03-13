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


import SessionCreationPolicy.ALWAYS;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Transient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


/**
 *
 *
 * @author Josh Cummings
 */
public class SessionManagementConfigurerTransientAuthenticationTests {
    @Autowired
    MockMvc mvc;

    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Test
    public void postWhenTransientAuthenticationThenNoSessionCreated() throws Exception {
        this.spring.register(SessionManagementConfigurerTransientAuthenticationTests.WithTransientAuthenticationConfig.class).autowire();
        MvcResult result = this.mvc.perform(post("/login")).andReturn();
        assertThat(result.getRequest().getSession(false)).isNull();
    }

    @Test
    public void postWhenTransientAuthenticationThenAlwaysSessionOverrides() throws Exception {
        this.spring.register(SessionManagementConfigurerTransientAuthenticationTests.AlwaysCreateSessionConfig.class).autowire();
        MvcResult result = this.mvc.perform(post("/login")).andReturn();
        assertThat(result.getRequest().getSession(false)).isNotNull();
    }

    @EnableWebSecurity
    static class WithTransientAuthenticationConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            http.csrf().disable();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(new SessionManagementConfigurerTransientAuthenticationTests.TransientAuthenticationProvider());
        }
    }

    @EnableWebSecurity
    static class AlwaysCreateSessionConfig extends SessionManagementConfigurerTransientAuthenticationTests.WithTransientAuthenticationConfig {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement().sessionCreationPolicy(ALWAYS);
        }
    }

    static class TransientAuthenticationProvider implements AuthenticationProvider {
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            return new SessionManagementConfigurerTransientAuthenticationTests.SomeTransientAuthentication();
        }

        @Override
        public boolean supports(Class<?> authentication) {
            return true;
        }
    }

    @Transient
    static class SomeTransientAuthentication extends AbstractAuthenticationToken {
        SomeTransientAuthentication() {
            super(null);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }
    }
}

