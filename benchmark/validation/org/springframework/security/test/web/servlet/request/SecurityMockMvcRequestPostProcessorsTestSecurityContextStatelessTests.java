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
package org.springframework.security.test.web.servlet.request;


import SessionCreationPolicy.STATELESS;
import javax.servlet.Filter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SecurityMockMvcRequestPostProcessorsTestSecurityContextStatelessTests.Config.class)
@WebAppConfiguration
public class SecurityMockMvcRequestPostProcessorsTestSecurityContextStatelessTests {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvc mvc;

    @Test
    @WithMockUser
    public void testSecurityContextWithMockUserWorksWithStateless() throws Exception {
        mvc.perform(get("/")).andExpect(status().is2xxSuccessful());
    }

    @EnableWebSecurity
    @EnableWebMvc
    static class Config extends WebSecurityConfigurerAdapter {
        // @formatter:off
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            http.sessionManagement().sessionCreationPolicy(STATELESS);
        }

        // @formatter:on
        // @formatter:off
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication();
        }

        // @formatter:on
        @RestController
        static class Controller {
            @RequestMapping
            public String hello() {
                return "Hello";
            }
        }
    }
}

