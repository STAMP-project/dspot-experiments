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
package org.springframework.security.test.web.servlet.response;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SecurityMockWithAuthoritiesMvcResultMatchersTests.Config.class)
@WebAppConfiguration
public class SecurityMockWithAuthoritiesMvcResultMatchersTests {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Test
    public void withAuthoritiesNotOrderSensitive() throws Exception {
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin()).andExpect(SecurityMockMvcResultMatchers.authenticated().withAuthorities(grantedAuthorities));
    }

    @Test(expected = AssertionError.class)
    public void withAuthoritiesFailsIfNotAllRoles() throws Exception {
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin()).andExpect(SecurityMockMvcResultMatchers.authenticated().withAuthorities(grantedAuthorities));
    }

    @EnableWebSecurity
    @EnableWebMvc
    static class Config extends WebSecurityConfigurerAdapter {
        // @formatter:off
        @Bean
        public UserDetailsService userDetailsService() {
            UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("ADMIN", "SELLER").build();
            return new org.springframework.security.provisioning.InMemoryUserDetailsManager(user);
        }

        // @formatter:on
        @RestController
        static class Controller {
            @RequestMapping("/")
            public String ok() {
                return "ok";
            }
        }
    }
}

