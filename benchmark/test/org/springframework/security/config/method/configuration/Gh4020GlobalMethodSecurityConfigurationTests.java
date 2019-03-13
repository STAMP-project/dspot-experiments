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
package org.springframework.security.config.method.configuration;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class Gh4020GlobalMethodSecurityConfigurationTests {
    @Autowired
    Gh4020GlobalMethodSecurityConfigurationTests.DenyAllService denyAll;

    // gh-4020
    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void denyAll() {
        this.denyAll.denyAll();
    }

    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    static class SecurityConfig {
        @Bean
        PermissionEvaluator permissionEvaluator() {
            return Mockito.mock(PermissionEvaluator.class);
        }

        @Bean
        RoleHierarchy RoleHierarchy() {
            return Mockito.mock(RoleHierarchy.class);
        }

        @Bean
        AuthenticationTrustResolver trustResolver() {
            return Mockito.mock(AuthenticationTrustResolver.class);
        }

        @Autowired
        Gh4020GlobalMethodSecurityConfigurationTests.DenyAllService denyAll;
    }

    @Configuration
    static class ServiceConfig {
        @Bean
        Gh4020GlobalMethodSecurityConfigurationTests.DenyAllService denyAllService() {
            return new Gh4020GlobalMethodSecurityConfigurationTests.DenyAllService();
        }
    }

    @PreAuthorize("denyAll")
    static class DenyAllService {
        void denyAll() {
        }
    }
}

