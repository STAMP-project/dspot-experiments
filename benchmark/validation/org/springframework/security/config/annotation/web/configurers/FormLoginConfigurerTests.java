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


import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.config.users.AuthenticationTestConfiguration;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.test.web.servlet.MockMvc;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
public class FormLoginConfigurerTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void requestCache() throws Exception {
        this.spring.register(FormLoginConfigurerTests.RequestCacheConfig.class, AuthenticationTestConfiguration.class).autowire();
        FormLoginConfigurerTests.RequestCacheConfig config = this.spring.getContext().getBean(FormLoginConfigurerTests.RequestCacheConfig.class);
        this.mockMvc.perform(formLogin()).andExpect(authenticated());
        Mockito.verify(config.requestCache).getRequest(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @EnableWebSecurity
    static class RequestCacheConfig extends WebSecurityConfigurerAdapter {
        private RequestCache requestCache = Mockito.mock(RequestCache.class);

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.formLogin().and().requestCache().requestCache(this.requestCache);
        }
    }

    @Test
    public void requestCacheAsBean() throws Exception {
        this.spring.register(FormLoginConfigurerTests.RequestCacheBeanConfig.class, AuthenticationTestConfiguration.class).autowire();
        RequestCache requestCache = this.spring.getContext().getBean(RequestCache.class);
        this.mockMvc.perform(formLogin()).andExpect(authenticated());
        Mockito.verify(requestCache).getRequest(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @EnableWebSecurity
    static class RequestCacheBeanConfig {
        @Bean
        RequestCache requestCache() {
            return Mockito.mock(RequestCache.class);
        }
    }
}

