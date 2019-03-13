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


import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.alt.mongo.CityMongoDbRepository;
import org.springframework.boot.autoconfigure.data.empty.EmptyDataPackage;
import org.springframework.boot.autoconfigure.data.mongo.city.City;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link MongoRepositoriesAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Oliver Gierke
 */
public class MongoRepositoriesAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class));

    @Test
    public void testDefaultRepositoryConfiguration() {
        this.contextRunner.withUserConfiguration(MongoRepositoriesAutoConfigurationTests.TestConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            MongoMappingContext mappingContext = context.getBean(.class);
            @SuppressWarnings("unchecked")
            Set<? extends Class<?>> entities = ((Set<? extends Class<?>>) (ReflectionTestUtils.getField(mappingContext, "initialEntitySet")));
            assertThat(entities).hasSize(1);
        });
    }

    @Test
    public void testNoRepositoryConfiguration() {
        this.contextRunner.withUserConfiguration(MongoRepositoriesAutoConfigurationTests.EmptyConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void doesNotTriggerDefaultRepositoryDetectionIfCustomized() {
        this.contextRunner.withUserConfiguration(MongoRepositoriesAutoConfigurationTests.CustomizedConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void autoConfigurationShouldNotKickInEvenIfManualConfigDidNotCreateAnyRepositories() {
        this.contextRunner.withUserConfiguration(MongoRepositoriesAutoConfigurationTests.SortOfInvalidCustomConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void enablingReactiveRepositoriesDisablesImperativeRepositories() {
        this.contextRunner.withUserConfiguration(MongoRepositoriesAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.data.mongodb.repositories.type=reactive").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void enablingNoRepositoriesDisablesImperativeRepositories() {
        this.contextRunner.withUserConfiguration(MongoRepositoriesAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.data.mongodb.repositories.type=none").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Configuration
    @TestAutoConfigurationPackage(City.class)
    protected static class TestConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    protected static class EmptyConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(MongoRepositoriesAutoConfigurationTests.class)
    @EnableMongoRepositories(basePackageClasses = CityMongoDbRepository.class)
    protected static class CustomizedConfiguration {}

    // To not find any repositories
    @Configuration
    @EnableMongoRepositories("foo.bar")
    @TestAutoConfigurationPackage(MongoRepositoriesAutoConfigurationTests.class)
    protected static class SortOfInvalidCustomConfiguration {}
}

