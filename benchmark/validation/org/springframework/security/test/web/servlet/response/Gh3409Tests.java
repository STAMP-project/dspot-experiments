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
package org.springframework.security.test.web.servlet.response;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 *
 *
 * @author Rob Winch
 * @since 4.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class Gh3409Tests {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    // gh-3409
    @Test
    public void unauthenticatedAnonymousUser() throws Exception {
        // @formatter:off
        this.mockMvc.perform(get("/public/").with(SecurityMockMvcRequestPostProcessors.securityContext(new SecurityContextImpl())));
        this.mockMvc.perform(get("/public/")).andExpect(SecurityMockMvcResultMatchers.unauthenticated());
        // @formatter:on
    }

    @Test
    public void unauthenticatedNullAuthenitcation() throws Exception {
        // @formatter:off
        this.mockMvc.perform(get("/").with(SecurityMockMvcRequestPostProcessors.securityContext(new SecurityContextImpl())));
        this.mockMvc.perform(get("/")).andExpect(SecurityMockMvcResultMatchers.unauthenticated());
        // @formatter:on
    }

    @EnableWebSecurity
    @EnableWebMvc
    static class Config extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.authorizeRequests().antMatchers("/public/**").permitAll().anyRequest().authenticated().and().formLogin().and().httpBasic();
            // @formatter:on
        }
    }
}

