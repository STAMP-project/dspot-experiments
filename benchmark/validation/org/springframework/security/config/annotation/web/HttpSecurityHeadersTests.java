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
package org.springframework.security.config.annotation.web;


import HttpHeaders.CACHE_CONTROL;
import HttpHeaders.EXPIRES;
import HttpHeaders.PRAGMA;
import javax.servlet.Filter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class HttpSecurityHeadersTests {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    Filter springSecurityFilterChain;

    MockMvc mockMvc;

    // gh-2953
    // gh-3975
    @Test
    public void headerWhenSpringMvcResourceThenCacheRelatedHeadersReset() throws Exception {
        mockMvc.perform(get("/resources/file.js")).andExpect(status().isOk()).andExpect(header().string(CACHE_CONTROL, "max-age=12345")).andExpect(header().doesNotExist(PRAGMA)).andExpect(header().doesNotExist(EXPIRES));
    }

    @Test
    public void headerWhenNotSpringResourceThenCacheRelatedHeadersSet() throws Exception {
        mockMvc.perform(get("/notresource")).andExpect(header().string(CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate")).andExpect(header().string(PRAGMA, "no-cache")).andExpect(header().string(EXPIRES, "0"));
    }

    @EnableWebSecurity
    static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
        }
    }

    @EnableWebMvc
    @Configuration
    static class WebMvcConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/resources/").setCachePeriod(12345);
        }
    }
}

