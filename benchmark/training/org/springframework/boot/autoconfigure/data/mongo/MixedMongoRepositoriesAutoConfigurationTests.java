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
package org.springframework.boot.autoconfigure.data.mongo;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.city.City;
import org.springframework.boot.autoconfigure.data.jpa.city.CityRepository;
import org.springframework.boot.autoconfigure.data.mongo.country.Country;
import org.springframework.boot.autoconfigure.data.mongo.country.CountryRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfigurationTests;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.util.StringUtils;


/**
 * Tests for {@link MongoRepositoriesAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Oliver Gierke
 */
public class MixedMongoRepositoriesAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void testDefaultRepositoryConfiguration() {
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("spring.datasource.initialization-mode:never").applyTo(this.context);
        this.context.register(MixedMongoRepositoriesAutoConfigurationTests.TestConfiguration.class, MixedMongoRepositoriesAutoConfigurationTests.BaseConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(CountryRepository.class)).isNotNull();
    }

    @Test
    public void testMixedRepositoryConfiguration() {
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("spring.datasource.initialization-mode:never").applyTo(this.context);
        this.context.register(MixedMongoRepositoriesAutoConfigurationTests.MixedConfiguration.class, MixedMongoRepositoriesAutoConfigurationTests.BaseConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(CountryRepository.class)).isNotNull();
        assertThat(this.context.getBean(CityRepository.class)).isNotNull();
    }

    @Test
    public void testJpaRepositoryConfigurationWithMongoTemplate() {
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("spring.datasource.initialization-mode:never").applyTo(this.context);
        this.context.register(MixedMongoRepositoriesAutoConfigurationTests.JpaConfiguration.class, MixedMongoRepositoriesAutoConfigurationTests.BaseConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(CityRepository.class)).isNotNull();
    }

    @Test
    public void testJpaRepositoryConfigurationWithMongoOverlap() {
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("spring.datasource.initialization-mode:never").applyTo(this.context);
        this.context.register(MixedMongoRepositoriesAutoConfigurationTests.OverlapConfiguration.class, MixedMongoRepositoriesAutoConfigurationTests.BaseConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(CityRepository.class)).isNotNull();
    }

    @Test
    public void testJpaRepositoryConfigurationWithMongoOverlapDisabled() {
        this.context = new AnnotationConfigApplicationContext();
        TestPropertyValues.of("spring.datasource.initialization-mode:never", "spring.data.mongodb.repositories.type:none").applyTo(this.context);
        this.context.register(MixedMongoRepositoriesAutoConfigurationTests.OverlapConfiguration.class, MixedMongoRepositoriesAutoConfigurationTests.BaseConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(CityRepository.class)).isNotNull();
    }

    // Not this package or its parent
    @Configuration
    @TestAutoConfigurationPackage(MongoAutoConfigurationTests.class)
    @EnableMongoRepositories(basePackageClasses = Country.class)
    protected static class TestConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(MongoAutoConfigurationTests.class)
    @EnableMongoRepositories(basePackageClasses = Country.class)
    @EntityScan(basePackageClasses = City.class)
    @EnableJpaRepositories(basePackageClasses = CityRepository.class)
    protected static class MixedConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(MongoAutoConfigurationTests.class)
    @EntityScan(basePackageClasses = City.class)
    @EnableJpaRepositories(basePackageClasses = CityRepository.class)
    protected static class JpaConfiguration {}

    // In this one the Jpa repositories and the auto-configuration packages overlap, so
    // Mongo will try and configure the same repositories
    @Configuration
    @TestAutoConfigurationPackage(CityRepository.class)
    @EnableJpaRepositories(basePackageClasses = CityRepository.class)
    protected static class OverlapConfiguration {}

    @Configuration
    @Import(MixedMongoRepositoriesAutoConfigurationTests.Registrar.class)
    protected static class BaseConfiguration {}

    protected static class Registrar implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            List<String> names = new ArrayList<>();
            for (Class<?> type : new Class<?>[]{ DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class }) {
                names.add(type.getName());
            }
            return StringUtils.toStringArray(names);
        }
    }
}

