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
package org.springframework.boot.autoconfigure.data.couchbase;


import org.junit.Test;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseTestConfigurer;
import org.springframework.boot.autoconfigure.data.couchbase.city.City;
import org.springframework.boot.autoconfigure.data.couchbase.city.CityRepository;
import org.springframework.boot.autoconfigure.data.empty.EmptyDataPackage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Tests for {@link CouchbaseRepositoriesAutoConfiguration}.
 *
 * @author Edd? Mel?ndez
 * @author Stephane Nicoll
 */
public class CouchbaseRepositoriesAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void couchbaseNotAvailable() {
        load(null);
        assertThat(this.context.getBeansOfType(CityRepository.class)).hasSize(0);
    }

    @Test
    public void defaultRepository() {
        load(CouchbaseRepositoriesAutoConfigurationTests.DefaultConfiguration.class);
        assertThat(this.context.getBeansOfType(CityRepository.class)).hasSize(1);
    }

    @Test
    public void reactiveRepositories() {
        load(CouchbaseRepositoriesAutoConfigurationTests.DefaultConfiguration.class, "spring.data.couchbase.repositories.type=reactive");
        assertThat(this.context.getBeansOfType(CityRepository.class)).hasSize(0);
    }

    @Test
    public void disabledRepositories() {
        load(CouchbaseRepositoriesAutoConfigurationTests.DefaultConfiguration.class, "spring.data.couchbase.repositories.type=none");
        assertThat(this.context.getBeansOfType(CityRepository.class)).hasSize(0);
    }

    @Test
    public void noRepositoryAvailable() {
        load(CouchbaseRepositoriesAutoConfigurationTests.NoRepositoryConfiguration.class);
        assertThat(this.context.getBeansOfType(CityRepository.class)).hasSize(0);
    }

    @Configuration
    @TestAutoConfigurationPackage(City.class)
    static class CouchbaseNotAvailableConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(City.class)
    @Import(CouchbaseTestConfigurer.class)
    static class DefaultConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    @Import(CouchbaseTestConfigurer.class)
    protected static class NoRepositoryConfiguration {}
}

