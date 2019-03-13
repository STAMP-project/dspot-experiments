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
package org.springframework.boot.actuate.autoconfigure.metrics.orm.jpa;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.autoconfigure.metrics.test.MetricsRun;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;


/**
 * Tests for {@link HibernateMetricsAutoConfiguration}.
 *
 * @author Rui Figueira
 * @author Stephane Nicoll
 */
public class HibernateMetricsAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().with(MetricsRun.simple()).withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, HibernateMetricsAutoConfiguration.class)).withUserConfiguration(HibernateMetricsAutoConfigurationTests.BaseConfiguration.class);

    @Test
    public void autoConfiguredEntityManagerFactoryWithStatsIsInstrumented() {
        this.contextRunner.withPropertyValues("spring.jpa.properties.hibernate.generate_statistics:true").run(( context) -> {
            MeterRegistry registry = context.getBean(.class);
            registry.get("hibernate.statements").tags("entityManagerFactory", "entityManagerFactory").meter();
        });
    }

    @Test
    public void autoConfiguredEntityManagerFactoryWithoutStatsIsNotInstrumented() {
        this.contextRunner.withPropertyValues("spring.jpa.properties.hibernate.generate_statistics:false").run(( context) -> {
            context.getBean(.class).unwrap(.class);
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("hibernate.statements").meter()).isNull();
        });
    }

    @Test
    public void entityManagerFactoryInstrumentationCanBeDisabled() {
        this.contextRunner.withPropertyValues("management.metrics.enable.hibernate=false", "spring.jpa.properties.hibernate.generate_statistics:true").run(( context) -> {
            context.getBean(.class).unwrap(.class);
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("hibernate.statements").meter()).isNull();
        });
    }

    @Test
    public void allEntityManagerFactoriesCanBeInstrumented() {
        this.contextRunner.withPropertyValues("spring.jpa.properties.hibernate.generate_statistics:true").withUserConfiguration(HibernateMetricsAutoConfigurationTests.TwoEntityManagerFactoriesConfiguration.class).run(( context) -> {
            context.getBean("firstEntityManagerFactory", .class).unwrap(.class);
            context.getBean("secondOne", .class).unwrap(.class);
            MeterRegistry registry = context.getBean(.class);
            registry.get("hibernate.statements").tags("entityManagerFactory", "first").meter();
            registry.get("hibernate.statements").tags("entityManagerFactory", "secondOne").meter();
        });
    }

    @Test
    public void entityManagerFactoryInstrumentationIsDisabledIfNotHibernateSessionFactory() {
        this.contextRunner.withPropertyValues("spring.jpa.properties.hibernate.generate_statistics:true").withUserConfiguration(HibernateMetricsAutoConfigurationTests.NonHibernateEntityManagerFactoryConfiguration.class).run(( context) -> {
            // ensure EntityManagerFactory is not a Hibernate SessionFactory
            assertThatThrownBy(() -> context.getBean(.class).unwrap(.class)).isInstanceOf(.class);
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("hibernate.statements").meter()).isNull();
        });
    }

    @Test
    public void entityManagerFactoryInstrumentationIsDisabledIfHibernateIsNotAvailable() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(SessionFactory.class)).withUserConfiguration(HibernateMetricsAutoConfigurationTests.NonHibernateEntityManagerFactoryConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("hibernate.statements").meter()).isNull();
        });
    }

    @Configuration
    static class BaseConfiguration {
        @Bean
        public SimpleMeterRegistry simpleMeterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @Entity
    static class MyEntity {
        @Id
        @GeneratedValue
        private Long id;
    }

    @Configuration
    static class TwoEntityManagerFactoriesConfiguration {
        private static final Class<?>[] PACKAGE_CLASSES = new Class<?>[]{ HibernateMetricsAutoConfigurationTests.MyEntity.class };

        @Primary
        @Bean
        public LocalContainerEntityManagerFactoryBean firstEntityManagerFactory(DataSource ds) {
            return createSessionFactory(ds);
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean secondOne(DataSource ds) {
            return createSessionFactory(ds);
        }

        private LocalContainerEntityManagerFactoryBean createSessionFactory(DataSource ds) {
            Map<String, String> jpaProperties = new HashMap<>();
            jpaProperties.put("hibernate.generate_statistics", "true");
            return dataSource(ds).packages(HibernateMetricsAutoConfigurationTests.TwoEntityManagerFactoriesConfiguration.PACKAGE_CLASSES).build();
        }
    }

    @Configuration
    static class NonHibernateEntityManagerFactoryConfiguration {
        @Bean
        public EntityManagerFactory entityManagerFactory() {
            EntityManagerFactory mockedFactory = Mockito.mock(EntityManagerFactory.class);
            // enforces JPA contract
            BDDMockito.given(mockedFactory.unwrap(ArgumentMatchers.<Class<SessionFactory>>any())).willThrow(PersistenceException.class);
            return mockedFactory;
        }
    }
}

