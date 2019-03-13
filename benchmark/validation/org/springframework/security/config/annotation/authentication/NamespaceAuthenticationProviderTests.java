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
package org.springframework.security.config.annotation.authentication;


import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.userdetails.PasswordEncodedUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;


/**
 *
 *
 * @author Rob Winch
 */
public class NamespaceAuthenticationProviderTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    private MockMvc mockMvc;

    // authentication-provider@ref
    @Test
    public void authenticationProviderRef() throws Exception {
        this.spring.register(NamespaceAuthenticationProviderTests.AuthenticationProviderRefConfig.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(authenticated().withUsername("user"));
    }

    @EnableWebSecurity
    static class AuthenticationProviderRefConfig extends WebSecurityConfigurerAdapter {
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider());
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider result = new DaoAuthenticationProvider();
            result.setUserDetailsService(new org.springframework.security.provisioning.InMemoryUserDetailsManager(PasswordEncodedUser.user()));
            return result;
        }
    }

    // authentication-provider@user-service-ref
    @Test
    public void authenticationProviderUserServiceRef() throws Exception {
        this.spring.register(NamespaceAuthenticationProviderTests.AuthenticationProviderRefConfig.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(authenticated().withUsername("user"));
    }

    @EnableWebSecurity
    static class UserServiceRefConfig extends WebSecurityConfigurerAdapter {
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService());
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new org.springframework.security.provisioning.InMemoryUserDetailsManager(PasswordEncodedUser.user());
        }
    }
}

