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
package org.springframework.boot.autoconfigure.data.neo4j;


import org.junit.Test;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.data.jpa.city.City;
import org.springframework.boot.autoconfigure.data.jpa.city.CityRepository;
import org.springframework.boot.autoconfigure.data.neo4j.country.Country;
import org.springframework.boot.autoconfigure.data.neo4j.country.CountryRepository;
import org.springframework.boot.autoconfigure.data.neo4j.empty.EmptyMarker;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;


/**
 * Tests for {@link Neo4jRepositoriesAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Oliver Gierke
 * @author Michael Hunger
 * @author Vince Bickers
 * @author Stephane Nicoll
 */
public class MixedNeo4jRepositoriesAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void testDefaultRepositoryConfiguration() {
        load(MixedNeo4jRepositoriesAutoConfigurationTests.TestConfiguration.class);
        assertThat(this.context.getBean(CountryRepository.class)).isNotNull();
    }

    @Test
    public void testMixedRepositoryConfiguration() {
        load(MixedNeo4jRepositoriesAutoConfigurationTests.MixedConfiguration.class);
        assertThat(this.context.getBean(CountryRepository.class)).isNotNull();
        assertThat(this.context.getBean(CityRepository.class)).isNotNull();
    }

    @Test
    public void testJpaRepositoryConfigurationWithNeo4jTemplate() {
        load(MixedNeo4jRepositoriesAutoConfigurationTests.JpaConfiguration.class);
        assertThat(this.context.getBean(CityRepository.class)).isNotNull();
    }

    @Test
    public void testJpaRepositoryConfigurationWithNeo4jOverlapDisabled() {
        load(MixedNeo4jRepositoriesAutoConfigurationTests.OverlapConfiguration.class, "spring.data.neo4j.repositories.enabled:false");
        assertThat(this.context.getBean(CityRepository.class)).isNotNull();
    }

    // Not this package or its parent
    @Configuration
    @TestAutoConfigurationPackage(EmptyMarker.class)
    @EnableNeo4jRepositories(basePackageClasses = Country.class)
    protected static class TestConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(EmptyMarker.class)
    @EnableNeo4jRepositories(basePackageClasses = Country.class)
    @EntityScan(basePackageClasses = City.class)
    @EnableJpaRepositories(basePackageClasses = CityRepository.class)
    protected static class MixedConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(EmptyMarker.class)
    @EntityScan(basePackageClasses = City.class)
    @EnableJpaRepositories(basePackageClasses = CityRepository.class)
    protected static class JpaConfiguration {}

    // In this one the Jpa repositories and the auto-configuration packages overlap, so
    // Neo4j will try and configure the same repositories
    @Configuration
    @TestAutoConfigurationPackage(CityRepository.class)
    @EnableJpaRepositories(basePackageClasses = CityRepository.class)
    protected static class OverlapConfiguration {}
}

