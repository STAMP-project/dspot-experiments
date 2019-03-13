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
package org.springframework.security.config.annotation.sec2758;


import javax.annotation.security.RolesAllowed;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.PriorityOrdered;
import org.springframework.security.access.annotation.Jsr250MethodSecurityMetadataSource;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 *
 * @author Josh Cummings
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SecurityTestExecutionListeners
public class Sec2758Tests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    MockMvc mvc;

    @Autowired(required = false)
    Sec2758Tests.Service service;

    @WithMockUser(authorities = "CUSTOM")
    @Test
    public void requestWhenNullifyingRolePrefixThenPassivityRestored() throws Exception {
        this.spring.register(Sec2758Tests.SecurityConfig.class).autowire();
        this.mvc.perform(get("/")).andExpect(status().isOk());
    }

    @WithMockUser(authorities = "CUSTOM")
    @Test
    public void methodSecurityWhenNullifyingRolePrefixThenPassivityRestored() {
        this.spring.register(Sec2758Tests.SecurityConfig.class).autowire();
        assertThatCode(() -> service.doJsr250()).doesNotThrowAnyException();
        assertThatCode(() -> service.doPreAuthorize()).doesNotThrowAnyException();
    }

    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
    static class SecurityConfig extends WebSecurityConfigurerAdapter {
        @RestController
        static class RootController {
            @GetMapping("/")
            public String ok() {
                return "ok";
            }
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().access("hasAnyRole('CUSTOM')");
        }

        @Bean
        public Sec2758Tests.Service service() {
            return new Sec2758Tests.Service();
        }

        @Bean
        static Sec2758Tests.DefaultRolesPrefixPostProcessor defaultRolesPrefixPostProcessor() {
            return new Sec2758Tests.DefaultRolesPrefixPostProcessor();
        }
    }

    static class Service {
        @PreAuthorize("hasRole('CUSTOM')")
        public void doPreAuthorize() {
        }

        @RolesAllowed("CUSTOM")
        public void doJsr250() {
        }
    }

    static class DefaultRolesPrefixPostProcessor implements BeanPostProcessor , PriorityOrdered {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof Jsr250MethodSecurityMetadataSource) {
                setDefaultRolePrefix(null);
            }
            if (bean instanceof DefaultMethodSecurityExpressionHandler) {
                setDefaultRolePrefix(null);
            }
            if (bean instanceof DefaultWebSecurityExpressionHandler) {
                setDefaultRolePrefix(null);
            }
            return bean;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public int getOrder() {
            return PriorityOrdered.HIGHEST_PRECEDENCE;
        }
    }
}

