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
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link MongoReactiveRepositoriesAutoConfiguration}.
 *
 * @author Mark Paluch
 * @author Andy Wilkinson
 */
public class MongoReactiveRepositoriesAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class, MongoReactiveRepositoriesAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class));

    @Test
    public void testDefaultRepositoryConfiguration() {
        this.contextRunner.withUserConfiguration(MongoReactiveRepositoriesAutoConfigurationTests.TestConfiguration.class).run(( context) -> {
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
        this.contextRunner.withUserConfiguration(MongoReactiveRepositoriesAutoConfigurationTests.EmptyConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void doesNotTriggerDefaultRepositoryDetectionIfCustomized() {
        this.contextRunner.withUserConfiguration(MongoReactiveRepositoriesAutoConfigurationTests.CustomizedConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void autoConfigurationShouldNotKickInEvenIfManualConfigDidNotCreateAnyRepositories() {
        this.contextRunner.withUserConfiguration(MongoReactiveRepositoriesAutoConfigurationTests.SortOfInvalidCustomConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void enablingImperativeRepositoriesDisablesReactiveRepositories() {
        this.contextRunner.withUserConfiguration(MongoReactiveRepositoriesAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.data.mongodb.repositories.type=imperative").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void enablingNoRepositoriesDisablesReactiveRepositories() {
        this.contextRunner.withUserConfiguration(MongoReactiveRepositoriesAutoConfigurationTests.TestConfiguration.class).withPropertyValues("spring.data.mongodb.repositories.type=none").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Configuration
    @TestAutoConfigurationPackage(City.class)
    protected static class TestConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    protected static class EmptyConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(MongoReactiveRepositoriesAutoConfigurationTests.class)
    @EnableMongoRepositories(basePackageClasses = CityMongoDbRepository.class)
    protected static class CustomizedConfiguration {}

    // To not find any repositories
    @Configuration
    @EnableReactiveMongoRepositories("foo.bar")
    @TestAutoConfigurationPackage(MongoReactiveRepositoriesAutoConfigurationTests.class)
    protected static class SortOfInvalidCustomConfiguration {}
}

