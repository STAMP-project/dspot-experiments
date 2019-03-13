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
package org.springframework.boot.autoconfigure.session;


import java.time.Duration;
import java.util.Collections;
import javax.servlet.DispatcherType;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link SessionAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Edd? Mel?ndez
 * @author Stephane Nicoll
 * @author Vedran Pavic
 */
public class SessionAutoConfigurationTests extends AbstractSessionAutoConfigurationTests {
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(SessionAutoConfiguration.class));

    @Test
    public void contextFailsIfMultipleStoresAreAvailable() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context).getFailure().hasCauseInstanceOf(.class);
            assertThat(context).getFailure().hasMessageContaining("Multiple session repository candidates are available");
        });
    }

    @Test
    public void contextFailsIfStoreTypeNotAvailable() {
        this.contextRunner.withPropertyValues("spring.session.store-type=jdbc").run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context).getFailure().hasCauseInstanceOf(.class);
            assertThat(context).getFailure().hasMessageContaining("No session repository could be auto-configured");
            assertThat(context).getFailure().hasMessageContaining("session store type is 'jdbc'");
        });
    }

    @Test
    public void autoConfigurationDisabledIfStoreTypeSetToNone() {
        this.contextRunner.withPropertyValues("spring.session.store-type=none").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void backOffIfSessionRepositoryIsPresent() {
        this.contextRunner.withUserConfiguration(SessionAutoConfigurationTests.SessionRepositoryConfiguration.class).withPropertyValues("spring.session.store-type=redis").run(( context) -> {
            MapSessionRepository repository = validateSessionRepository(context, .class);
            assertThat(context).getBean("mySessionRepository").isSameAs(repository);
        });
    }

    @Test
    public void autoConfigWhenSpringSessionTimeoutIsSetShouldUseThat() {
        this.contextRunner.withUserConfiguration(SessionAutoConfigurationTests.ServerPropertiesConfiguration.class, SessionAutoConfigurationTests.SessionRepositoryConfiguration.class).withPropertyValues("server.servlet.session.timeout=1", "spring.session.timeout=3").run(( context) -> assertThat(context.getBean(.class).getTimeout()).isEqualTo(Duration.ofSeconds(3)));
    }

    @Test
    public void autoConfigWhenSpringSessionTimeoutIsNotSetShouldUseServerSessionTimeout() {
        this.contextRunner.withUserConfiguration(SessionAutoConfigurationTests.ServerPropertiesConfiguration.class, SessionAutoConfigurationTests.SessionRepositoryConfiguration.class).withPropertyValues("server.servlet.session.timeout=3").run(( context) -> assertThat(context.getBean(.class).getTimeout()).isEqualTo(Duration.ofSeconds(3)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void filterIsRegisteredWithAsyncErrorAndRequestDispatcherTypes() {
        this.contextRunner.withUserConfiguration(SessionAutoConfigurationTests.SessionRepositoryConfiguration.class).run(( context) -> {
            FilterRegistrationBean<?> registration = context.getBean(.class);
            assertThat(registration.getFilter()).isSameAs(context.getBean(.class));
            assertThat(((EnumSet<DispatcherType>) (ReflectionTestUtils.getField(registration, "dispatcherTypes")))).containsOnly(DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.REQUEST);
        });
    }

    @Test
    public void filterOrderCanBeCustomizedWithCustomStore() {
        this.contextRunner.withUserConfiguration(SessionAutoConfigurationTests.SessionRepositoryConfiguration.class).withPropertyValues("spring.session.servlet.filter-order=123").run(( context) -> {
            FilterRegistrationBean<?> registration = context.getBean(.class);
            assertThat(registration.getOrder()).isEqualTo(123);
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void filterDispatcherTypesCanBeCustomized() {
        this.contextRunner.withUserConfiguration(SessionAutoConfigurationTests.SessionRepositoryConfiguration.class).withPropertyValues("spring.session.servlet.filter-dispatcher-types=error, request").run(( context) -> {
            FilterRegistrationBean<?> registration = context.getBean(.class);
            assertThat(((EnumSet<DispatcherType>) (ReflectionTestUtils.getField(registration, "dispatcherTypes")))).containsOnly(DispatcherType.ERROR, DispatcherType.REQUEST);
        });
    }

    @Test
    public void sessionCookieConfigurationIsAppliedToAutoConfiguredCookieSerializer() {
        this.contextRunner.withUserConfiguration(SessionAutoConfigurationTests.SessionRepositoryConfiguration.class).withPropertyValues("server.servlet.session.cookie.name=sid", "server.servlet.session.cookie.domain=spring", "server.servlet.session.cookie.path=/test", "server.servlet.session.cookie.httpOnly=false", "server.servlet.session.cookie.secure=false", "server.servlet.session.cookie.maxAge=10s").run(( context) -> {
            DefaultCookieSerializer cookieSerializer = context.getBean(.class);
            assertThat(cookieSerializer).hasFieldOrPropertyWithValue("cookieName", "sid");
            assertThat(cookieSerializer).hasFieldOrPropertyWithValue("domainName", "spring");
            assertThat(cookieSerializer).hasFieldOrPropertyWithValue("cookiePath", "/test");
            assertThat(cookieSerializer).hasFieldOrPropertyWithValue("useHttpOnlyCookie", false);
            assertThat(cookieSerializer).hasFieldOrPropertyWithValue("useSecureCookie", false);
            assertThat(cookieSerializer).hasFieldOrPropertyWithValue("cookieMaxAge", 10);
        });
    }

    @Test
    public void autoConfiguredCookieSerializerIsUsedBySessionRepositoryFilter() {
        this.contextRunner.withUserConfiguration(SessionAutoConfigurationTests.SessionRepositoryConfiguration.class).withPropertyValues("server.port=0").run(( context) -> {
            SessionRepositoryFilter<?> filter = context.getBean(.class);
            CookieHttpSessionIdResolver sessionIdResolver = ((CookieHttpSessionIdResolver) (ReflectionTestUtils.getField(filter, "httpSessionIdResolver")));
            DefaultCookieSerializer cookieSerializer = ((DefaultCookieSerializer) (ReflectionTestUtils.getField(sessionIdResolver, "cookieSerializer")));
            assertThat(cookieSerializer).isSameAs(context.getBean(.class));
        });
    }

    @Test
    public void autoConfiguredCookieSerializerBacksOffWhenUserConfiguresACookieSerializer() {
        this.contextRunner.withUserConfiguration(SessionAutoConfigurationTests.UserProvidedCookieSerializerConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasBean("myCookieSerializer");
        });
    }

    @Test
    public void cookiesSerializerIsAutoConfiguredWhenUserConfiguresCookieHttpSessionIdResolver() {
        this.contextRunner.withUserConfiguration(SessionAutoConfigurationTests.UserProvidedCookieHttpSessionStrategyConfiguration.class).run(( context) -> assertThat(context.getBeansOfType(.class)).isNotEmpty());
    }

    @Test
    public void autoConfiguredCookieSerializerBacksOffWhenUserConfiguresHeaderHttpSessionIdResolver() {
        this.contextRunner.withUserConfiguration(SessionAutoConfigurationTests.UserProvidedHeaderHttpSessionStrategyConfiguration.class).run(( context) -> assertThat(context.getBeansOfType(.class)).isEmpty());
    }

    @Test
    public void autoConfiguredCookieSerializerBacksOffWhenUserConfiguresCustomHttpSessionIdResolver() {
        this.contextRunner.withUserConfiguration(SessionAutoConfigurationTests.UserProvidedCustomHttpSessionStrategyConfiguration.class).run(( context) -> assertThat(context.getBeansOfType(.class)).isEmpty());
    }

    @Configuration
    @EnableSpringHttpSession
    static class SessionRepositoryConfiguration {
        @Bean
        public MapSessionRepository mySessionRepository() {
            return new MapSessionRepository(Collections.emptyMap());
        }
    }

    @EnableConfigurationProperties(ServerProperties.class)
    static class ServerPropertiesConfiguration {}

    @Configuration
    @EnableSpringHttpSession
    static class UserProvidedCookieSerializerConfiguration extends SessionAutoConfigurationTests.SessionRepositoryConfiguration {
        @Bean
        public DefaultCookieSerializer myCookieSerializer() {
            return new DefaultCookieSerializer();
        }
    }

    @Configuration
    @EnableSpringHttpSession
    static class UserProvidedCookieHttpSessionStrategyConfiguration extends SessionAutoConfigurationTests.SessionRepositoryConfiguration {
        @Bean
        public CookieHttpSessionIdResolver httpSessionStrategy() {
            return new CookieHttpSessionIdResolver();
        }
    }

    @Configuration
    @EnableSpringHttpSession
    static class UserProvidedHeaderHttpSessionStrategyConfiguration extends SessionAutoConfigurationTests.SessionRepositoryConfiguration {
        @Bean
        public HeaderHttpSessionIdResolver httpSessionStrategy() {
            return HeaderHttpSessionIdResolver.xAuthToken();
        }
    }

    @Configuration
    @EnableSpringHttpSession
    static class UserProvidedCustomHttpSessionStrategyConfiguration extends SessionAutoConfigurationTests.SessionRepositoryConfiguration {
        @Bean
        public HttpSessionIdResolver httpSessionStrategy() {
            return Mockito.mock(HttpSessionIdResolver.class);
        }
    }
}

