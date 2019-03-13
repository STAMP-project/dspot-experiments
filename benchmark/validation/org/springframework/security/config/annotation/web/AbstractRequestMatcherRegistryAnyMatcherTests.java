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
package org.springframework.security.config.annotation.web;


import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


/**
 * Tests for {@link AbstractRequestMatcherRegistry}.
 *
 * @author Ankur Pathak
 */
public class AbstractRequestMatcherRegistryAnyMatcherTests {
    @EnableWebSecurity
    static class AntMatchersAfterAnyRequestConfig extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated().antMatchers("/demo/**").permitAll();
        }
    }

    @Test(expected = BeanCreationException.class)
    public void antMatchersCanNotWorkAfterAnyRequest() {
        loadConfig(AbstractRequestMatcherRegistryAnyMatcherTests.AntMatchersAfterAnyRequestConfig.class);
    }

    @EnableWebSecurity
    static class MvcMatchersAfterAnyRequestConfig extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated().mvcMatchers("/demo/**").permitAll();
        }
    }

    @Test(expected = BeanCreationException.class)
    public void mvcMatchersCanNotWorkAfterAnyRequest() {
        loadConfig(AbstractRequestMatcherRegistryAnyMatcherTests.MvcMatchersAfterAnyRequestConfig.class);
    }

    @EnableWebSecurity
    static class RegexMatchersAfterAnyRequestConfig extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated().regexMatchers(".*").permitAll();
        }
    }

    @Test(expected = BeanCreationException.class)
    public void regexMatchersCanNotWorkAfterAnyRequest() {
        loadConfig(AbstractRequestMatcherRegistryAnyMatcherTests.RegexMatchersAfterAnyRequestConfig.class);
    }

    @EnableWebSecurity
    static class AnyRequestAfterItselfConfig extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated().anyRequest().permitAll();
        }
    }

    @Test(expected = BeanCreationException.class)
    public void anyRequestCanNotWorkAfterItself() {
        loadConfig(AbstractRequestMatcherRegistryAnyMatcherTests.AnyRequestAfterItselfConfig.class);
    }

    @EnableWebSecurity
    static class RequestMatchersAfterAnyRequestConfig extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated().requestMatchers(new AntPathRequestMatcher("/**")).permitAll();
        }
    }

    @Test(expected = BeanCreationException.class)
    public void requestMatchersCanNotWorkAfterAnyRequest() {
        loadConfig(AbstractRequestMatcherRegistryAnyMatcherTests.RequestMatchersAfterAnyRequestConfig.class);
    }
}

