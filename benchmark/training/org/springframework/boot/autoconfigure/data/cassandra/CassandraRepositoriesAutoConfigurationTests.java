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
package org.springframework.boot.autoconfigure.data.cassandra;


import ComponentScan.Filter;
import com.datastax.driver.core.Session;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.alt.cassandra.CityCassandraRepository;
import org.springframework.boot.autoconfigure.data.cassandra.city.City;
import org.springframework.boot.autoconfigure.data.empty.EmptyDataPackage;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;


/**
 * Tests for {@link CassandraRepositoriesAutoConfiguration}.
 *
 * @author Edd? Mel?ndez
 * @author Mark Paluch
 * @author Stephane Nicoll
 */
public class CassandraRepositoriesAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(CassandraAutoConfiguration.class, CassandraRepositoriesAutoConfiguration.class, CassandraDataAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class));

    @Test
    public void testDefaultRepositoryConfiguration() {
        this.contextRunner.withUserConfiguration(CassandraRepositoriesAutoConfigurationTests.TestConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            assertThat(getInitialEntitySet(context)).hasSize(1);
        });
    }

    @Test
    public void testNoRepositoryConfiguration() {
        this.contextRunner.withUserConfiguration(CassandraRepositoriesAutoConfigurationTests.TestExcludeConfiguration.class, CassandraRepositoriesAutoConfigurationTests.EmptyConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(getInitialEntitySet(context)).hasSize(1).containsOnly(.class);
        });
    }

    @Test
    public void doesNotTriggerDefaultRepositoryDetectionIfCustomized() {
        this.contextRunner.withUserConfiguration(CassandraRepositoriesAutoConfigurationTests.TestExcludeConfiguration.class, CassandraRepositoriesAutoConfigurationTests.CustomizedConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(getInitialEntitySet(context)).hasSize(1).containsOnly(.class);
        });
    }

    @Test
    public void enablingReactiveRepositoriesDisablesImperativeRepositories() {
        this.contextRunner.withUserConfiguration(CassandraRepositoriesAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.data.cassandra.repositories.type=reactive").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void enablingNoRepositoriesDisablesImperativeRepositories() {
        this.contextRunner.withUserConfiguration(CassandraRepositoriesAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.data.cassandra.repositories.type=none").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Configuration
    @TestAutoConfigurationPackage(City.class)
    static class TestConfiguration {
        @Bean
        public Session session() {
            return Mockito.mock(Session.class);
        }
    }

    @Configuration
    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    static class EmptyConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(CassandraRepositoriesAutoConfigurationTests.class)
    @EnableCassandraRepositories(basePackageClasses = CityCassandraRepository.class)
    static class CustomizedConfiguration {}

    @Configuration
    @ComponentScan(excludeFilters = @Filter(classes = { Session.class }, type = FilterType.ASSIGNABLE_TYPE))
    static class TestExcludeConfiguration {}
}

