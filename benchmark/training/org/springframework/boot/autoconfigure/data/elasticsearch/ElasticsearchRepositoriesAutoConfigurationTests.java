/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.autoconfigure.data.elasticsearch;


import org.elasticsearch.client.Client;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.data.alt.elasticsearch.CityElasticsearchDbRepository;
import org.springframework.boot.autoconfigure.data.elasticsearch.city.City;
import org.springframework.boot.autoconfigure.data.elasticsearch.city.CityRepository;
import org.springframework.boot.autoconfigure.data.empty.EmptyDataPackage;
import org.springframework.boot.testsupport.testcontainers.ElasticsearchContainer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


/**
 * Tests for {@link ElasticsearchRepositoriesAutoConfiguration}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class ElasticsearchRepositoriesAutoConfigurationTests {
    @ClassRule
    public static ElasticsearchContainer elasticsearch = new ElasticsearchContainer();

    private AnnotationConfigApplicationContext context;

    @Test
    public void testDefaultRepositoryConfiguration() {
        load(ElasticsearchRepositoriesAutoConfigurationTests.TestConfiguration.class);
        assertThat(this.context.getBean(CityRepository.class)).isNotNull();
        assertThat(this.context.getBean(Client.class)).isNotNull();
    }

    @Test
    public void testNoRepositoryConfiguration() {
        load(ElasticsearchRepositoriesAutoConfigurationTests.EmptyConfiguration.class);
        assertThat(this.context.getBean(Client.class)).isNotNull();
    }

    @Test
    public void doesNotTriggerDefaultRepositoryDetectionIfCustomized() {
        load(ElasticsearchRepositoriesAutoConfigurationTests.CustomizedConfiguration.class);
        assertThat(this.context.getBean(CityElasticsearchDbRepository.class)).isNotNull();
    }

    @Configuration
    @TestAutoConfigurationPackage(City.class)
    protected static class TestConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    protected static class EmptyConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(ElasticsearchRepositoriesAutoConfigurationTests.class)
    @EnableElasticsearchRepositories(basePackageClasses = CityElasticsearchDbRepository.class)
    protected static class CustomizedConfiguration {}
}

