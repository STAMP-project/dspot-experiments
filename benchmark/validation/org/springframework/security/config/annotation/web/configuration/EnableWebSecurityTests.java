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
package org.springframework.security.config.annotation.web.configuration;


import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.PasswordEncodedUser;
import org.springframework.security.web.debug.DebugFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 *
 *
 * @author Joe Grandja
 */
public class EnableWebSecurityTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void configureWhenOverrideAuthenticationManagerBeanThenAuthenticationManagerBeanRegistered() throws Exception {
        this.spring.register(EnableWebSecurityTests.SecurityConfig.class).autowire();
        AuthenticationManager authenticationManager = this.spring.getContext().getBean(AuthenticationManager.class);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user", "password"));
        assertThat(authentication.isAuthenticated()).isTrue();
    }

    @EnableWebSecurity
    static class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/*").hasRole("USER").and().formLogin();
        }
    }

    @Test
    public void loadConfigWhenChildConfigExtendsSecurityConfigThenSecurityConfigInherited() throws Exception {
        this.spring.register(EnableWebSecurityTests.ChildSecurityConfig.class).autowire();
        this.spring.getContext().getBean("springSecurityFilterChain", DebugFilter.class);
    }

    @Configuration
    static class ChildSecurityConfig extends EnableWebSecurityTests.DebugSecurityConfig {}

    @EnableWebSecurity(debug = true)
    static class DebugSecurityConfig extends WebSecurityConfigurerAdapter {}

    @Test
    public void configureWhenEnableWebMvcThenAuthenticationPrincipalResolvable() throws Exception {
        this.spring.register(EnableWebSecurityTests.AuthenticationPrincipalConfig.class).autowire();
        this.mockMvc.perform(get("/").with(authentication(new TestingAuthenticationToken("user1", "password")))).andExpect(content().string("user1"));
    }

    @EnableWebSecurity
    @EnableWebMvc
    static class AuthenticationPrincipalConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
        }

        @RestController
        static class AuthController {
            @GetMapping("/")
            String principal(@AuthenticationPrincipal
            String principal) {
                return principal;
            }
        }
    }
}

