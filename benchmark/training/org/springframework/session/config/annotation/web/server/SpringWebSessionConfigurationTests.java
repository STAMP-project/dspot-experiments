/**
 * Copyright 2014-2018 the original author or authors.
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
package org.springframework.session.config.annotation.web.server;


import WebHttpHandlerBuilder.WEB_SESSION_MANAGER_BEAN_NAME;
import java.util.HashMap;
import org.junit.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.session.ReactiveMapSessionRepository;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.HeaderWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;
import org.springframework.web.server.session.WebSessionManager;


/**
 * Verify various configurations through {@link EnableSpringWebSession}.
 *
 * @author Greg Turnquist
 */
public class SpringWebSessionConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void enableSpringWebSessionConfiguresThings() {
        this.context = new AnnotationConfigApplicationContext();
        this.context.register(SpringWebSessionConfigurationTests.GoodConfig.class);
        this.context.refresh();
        WebSessionManager webSessionManagerFoundByType = this.context.getBean(WebSessionManager.class);
        Object webSessionManagerFoundByName = this.context.getBean(WEB_SESSION_MANAGER_BEAN_NAME);
        assertThat(webSessionManagerFoundByType).isNotNull();
        assertThat(webSessionManagerFoundByName).isNotNull();
        assertThat(webSessionManagerFoundByType).isEqualTo(webSessionManagerFoundByName);
        assertThat(this.context.getBean(ReactiveSessionRepository.class)).isNotNull();
    }

    @Test
    public void missingReactiveSessionRepositoryBreaksAppContext() {
        this.context = new AnnotationConfigApplicationContext();
        this.context.register(SpringWebSessionConfigurationTests.BadConfig.class);
        assertThatExceptionOfType(UnsatisfiedDependencyException.class).isThrownBy(this.context::refresh).withMessageContaining("Error creating bean with name 'webSessionManager'").withMessageContaining(("No qualifying bean of type '" + (ReactiveSessionRepository.class.getCanonicalName())));
    }

    @Test
    public void defaultSessionIdResolverShouldBeCookieBased() {
        this.context = new AnnotationConfigApplicationContext();
        this.context.register(SpringWebSessionConfigurationTests.GoodConfig.class);
        this.context.refresh();
        DefaultWebSessionManager manager = this.context.getBean(DefaultWebSessionManager.class);
        assertThat(manager.getSessionIdResolver().getClass()).isAssignableFrom(CookieWebSessionIdResolver.class);
    }

    @Test
    public void providedSessionIdResolverShouldBePickedUpAutomatically() {
        this.context = new AnnotationConfigApplicationContext();
        this.context.register(SpringWebSessionConfigurationTests.OverrideSessionIdResolver.class);
        this.context.refresh();
        DefaultWebSessionManager manager = this.context.getBean(DefaultWebSessionManager.class);
        assertThat(manager.getSessionIdResolver().getClass()).isAssignableFrom(HeaderWebSessionIdResolver.class);
    }

    /**
     * A configuration with all the right parts.
     */
    @EnableSpringWebSession
    static class GoodConfig {
        /**
         * Use Reactor-friendly, {@link java.util.Map}-backed {@link ReactiveSessionRepository} for test purposes.
         */
        @Bean
        ReactiveSessionRepository<?> reactiveSessionRepository() {
            return new ReactiveMapSessionRepository(new HashMap());
        }
    }

    /**
     * A configuration where no {@link ReactiveSessionRepository} is defined. It's BAD!
     */
    @EnableSpringWebSession
    static class BadConfig {}

    @EnableSpringWebSession
    static class OverrideSessionIdResolver {
        @Bean
        ReactiveSessionRepository<?> reactiveSessionRepository() {
            return new ReactiveMapSessionRepository(new HashMap());
        }

        @Bean
        WebSessionIdResolver alternateWebSessionIdResolver() {
            return new HeaderWebSessionIdResolver();
        }
    }
}

