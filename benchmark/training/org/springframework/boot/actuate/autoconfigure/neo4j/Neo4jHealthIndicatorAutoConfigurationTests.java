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
package org.springframework.boot.actuate.autoconfigure.neo4j;


import Health.Builder;
import org.junit.Test;
import org.mockito.Mockito;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.neo4j.Neo4jHealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link Neo4jHealthIndicatorAutoConfiguration}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class Neo4jHealthIndicatorAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(Neo4jHealthIndicatorAutoConfigurationTests.Neo4jConfiguration.class).withConfiguration(AutoConfigurations.of(Neo4jHealthIndicatorAutoConfiguration.class, HealthIndicatorAutoConfiguration.class));

    @Test
    public void runShouldCreateIndicator() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class).doesNotHaveBean(.class));
    }

    @Test
    public void runWhenDisabledShouldNotCreateIndicator() {
        this.contextRunner.withPropertyValues("management.health.neo4j.enabled:false").run(( context) -> assertThat(context).doesNotHaveBean(.class).hasSingleBean(.class));
    }

    @Test
    public void defaultIndicatorCanBeReplaced() {
        this.contextRunner.withUserConfiguration(Neo4jHealthIndicatorAutoConfigurationTests.CustomIndicatorConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            Health health = context.getBean(.class).health();
            assertThat(health.getDetails()).containsOnly(entry("test", true));
        });
    }

    @Configuration
    protected static class Neo4jConfiguration {
        @Bean
        public SessionFactory sessionFactory() {
            return Mockito.mock(SessionFactory.class);
        }
    }

    @Configuration
    protected static class CustomIndicatorConfiguration {
        @Bean
        public Neo4jHealthIndicator neo4jHealthIndicator(SessionFactory sessionFactory) {
            return new Neo4jHealthIndicator(sessionFactory) {
                @Override
                protected void extractResult(Session session, Health.Builder builder) {
                    builder.up().withDetail("test", true);
                }
            };
        }
    }
}

