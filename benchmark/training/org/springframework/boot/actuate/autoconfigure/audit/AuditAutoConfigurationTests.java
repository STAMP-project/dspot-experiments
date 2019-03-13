/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.autoconfigure.audit;


import org.junit.Test;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.boot.actuate.audit.listener.AbstractAuditListener;
import org.springframework.boot.actuate.security.AbstractAuthenticationAuditListener;
import org.springframework.boot.actuate.security.AbstractAuthorizationAuditListener;
import org.springframework.boot.actuate.security.AuthenticationAuditListener;
import org.springframework.boot.actuate.security.AuthorizationAuditListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;


/**
 * Tests for {@link AuditAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Vedran Pavic
 */
public class AuditAutoConfigurationTests {
    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void defaultConfiguration() {
        registerAndRefresh(AuditAutoConfiguration.class);
        assertThat(this.context.getBean(AuditEventRepository.class)).isNotNull();
        assertThat(this.context.getBean(AuthenticationAuditListener.class)).isNotNull();
        assertThat(this.context.getBean(AuthorizationAuditListener.class)).isNotNull();
    }

    @Test
    public void ownAuditEventRepository() {
        registerAndRefresh(AuditAutoConfigurationTests.CustomAuditEventRepositoryConfiguration.class, AuditAutoConfiguration.class);
        assertThat(this.context.getBean(AuditEventRepository.class)).isInstanceOf(AuditAutoConfigurationTests.TestAuditEventRepository.class);
    }

    @Test
    public void ownAuthenticationAuditListener() {
        registerAndRefresh(AuditAutoConfigurationTests.CustomAuthenticationAuditListenerConfiguration.class, AuditAutoConfiguration.class);
        assertThat(this.context.getBean(AbstractAuthenticationAuditListener.class)).isInstanceOf(AuditAutoConfigurationTests.TestAuthenticationAuditListener.class);
    }

    @Test
    public void ownAuthorizationAuditListener() {
        registerAndRefresh(AuditAutoConfigurationTests.CustomAuthorizationAuditListenerConfiguration.class, AuditAutoConfiguration.class);
        assertThat(this.context.getBean(AbstractAuthorizationAuditListener.class)).isInstanceOf(AuditAutoConfigurationTests.TestAuthorizationAuditListener.class);
    }

    @Test
    public void ownAuditListener() {
        registerAndRefresh(AuditAutoConfigurationTests.CustomAuditListenerConfiguration.class, AuditAutoConfiguration.class);
        assertThat(this.context.getBean(AbstractAuditListener.class)).isInstanceOf(AuditAutoConfigurationTests.TestAuditListener.class);
    }

    @Configuration
    public static class CustomAuditEventRepositoryConfiguration {
        @Bean
        public AuditAutoConfigurationTests.TestAuditEventRepository testAuditEventRepository() {
            return new AuditAutoConfigurationTests.TestAuditEventRepository();
        }
    }

    public static class TestAuditEventRepository extends InMemoryAuditEventRepository {}

    @Configuration
    protected static class CustomAuthenticationAuditListenerConfiguration {
        @Bean
        public AuditAutoConfigurationTests.TestAuthenticationAuditListener authenticationAuditListener() {
            return new AuditAutoConfigurationTests.TestAuthenticationAuditListener();
        }
    }

    protected static class TestAuthenticationAuditListener extends AbstractAuthenticationAuditListener {
        @Override
        public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        }

        @Override
        public void onApplicationEvent(AbstractAuthenticationEvent event) {
        }
    }

    @Configuration
    protected static class CustomAuthorizationAuditListenerConfiguration {
        @Bean
        public AuditAutoConfigurationTests.TestAuthorizationAuditListener authorizationAuditListener() {
            return new AuditAutoConfigurationTests.TestAuthorizationAuditListener();
        }
    }

    protected static class TestAuthorizationAuditListener extends AbstractAuthorizationAuditListener {
        @Override
        public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        }

        @Override
        public void onApplicationEvent(AbstractAuthorizationEvent event) {
        }
    }

    @Configuration
    protected static class CustomAuditListenerConfiguration {
        @Bean
        public AuditAutoConfigurationTests.TestAuditListener testAuditListener() {
            return new AuditAutoConfigurationTests.TestAuditListener();
        }
    }

    protected static class TestAuditListener extends AbstractAuditListener {
        @Override
        protected void onAuditEvent(AuditEvent event) {
        }
    }
}

