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
package org.springframework.boot.actuate.autoconfigure.health;


import java.time.Duration;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.HealthWebEndpointResponseMapper;
import org.springframework.boot.actuate.health.ReactiveHealthEndpointWebExtension;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.ReactiveHealthIndicatorRegistry;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;


/**
 * Tests for {@link HealthEndpointAutoConfiguration} in a reactive environment.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Phillip Webb
 */
public class ReactiveHealthEndpointWebExtensionTests {
    private ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner().withUserConfiguration(HealthIndicatorAutoConfiguration.class, HealthEndpointAutoConfiguration.class);

    @Test
    public void runShouldCreateExtensionBeans() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void runWhenHealthEndpointIsDisabledShouldNotCreateExtensionBeans() {
        this.contextRunner.withPropertyValues("management.endpoint.health.enabled:false").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void runWithCustomHealthMappingShouldMapStatusCode() {
        this.contextRunner.withPropertyValues("management.health.status.http-mapping.CUSTOM=500").run(( context) -> {
            Object extension = context.getBean(.class);
            HealthWebEndpointResponseMapper responseMapper = ((HealthWebEndpointResponseMapper) (ReflectionTestUtils.getField(extension, "responseMapper")));
            Class<SecurityContext> securityContext = .class;
            assertThat(responseMapper.map(Health.down().build(), mock(securityContext)).getStatus()).isEqualTo(503);
            assertThat(responseMapper.map(Health.status("OUT_OF_SERVICE").build(), mock(securityContext)).getStatus()).isEqualTo(503);
            assertThat(responseMapper.map(Health.status("CUSTOM").build(), mock(securityContext)).getStatus()).isEqualTo(500);
        });
    }

    @Test
    public void regularAndReactiveHealthIndicatorsMatch() {
        this.contextRunner.withPropertyValues("management.endpoint.health.show-details=always").withUserConfiguration(ReactiveHealthEndpointWebExtensionTests.HealthIndicatorsConfiguration.class).run(( context) -> {
            HealthEndpoint endpoint = context.getBean(.class);
            ReactiveHealthEndpointWebExtension extension = context.getBean(.class);
            Health endpointHealth = endpoint.health();
            SecurityContext securityContext = mock(.class);
            given(securityContext.getPrincipal()).willReturn(mock(.class));
            Health extensionHealth = extension.health(securityContext).block(Duration.ofSeconds(30)).getBody();
            assertThat(endpointHealth.getDetails()).containsOnlyKeys("application", "first", "second");
            assertThat(extensionHealth.getDetails()).containsOnlyKeys("application", "first", "second");
        });
    }

    @Test
    public void unauthenticatedUsersAreNotShownDetailsByDefault() {
        this.contextRunner.run(( context) -> {
            ReactiveHealthEndpointWebExtension extension = context.getBean(.class);
            assertThat(extension.health(mock(.class)).block(Duration.ofSeconds(30)).getBody().getDetails()).isEmpty();
        });
    }

    @Test
    public void authenticatedUsersAreNotShownDetailsByDefault() {
        this.contextRunner.run(( context) -> {
            ReactiveHealthEndpointWebExtension extension = context.getBean(.class);
            SecurityContext securityContext = mock(.class);
            given(securityContext.getPrincipal()).willReturn(mock(.class));
            assertThat(extension.health(securityContext).block(Duration.ofSeconds(30)).getBody().getDetails()).isEmpty();
        });
    }

    @Test
    public void authenticatedUsersWhenAuthorizedCanBeShownDetails() {
        this.contextRunner.withPropertyValues("management.endpoint.health.show-details=when-authorized").run(( context) -> {
            ReactiveHealthEndpointWebExtension extension = context.getBean(.class);
            SecurityContext securityContext = mock(.class);
            given(securityContext.getPrincipal()).willReturn(mock(.class));
            assertThat(extension.health(securityContext).block(Duration.ofSeconds(30)).getBody().getDetails()).isNotEmpty();
        });
    }

    @Test
    public void unauthenticatedUsersCanBeShownDetails() {
        this.contextRunner.withPropertyValues("management.endpoint.health.show-details=always").run(( context) -> {
            ReactiveHealthEndpointWebExtension extension = context.getBean(.class);
            assertThat(extension.health(null).block(Duration.ofSeconds(30)).getBody().getDetails()).isNotEmpty();
        });
    }

    @Test
    public void detailsCanBeHiddenFromAuthenticatedUsers() {
        this.contextRunner.withPropertyValues("management.endpoint.health.show-details=never").run(( context) -> {
            ReactiveHealthEndpointWebExtension extension = context.getBean(.class);
            SecurityContext securityContext = mock(.class);
            assertThat(extension.health(securityContext).block(Duration.ofSeconds(30)).getBody().getDetails()).isEmpty();
        });
    }

    @Test
    public void detailsCanBeHiddenFromUnauthorizedUsers() {
        this.contextRunner.withPropertyValues("management.endpoint.health.show-details=when-authorized", "management.endpoint.health.roles=ACTUATOR").run(( context) -> {
            ReactiveHealthEndpointWebExtension extension = context.getBean(.class);
            SecurityContext securityContext = mock(.class);
            given(securityContext.getPrincipal()).willReturn(mock(.class));
            given(securityContext.isUserInRole("ACTUATOR")).willReturn(false);
            assertThat(extension.health(securityContext).block(Duration.ofSeconds(30)).getBody().getDetails()).isEmpty();
        });
    }

    @Test
    public void detailsCanBeShownToAuthorizedUsers() {
        this.contextRunner.withPropertyValues("management.endpoint.health.show-details=when-authorized", "management.endpoint.health.roles=ACTUATOR").run(( context) -> {
            ReactiveHealthEndpointWebExtension extension = context.getBean(.class);
            SecurityContext securityContext = mock(.class);
            given(securityContext.getPrincipal()).willReturn(mock(.class));
            given(securityContext.isUserInRole("ACTUATOR")).willReturn(true);
            assertThat(extension.health(securityContext).block(Duration.ofSeconds(30)).getBody().getDetails()).isNotEmpty();
        });
    }

    @Test
    public void roleCanBeCustomized() {
        this.contextRunner.withPropertyValues("management.endpoint.health.show-details=when-authorized", "management.endpoint.health.roles=ADMIN").run(( context) -> {
            ReactiveHealthEndpointWebExtension extension = context.getBean(.class);
            SecurityContext securityContext = mock(.class);
            given(securityContext.getPrincipal()).willReturn(mock(.class));
            given(securityContext.isUserInRole("ADMIN")).willReturn(true);
            assertThat(extension.health(securityContext).block(Duration.ofSeconds(30)).getBody().getDetails()).isNotEmpty();
        });
    }

    @Test
    public void registryCanBeAltered() {
        this.contextRunner.withUserConfiguration(ReactiveHealthEndpointWebExtensionTests.HealthIndicatorsConfiguration.class).withPropertyValues("management.endpoint.health.show-details=always").run(( context) -> {
            ReactiveHealthIndicatorRegistry registry = context.getBean(.class);
            ReactiveHealthEndpointWebExtension extension = context.getBean(.class);
            assertThat(extension.health(null).block(Duration.ofSeconds(30)).getBody().getDetails()).containsOnlyKeys("application", "first", "second");
            assertThat(registry.unregister("second")).isNotNull();
            assertThat(extension.health(null).block(Duration.ofSeconds(30)).getBody().getDetails()).containsKeys("application", "first");
        });
    }

    @Configuration
    static class HealthIndicatorsConfiguration {
        @Bean
        public HealthIndicator firstHealthIndicator() {
            return () -> Health.up().build();
        }

        @Bean
        public ReactiveHealthIndicator secondHealthIndicator() {
            return () -> Mono.just(Health.up().build());
        }
    }
}

