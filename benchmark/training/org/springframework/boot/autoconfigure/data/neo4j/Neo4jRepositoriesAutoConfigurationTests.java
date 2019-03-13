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
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.data.alt.neo4j.CityNeo4jRepository;
import org.springframework.boot.autoconfigure.data.empty.EmptyDataPackage;
import org.springframework.boot.autoconfigure.data.neo4j.city.City;
import org.springframework.boot.autoconfigure.data.neo4j.city.CityRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.mapping.Neo4jMappingContext;
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
public class Neo4jRepositoriesAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void testDefaultRepositoryConfiguration() {
        prepareApplicationContext(Neo4jRepositoriesAutoConfigurationTests.TestConfiguration.class);
        assertThat(this.context.getBean(CityRepository.class)).isNotNull();
        Neo4jMappingContext mappingContext = this.context.getBean(Neo4jMappingContext.class);
        assertThat(mappingContext.getPersistentEntity(City.class)).isNotNull();
    }

    @Test
    public void testNoRepositoryConfiguration() {
        prepareApplicationContext(Neo4jRepositoriesAutoConfigurationTests.EmptyConfiguration.class);
        assertThat(this.context.getBean(SessionFactory.class)).isNotNull();
    }

    @Test
    public void doesNotTriggerDefaultRepositoryDetectionIfCustomized() {
        prepareApplicationContext(Neo4jRepositoriesAutoConfigurationTests.CustomizedConfiguration.class);
        assertThat(this.context.getBean(CityNeo4jRepository.class)).isNotNull();
    }

    @Test
    public void autoConfigurationShouldNotKickInEvenIfManualConfigDidNotCreateAnyRepositories() {
        prepareApplicationContext(Neo4jRepositoriesAutoConfigurationTests.SortOfInvalidCustomConfiguration.class);
        assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(() -> this.context.getBean(.class));
    }

    @Configuration
    @TestAutoConfigurationPackage(City.class)
    protected static class TestConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    protected static class EmptyConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(Neo4jRepositoriesAutoConfigurationTests.class)
    @EnableNeo4jRepositories(basePackageClasses = CityNeo4jRepository.class)
    protected static class CustomizedConfiguration {}

    // To not find any repositories
    @Configuration
    @EnableNeo4jRepositories("foo.bar")
    @TestAutoConfigurationPackage(Neo4jRepositoriesAutoConfigurationTests.class)
    protected static class SortOfInvalidCustomConfiguration {}
}

