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
package org.springframework.security.config.annotation.method.configuration;


import BeanDefinition.ROLE_INFRASTRUCTURE;
import Ordered.LOWEST_PRECEDENCE;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.intercept.AfterInvocationManager;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.intercept.RunAsManagerImpl;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityMetadataSourceAdvisor;
import org.springframework.security.access.intercept.aspectj.AspectJMethodSecurityInterceptor;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Rob Winch
 * @author Josh Cummings
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SecurityTestExecutionListeners
public class NamespaceGlobalMethodSecurityTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired(required = false)
    private MethodSecurityService service;

    // --- access-decision-manager-ref ---
    @Test
    @WithMockUser
    public void methodSecurityWhenCustomAccessDecisionManagerThenAuthorizes() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.CustomAccessDecisionManagerConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThatThrownBy(() -> this.service.preAuthorize()).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> this.service.secured()).isInstanceOf(AccessDeniedException.class);
    }

    @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
    public static class CustomAccessDecisionManagerConfig extends GlobalMethodSecurityConfiguration {
        @Override
        protected AccessDecisionManager accessDecisionManager() {
            return new NamespaceGlobalMethodSecurityTests.CustomAccessDecisionManagerConfig.DenyAllAccessDecisionManager();
        }

        public static class DenyAllAccessDecisionManager implements AccessDecisionManager {
            public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) {
                throw new AccessDeniedException("Always Denied");
            }

            public boolean supports(ConfigAttribute attribute) {
                return true;
            }

            public boolean supports(Class<?> clazz) {
                return true;
            }
        }
    }

    // --- after-invocation-provider
    @Test
    @WithMockUser
    public void methodSecurityWhenCustomAfterInvocationManagerThenAuthorizes() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.CustomAfterInvocationManagerConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThatThrownBy(() -> this.service.preAuthorizePermitAll()).isInstanceOf(AccessDeniedException.class);
    }

    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public static class CustomAfterInvocationManagerConfig extends GlobalMethodSecurityConfiguration {
        @Override
        protected AfterInvocationManager afterInvocationManager() {
            return new NamespaceGlobalMethodSecurityTests.CustomAfterInvocationManagerConfig.AfterInvocationManagerStub();
        }

        public static class AfterInvocationManagerStub implements AfterInvocationManager {
            public Object decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes, Object returnedObject) throws AccessDeniedException {
                throw new AccessDeniedException("custom AfterInvocationManager");
            }

            public boolean supports(ConfigAttribute attribute) {
                return true;
            }

            public boolean supports(Class<?> clazz) {
                return true;
            }
        }
    }

    // --- authentication-manager-ref ---
    @Test
    @WithMockUser
    public void methodSecurityWhenCustomAuthenticationManagerThenAuthorizes() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.CustomAuthenticationConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThatThrownBy(() -> this.service.preAuthorize()).isInstanceOf(UnsupportedOperationException.class);
    }

    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public static class CustomAuthenticationConfig extends GlobalMethodSecurityConfiguration {
        @Override
        public MethodInterceptor methodSecurityInterceptor() throws Exception {
            MethodInterceptor interceptor = super.methodSecurityInterceptor();
            setAlwaysReauthenticate(true);
            return interceptor;
        }

        @Override
        protected AuthenticationManager authenticationManager() {
            return ( authentication) -> {
                throw new UnsupportedOperationException();
            };
        }
    }

    // --- jsr250-annotations ---
    @Test
    @WithMockUser
    public void methodSecurityWhenJsr250EnabledThenAuthorizes() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.Jsr250Config.class, MethodSecurityServiceConfig.class).autowire();
        assertThatCode(() -> this.service.preAuthorize()).doesNotThrowAnyException();
        assertThatCode(() -> this.service.secured()).doesNotThrowAnyException();
        assertThatThrownBy(() -> this.service.jsr250()).isInstanceOf(AccessDeniedException.class);
        assertThatCode(() -> this.service.jsr250PermitAll()).doesNotThrowAnyException();
    }

    @EnableGlobalMethodSecurity(jsr250Enabled = true)
    @Configuration
    public static class Jsr250Config {}

    // --- metadata-source-ref ---
    @Test
    @WithMockUser
    public void methodSecurityWhenCustomMethodSecurityMetadataSourceThenAuthorizes() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.CustomMethodSecurityMetadataSourceConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThatThrownBy(() -> this.service.preAuthorize()).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> this.service.secured()).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> this.service.jsr250()).isInstanceOf(AccessDeniedException.class);
    }

    @EnableGlobalMethodSecurity
    public static class CustomMethodSecurityMetadataSourceConfig extends GlobalMethodSecurityConfiguration {
        @Override
        protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
            return new AbstractMethodSecurityMetadataSource() {
                public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
                    // require ROLE_NOBODY for any method on MethodSecurityService interface
                    return MethodSecurityService.class.isAssignableFrom(targetClass) ? Arrays.asList(new SecurityConfig("ROLE_NOBODY")) : Collections.emptyList();
                }

                public Collection<ConfigAttribute> getAllConfigAttributes() {
                    return null;
                }
            };
        }
    }

    // --- mode ---
    @Test
    @WithMockUser
    public void contextRefreshWhenUsingAspectJThenAutowire() throws Exception {
        this.spring.register(NamespaceGlobalMethodSecurityTests.AspectJModeConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThat(this.spring.getContext().getBean(Class.forName("org.springframework.security.access.intercept.aspectj.aspect.AnnotationSecurityAspect"))).isNotNull();
        assertThat(this.spring.getContext().getBean(AspectJMethodSecurityInterceptor.class)).isNotNull();
        // TODO diagnose why aspectj isn't weaving method security advice around MethodSecurityServiceImpl
    }

    @EnableGlobalMethodSecurity(mode = AdviceMode.ASPECTJ, securedEnabled = true)
    public static class AspectJModeConfig {}

    @Test
    public void contextRefreshWhenUsingAspectJAndCustomGlobalMethodSecurityConfigurationThenAutowire() throws Exception {
        this.spring.register(NamespaceGlobalMethodSecurityTests.AspectJModeExtendsGMSCConfig.class).autowire();
        assertThat(this.spring.getContext().getBean(Class.forName("org.springframework.security.access.intercept.aspectj.aspect.AnnotationSecurityAspect"))).isNotNull();
        assertThat(this.spring.getContext().getBean(AspectJMethodSecurityInterceptor.class)).isNotNull();
    }

    @EnableGlobalMethodSecurity(mode = AdviceMode.ASPECTJ, securedEnabled = true)
    public static class AspectJModeExtendsGMSCConfig extends GlobalMethodSecurityConfiguration {}

    // --- order ---
    private static class AdvisorOrderConfig implements ImportBeanDefinitionRegistrar {
        private static class ExceptingInterceptor implements MethodInterceptor {
            @Override
            public Object invoke(MethodInvocation invocation) {
                throw new UnsupportedOperationException("Deny All");
            }
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            BeanDefinitionBuilder advice = BeanDefinitionBuilder.rootBeanDefinition(NamespaceGlobalMethodSecurityTests.AdvisorOrderConfig.ExceptingInterceptor.class);
            registry.registerBeanDefinition("exceptingInterceptor", advice.getBeanDefinition());
            BeanDefinitionBuilder advisor = BeanDefinitionBuilder.rootBeanDefinition(MethodSecurityMetadataSourceAdvisor.class);
            advisor.setRole(ROLE_INFRASTRUCTURE);
            advisor.addConstructorArgValue("exceptingInterceptor");
            advisor.addConstructorArgReference("methodSecurityMetadataSource");
            advisor.addConstructorArgValue("methodSecurityMetadataSource");
            advisor.addPropertyValue("order", 0);
            registry.registerBeanDefinition("exceptingAdvisor", advisor.getBeanDefinition());
        }
    }

    @Test
    @WithMockUser
    public void methodSecurityWhenOrderSpecifiedThenConfigured() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.CustomOrderConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThat(this.spring.getContext().getBean("metaDataSourceAdvisor", MethodSecurityMetadataSourceAdvisor.class).getOrder()).isEqualTo((-135));
        assertThatThrownBy(() -> this.service.jsr250()).isInstanceOf(AccessDeniedException.class);
    }

    @EnableGlobalMethodSecurity(order = -135, jsr250Enabled = true)
    @Import(NamespaceGlobalMethodSecurityTests.AdvisorOrderConfig.class)
    public static class CustomOrderConfig {}

    @Test
    @WithMockUser
    public void methodSecurityWhenOrderUnspecifiedThenConfiguredToLowestPrecedence() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.DefaultOrderConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThat(this.spring.getContext().getBean("metaDataSourceAdvisor", MethodSecurityMetadataSourceAdvisor.class).getOrder()).isEqualTo(LOWEST_PRECEDENCE);
        assertThatThrownBy(() -> this.service.jsr250()).isInstanceOf(UnsupportedOperationException.class);
    }

    @EnableGlobalMethodSecurity(jsr250Enabled = true)
    @Import(NamespaceGlobalMethodSecurityTests.AdvisorOrderConfig.class)
    public static class DefaultOrderConfig {}

    @Test
    @WithMockUser
    public void methodSecurityWhenOrderUnspecifiedAndCustomGlobalMethodSecurityConfigurationThenConfiguredToLowestPrecedence() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.DefaultOrderExtendsMethodSecurityConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThat(this.spring.getContext().getBean("metaDataSourceAdvisor", MethodSecurityMetadataSourceAdvisor.class).getOrder()).isEqualTo(LOWEST_PRECEDENCE);
        assertThatThrownBy(() -> this.service.jsr250()).isInstanceOf(UnsupportedOperationException.class);
    }

    @EnableGlobalMethodSecurity(jsr250Enabled = true)
    @Import(NamespaceGlobalMethodSecurityTests.AdvisorOrderConfig.class)
    public static class DefaultOrderExtendsMethodSecurityConfig extends GlobalMethodSecurityConfiguration {}

    // --- pre-post-annotations ---
    @Test
    @WithMockUser
    public void methodSecurityWhenPrePostEnabledThenPreAuthorizes() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.PreAuthorizeConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThatCode(() -> this.service.secured()).doesNotThrowAnyException();
        assertThatCode(() -> this.service.jsr250()).doesNotThrowAnyException();
        assertThatThrownBy(() -> this.service.preAuthorize()).isInstanceOf(AccessDeniedException.class);
    }

    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public static class PreAuthorizeConfig {}

    @Test
    @WithMockUser
    public void methodSecurityWhenPrePostEnabledAndCustomGlobalMethodSecurityConfigurationThenPreAuthorizes() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.PreAuthorizeExtendsGMSCConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThatCode(() -> this.service.secured()).doesNotThrowAnyException();
        assertThatCode(() -> this.service.jsr250()).doesNotThrowAnyException();
        assertThatThrownBy(() -> this.service.preAuthorize()).isInstanceOf(AccessDeniedException.class);
    }

    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public static class PreAuthorizeExtendsGMSCConfig extends GlobalMethodSecurityConfiguration {}

    // --- proxy-target-class ---
    @Test
    @WithMockUser
    public void methodSecurityWhenProxyTargetClassThenDoesNotWireToInterface() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.ProxyTargetClassConfig.class, MethodSecurityServiceConfig.class).autowire();
        // make sure service was actually proxied
        assertThat(this.service.getClass().getInterfaces()).doesNotContain(MethodSecurityService.class);
        assertThatThrownBy(() -> this.service.preAuthorize()).isInstanceOf(AccessDeniedException.class);
    }

    @EnableGlobalMethodSecurity(proxyTargetClass = true, prePostEnabled = true)
    public static class ProxyTargetClassConfig {}

    @Test
    @WithMockUser
    public void methodSecurityWhenDefaultProxyThenWiresToInterface() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.DefaultProxyConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThat(this.service.getClass().getInterfaces()).contains(MethodSecurityService.class);
        assertThatThrownBy(() -> this.service.preAuthorize()).isInstanceOf(AccessDeniedException.class);
    }

    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public static class DefaultProxyConfig {}

    // --- run-as-manager-ref ---
    @Test
    @WithMockUser
    public void methodSecurityWhenCustomRunAsManagerThenRunAsWrapsAuthentication() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.CustomRunAsManagerConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThat(service.runAs().getAuthorities()).anyMatch(( authority) -> "ROLE_RUN_AS_SUPER".equals(authority.getAuthority()));
    }

    @EnableGlobalMethodSecurity(securedEnabled = true)
    public static class CustomRunAsManagerConfig extends GlobalMethodSecurityConfiguration {
        @Override
        protected RunAsManager runAsManager() {
            RunAsManagerImpl runAsManager = new RunAsManagerImpl();
            runAsManager.setKey("some key");
            return runAsManager;
        }
    }

    // --- secured-annotation ---
    @Test
    @WithMockUser
    public void methodSecurityWhenSecuredEnabledThenSecures() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.SecuredConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThatThrownBy(() -> this.service.secured()).isInstanceOf(AccessDeniedException.class);
        assertThatCode(() -> this.service.securedUser()).doesNotThrowAnyException();
        assertThatCode(() -> this.service.preAuthorize()).doesNotThrowAnyException();
        assertThatCode(() -> this.service.jsr250()).doesNotThrowAnyException();
    }

    @EnableGlobalMethodSecurity(securedEnabled = true)
    public static class SecuredConfig {}

    // --- unsorted ---
    @Test
    @WithMockUser
    public void methodSecurityWhenMissingEnableAnnotationThenShowsHelpfulError() {
        assertThatThrownBy(() -> this.spring.register(.class).autowire()).hasStackTraceContaining(((EnableGlobalMethodSecurity.class.getName()) + " is required"));
    }

    @Configuration
    public static class ExtendsNoEnableAnntotationConfig extends GlobalMethodSecurityConfiguration {}

    @Test
    @WithMockUser
    public void methodSecurityWhenImportingGlobalMethodSecurityConfigurationSubclassThenAuthorizes() {
        this.spring.register(NamespaceGlobalMethodSecurityTests.ImportSubclassGMSCConfig.class, MethodSecurityServiceConfig.class).autowire();
        assertThatCode(() -> this.service.secured()).doesNotThrowAnyException();
        assertThatCode(() -> this.service.jsr250()).doesNotThrowAnyException();
        assertThatThrownBy(() -> this.service.preAuthorize()).isInstanceOf(AccessDeniedException.class);
    }

    @Configuration
    @Import(NamespaceGlobalMethodSecurityTests.PreAuthorizeExtendsGMSCConfig.class)
    public static class ImportSubclassGMSCConfig {}
}

