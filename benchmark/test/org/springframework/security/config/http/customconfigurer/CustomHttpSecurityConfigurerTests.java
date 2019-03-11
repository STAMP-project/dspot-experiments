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
package org.springframework.security.config.http.customconfigurer;


import HttpServletResponse.SC_OK;
import java.util.Properties;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.FilterChainProxy;


/**
 *
 *
 * @author Rob Winch
 */
public class CustomHttpSecurityConfigurerTests {
    @Autowired
    ConfigurableApplicationContext context;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    MockFilterChain chain;

    @Test
    public void customConfiguerPermitAll() throws Exception {
        loadContext(CustomHttpSecurityConfigurerTests.Config.class);
        request.setPathInfo("/public/something");
        springSecurityFilterChain.doFilter(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(SC_OK);
    }

    @Test
    public void customConfiguerFormLogin() throws Exception {
        loadContext(CustomHttpSecurityConfigurerTests.Config.class);
        request.setPathInfo("/requires-authentication");
        springSecurityFilterChain.doFilter(request, response, chain);
        assertThat(response.getRedirectedUrl()).endsWith("/custom");
    }

    @Test
    public void customConfiguerCustomizeDisablesCsrf() throws Exception {
        loadContext(CustomHttpSecurityConfigurerTests.ConfigCustomize.class);
        request.setPathInfo("/public/something");
        request.setMethod("POST");
        springSecurityFilterChain.doFilter(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(SC_OK);
    }

    @Test
    public void customConfiguerCustomizeFormLogin() throws Exception {
        loadContext(CustomHttpSecurityConfigurerTests.ConfigCustomize.class);
        request.setPathInfo("/requires-authentication");
        springSecurityFilterChain.doFilter(request, response, chain);
        assertThat(response.getRedirectedUrl()).endsWith("/other");
    }

    @EnableWebSecurity
    static class Config extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.apply(CustomConfigurer.customConfigurer()).loginPage("/custom");
        }

        @Bean
        public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
            // Typically externalize this as a properties file
            Properties properties = new Properties();
            properties.setProperty("permitAllPattern", "/public/**");
            PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
            propertyPlaceholderConfigurer.setProperties(properties);
            return propertyPlaceholderConfigurer;
        }
    }

    @EnableWebSecurity
    static class ConfigCustomize extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.apply(CustomConfigurer.customConfigurer()).and().csrf().disable().formLogin().loginPage("/other");
        }

        @Bean
        public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
            // Typically externalize this as a properties file
            Properties properties = new Properties();
            properties.setProperty("permitAllPattern", "/public/**");
            PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
            propertyPlaceholderConfigurer.setProperties(properties);
            return propertyPlaceholderConfigurer;
        }
    }
}

