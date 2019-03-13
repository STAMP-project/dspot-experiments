/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.autoconfigure.data.neo4j;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.Test;
import org.mockito.Mockito;
import org.neo4j.ogm.config.Configuration.Builder;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.session.event.EventListener;
import org.neo4j.ogm.session.event.PersistenceEvent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.data.neo4j.city.City;
import org.springframework.boot.autoconfigure.data.neo4j.country.Country;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.annotation.EnableBookmarkManagement;
import org.springframework.data.neo4j.mapping.Neo4jMappingContext;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;


/**
 * Tests for {@link Neo4jDataAutoConfiguration}. Tests should not use the embedded driver
 * as it requires the complete Neo4j-Kernel and server to function properly.
 *
 * @author Stephane Nicoll
 * @author Michael Hunger
 * @author Vince Bickers
 * @author Andy Wilkinson
 * @author Kazuki Shimizu
 * @author Michael Simons
 */
public class Neo4jDataAutoConfigurationTests {
    private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withClassLoader(new FilteredClassLoader(EmbeddedDriver.class)).withUserConfiguration(Neo4jDataAutoConfigurationTests.TestConfiguration.class).withConfiguration(AutoConfigurations.of(Neo4jDataAutoConfiguration.class, TransactionAutoConfiguration.class));

    @Test
    public void defaultConfiguration() {
        this.contextRunner.withPropertyValues("spring.data.neo4j.uri=http://localhost:8989").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void customNeo4jTransactionManagerUsingProperties() {
        this.contextRunner.withPropertyValues("spring.transaction.default-timeout=30", "spring.transaction.rollback-on-commit-failure:true").run(( context) -> {
            Neo4jTransactionManager transactionManager = context.getBean(.class);
            assertThat(transactionManager.getDefaultTimeout()).isEqualTo(30);
            assertThat(transactionManager.isRollbackOnCommitFailure()).isTrue();
        });
    }

    @Test
    public void customSessionFactory() {
        this.contextRunner.withUserConfiguration(Neo4jDataAutoConfigurationTests.CustomSessionFactory.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void customConfiguration() {
        this.contextRunner.withUserConfiguration(Neo4jDataAutoConfigurationTests.CustomConfiguration.class).run(( context) -> {
            assertThat(context.getBean(.class)).isSameAs(context.getBean("myConfiguration"));
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void usesAutoConfigurationPackageToPickUpDomainTypes() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setClassLoader(new FilteredClassLoader(EmbeddedDriver.class));
        String cityPackage = City.class.getPackage().getName();
        AutoConfigurationPackages.register(context, cityPackage);
        context.register(Neo4jDataAutoConfiguration.class, Neo4jRepositoriesAutoConfiguration.class);
        try {
            context.refresh();
            Neo4jDataAutoConfigurationTests.assertDomainTypesDiscovered(context.getBean(Neo4jMappingContext.class), City.class);
        } finally {
            context.close();
        }
    }

    @Test
    public void openSessionInViewInterceptorCanBeDisabled() {
        this.contextRunner.withPropertyValues("spring.data.neo4j.open-in-view:false").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void shouldBeAbleToUseNativeTypesWithBolt() {
        this.contextRunner.withPropertyValues("spring.data.neo4j.uri=bolt://localhost:7687", "spring.data.neo4j.use-native-types:true").withConfiguration(AutoConfigurations.of(Neo4jDataAutoConfiguration.class, TransactionAutoConfiguration.class)).run(( context) -> assertThat(context).getBean(.class).hasFieldOrPropertyWithValue("useNativeTypes", true));
    }

    @Test
    public void shouldFailWhenNativeTypesAreNotAvailable() {
        this.contextRunner.withClassLoader(new FilteredClassLoader("org.neo4j.ogm.drivers.bolt.types")).withPropertyValues("spring.data.neo4j.uri=bolt://localhost:7687", "spring.data.neo4j.use-native-types:true").withConfiguration(AutoConfigurations.of(Neo4jDataAutoConfiguration.class, TransactionAutoConfiguration.class)).run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(.class);
        });
    }

    @Test
    public void shouldFailWhenNativeTypesAreNotSupported() {
        this.contextRunner.withPropertyValues("spring.data.neo4j.uri=http://localhost:7474", "spring.data.neo4j.use-native-types:true").withConfiguration(AutoConfigurations.of(Neo4jDataAutoConfiguration.class, TransactionAutoConfiguration.class)).run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(.class);
        });
    }

    @Test
    public void eventListenersAreAutoRegistered() {
        this.contextRunner.withUserConfiguration(Neo4jDataAutoConfigurationTests.EventListenerConfiguration.class).run(( context) -> {
            Session session = context.getBean(.class).openSession();
            session.notifyListeners(new PersistenceEvent(null, Event.TYPE.PRE_SAVE));
            verify(context.getBean("eventListenerOne", .class)).onPreSave(any(.class));
            verify(context.getBean("eventListenerTwo", .class)).onPreSave(any(.class));
        });
    }

    @Test
    public void providesARequestScopedBookmarkManagerIfNecessaryAndPossible() {
        this.contextRunner.withUserConfiguration(Neo4jDataAutoConfigurationTests.BookmarkManagementEnabledConfiguration.class).run(( context) -> {
            BeanDefinition bookmarkManagerBean = context.getBeanFactory().getBeanDefinition("scopedTarget.bookmarkManager");
            assertThat(bookmarkManagerBean.getScope()).isEqualTo(WebApplicationContext.SCOPE_REQUEST);
        });
    }

    @Test
    public void providesASingletonScopedBookmarkManagerIfNecessaryAndPossible() {
        new ApplicationContextRunner().withClassLoader(new FilteredClassLoader(EmbeddedDriver.class)).withUserConfiguration(Neo4jDataAutoConfigurationTests.TestConfiguration.class, Neo4jDataAutoConfigurationTests.BookmarkManagementEnabledConfiguration.class).withConfiguration(AutoConfigurations.of(Neo4jDataAutoConfiguration.class, TransactionAutoConfiguration.class)).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBeanDefinitionNames()).doesNotContain("scopedTarget.bookmarkManager");
        });
    }

    @Test
    public void doesNotProvideABookmarkManagerIfNotPossible() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(Caffeine.class, EmbeddedDriver.class)).withUserConfiguration(Neo4jDataAutoConfigurationTests.BookmarkManagementEnabledConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Configuration
    @EntityScan(basePackageClasses = Country.class)
    static class TestConfiguration {}

    @Configuration
    static class CustomSessionFactory {
        @Bean
        public SessionFactory customSessionFactory() {
            return Mockito.mock(SessionFactory.class);
        }
    }

    @Configuration
    static class CustomConfiguration {
        @Bean
        public Configuration myConfiguration() {
            return new Builder().uri("http://localhost:12345").build();
        }
    }

    @Configuration
    @EnableBookmarkManagement
    static class BookmarkManagementEnabledConfiguration {}

    @Configuration
    static class EventListenerConfiguration {
        @Bean
        public EventListener eventListenerOne() {
            return Mockito.mock(EventListener.class);
        }

        @Bean
        public EventListener eventListenerTwo() {
            return Mockito.mock(EventListener.class);
        }
    }
}

