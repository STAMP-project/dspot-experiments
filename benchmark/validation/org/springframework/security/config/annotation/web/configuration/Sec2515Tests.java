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
package org.springframework.security.config.annotation.web.configuration;


import java.net.URL;
import java.net.URLClassLoader;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


/**
 *
 *
 * @author Joe Grandja
 */
public class Sec2515Tests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    // SEC-2515
    @Test(expected = FatalBeanException.class)
    public void loadConfigWhenAuthenticationManagerNotConfiguredAndRegisterBeanThenThrowFatalBeanException() throws Exception {
        this.spring.register(Sec2515Tests.StackOverflowSecurityConfig.class).autowire();
    }

    @EnableWebSecurity
    static class StackOverflowSecurityConfig extends WebSecurityConfigurerAdapter {
        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    @Test(expected = FatalBeanException.class)
    public void loadConfigWhenAuthenticationManagerNotConfiguredAndRegisterBeanCustomNameThenThrowFatalBeanException() throws Exception {
        this.spring.register(Sec2515Tests.CustomBeanNameStackOverflowSecurityConfig.class).autowire();
    }

    @EnableWebSecurity
    static class CustomBeanNameStackOverflowSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        @Bean(name = "custom")
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    // SEC-2549
    @Test
    public void loadConfigWhenChildClassLoaderSetThenContextLoads() throws Exception {
        Sec2515Tests.CanLoadWithChildConfig.AUTHENTICATION_MANAGER = Mockito.mock(AuthenticationManager.class);
        this.spring.register(Sec2515Tests.CanLoadWithChildConfig.class);
        AnnotationConfigWebApplicationContext context = ((AnnotationConfigWebApplicationContext) (this.spring.getContext()));
        context.setClassLoader(new URLClassLoader(new URL[0], context.getClassLoader()));
        this.spring.autowire();
        assertThat(this.spring.getContext().getBean(AuthenticationManager.class)).isNotNull();
    }

    @EnableWebSecurity
    static class CanLoadWithChildConfig extends WebSecurityConfigurerAdapter {
        static AuthenticationManager AUTHENTICATION_MANAGER;

        @Bean
        public AuthenticationManager authenticationManager() {
            return Sec2515Tests.CanLoadWithChildConfig.AUTHENTICATION_MANAGER;
        }
    }

    // SEC-2515
    @Test
    public void loadConfigWhenAuthenticationManagerConfiguredAndRegisterBeanThenContextLoads() throws Exception {
        this.spring.register(Sec2515Tests.SecurityConfig.class).autowire();
    }

    @EnableWebSecurity
    static class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication();
        }
    }
}

