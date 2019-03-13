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
package org.springframework.security.test.context.showcase;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.showcase.service.HelloMessageService;
import org.springframework.security.test.context.showcase.service.MessageService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WithUserDetailsTests.Config.class)
public class WithUserDetailsTests {
    @Autowired
    private MessageService messageService;

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void getMessageUnauthenticated() {
        messageService.getMessage();
    }

    @Test
    @WithUserDetails
    public void getMessageWithUserDetails() {
        String message = messageService.getMessage();
        assertThat(message).contains("user");
        assertThat(getPrincipal()).isInstanceOf(CustomUserDetails.class);
    }

    @Test
    @WithUserDetails("customUsername")
    public void getMessageWithUserDetailsCustomUsername() {
        String message = messageService.getMessage();
        assertThat(message).contains("customUsername");
        assertThat(getPrincipal()).isInstanceOf(CustomUserDetails.class);
    }

    @Test
    @WithUserDetails(value = "customUsername", userDetailsServiceBeanName = "myUserDetailsService")
    public void getMessageWithUserDetailsServiceBeanName() {
        String message = messageService.getMessage();
        assertThat(message).contains("customUsername");
        assertThat(getPrincipal()).isInstanceOf(CustomUserDetails.class);
    }

    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @ComponentScan(basePackageClasses = HelloMessageService.class)
    static class Config {
        // @formatter:off
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(myUserDetailsService());
        }

        // @formatter:on
        @Bean
        public UserDetailsService myUserDetailsService() {
            return new WithUserDetailsTests.CustomUserDetailsService();
        }
    }

    static class CustomUserDetailsService implements UserDetailsService {
        public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
            return new CustomUserDetails("name", username);
        }
    }
}

