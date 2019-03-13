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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.userdetails.PasswordEncodedUser;
import org.springframework.test.web.servlet.MockMvc;


/**
 *
 *
 * @author Rob Winch
 */
public class NamespaceAuthenticationManagerTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void authenticationMangerWhenDefaultThenEraseCredentialsIsTrue() throws Exception {
        this.spring.register(NamespaceAuthenticationManagerTests.EraseCredentialsTrueDefaultConfig.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(authenticated().withAuthentication(( a) -> assertThat(a.getCredentials()).isNull()));
        this.mockMvc.perform(formLogin()).andExpect(authenticated().withAuthentication(( a) -> assertThat(a.getCredentials()).isNull()));
        // no exception due to username being cleared out
    }

    @EnableWebSecurity
    static class EraseCredentialsTrueDefaultConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }
    }

    @Test
    public void authenticationMangerWhenEraseCredentialsIsFalseThenCredentialsNotNull() throws Exception {
        this.spring.register(NamespaceAuthenticationManagerTests.EraseCredentialsFalseConfig.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(authenticated().withAuthentication(( a) -> assertThat(a.getCredentials()).isNotNull()));
        this.mockMvc.perform(formLogin()).andExpect(authenticated().withAuthentication(( a) -> assertThat(a.getCredentials()).isNotNull()));
        // no exception due to username being cleared out
    }

    @EnableWebSecurity
    static class EraseCredentialsFalseConfig extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.eraseCredentials(false).inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }
    }

    // SEC-2533
    @Test
    public void authenticationManagerWhenGlobalAndEraseCredentialsIsFalseThenCredentialsNotNull() throws Exception {
        this.spring.register(NamespaceAuthenticationManagerTests.GlobalEraseCredentialsFalseConfig.class).autowire();
        this.mockMvc.perform(formLogin()).andExpect(authenticated().withAuthentication(( a) -> assertThat(a.getCredentials()).isNotNull()));
    }

    @EnableWebSecurity
    static class GlobalEraseCredentialsFalseConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.eraseCredentials(false).inMemoryAuthentication().withUser(PasswordEncodedUser.user());
        }
    }
}

