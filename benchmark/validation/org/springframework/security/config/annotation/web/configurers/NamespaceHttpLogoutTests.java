/**
 * Copyright 2002-2019 the original author or authors.
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


import java.util.Objects;
import java.util.function.Predicate;
import javax.servlet.http.HttpSession;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Tests to verify that all the functionality of <logout> attributes is present
 *
 * @author Rob Winch
 * @author Josh Cummings
 */
@RunWith(SpringRunner.class)
@SecurityTestExecutionListeners
public class NamespaceHttpLogoutTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    MockMvc mvc;

    /**
     * http/logout equivalent
     */
    @Test
    @WithMockUser
    public void logoutWhenUsingDefaultsThenMatchesNamespace() throws Exception {
        this.spring.register(NamespaceHttpLogoutTests.HttpLogoutConfig.class).autowire();
        this.mvc.perform(post("/logout").with(csrf())).andExpect(authenticated(false)).andExpect(redirectedUrl("/login?logout")).andExpect(noCookies()).andExpect(session(Objects::isNull));
    }

    @EnableWebSecurity
    static class HttpLogoutConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
        }
    }

    /**
     * http/logout custom
     */
    @Test
    @WithMockUser
    public void logoutWhenUsingVariousCustomizationsMatchesNamespace() throws Exception {
        this.spring.register(NamespaceHttpLogoutTests.CustomHttpLogoutConfig.class).autowire();
        this.mvc.perform(post("/custom-logout").with(csrf())).andExpect(authenticated(false)).andExpect(redirectedUrl("/logout-success")).andExpect(( result) -> assertThat(result.getResponse().getCookies()).hasSize(1)).andExpect(cookie().maxAge("remove", 0)).andExpect(session(Objects::nonNull));
    }

    @EnableWebSecurity
    static class CustomHttpLogoutConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // logout@logout-url (default is /logout)
            // logout@invalidate-session=false (default is true)
            // logout@delete-cookies
            http.logout().deleteCookies("remove").invalidateHttpSession(false).logoutUrl("/custom-logout").logoutSuccessUrl("/logout-success");// logout@success-url (default is /login?logout)

        }
    }

    /**
     * http/logout@success-handler-ref
     */
    @Test
    @WithMockUser
    public void logoutWhenUsingSuccessHandlerRefThenMatchesNamespace() throws Exception {
        this.spring.register(NamespaceHttpLogoutTests.SuccessHandlerRefHttpLogoutConfig.class).autowire();
        this.mvc.perform(post("/logout").with(csrf())).andExpect(authenticated(false)).andExpect(redirectedUrl("/SuccessHandlerRefHttpLogoutConfig")).andExpect(noCookies()).andExpect(session(Objects::isNull));
    }

    @EnableWebSecurity
    static class SuccessHandlerRefHttpLogoutConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            SimpleUrlLogoutSuccessHandler logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
            logoutSuccessHandler.setDefaultTargetUrl("/SuccessHandlerRefHttpLogoutConfig");
            http.logout().logoutSuccessHandler(logoutSuccessHandler);
        }
    }
}

