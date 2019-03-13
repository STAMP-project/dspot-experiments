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
package org.springframework.boot.autoconfigure.data.redis;


import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.alt.redis.CityRedisRepository;
import org.springframework.boot.autoconfigure.data.empty.EmptyDataPackage;
import org.springframework.boot.autoconfigure.data.redis.city.City;
import org.springframework.boot.autoconfigure.data.redis.city.CityRepository;
import org.springframework.boot.testsupport.testcontainers.RedisContainer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


/**
 * Tests for {@link RedisRepositoriesAutoConfiguration}.
 *
 * @author Edd? Mel?ndez
 */
public class RedisRepositoriesAutoConfigurationTests {
    @ClassRule
    public static RedisContainer redis = new RedisContainer();

    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void testDefaultRepositoryConfiguration() {
        this.context.register(RedisRepositoriesAutoConfigurationTests.TestConfiguration.class, RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(CityRepository.class)).isNotNull();
    }

    @Test
    public void testNoRepositoryConfiguration() {
        this.context.register(RedisRepositoriesAutoConfigurationTests.EmptyConfiguration.class, RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean("redisTemplate")).isNotNull();
    }

    @Test
    public void doesNotTriggerDefaultRepositoryDetectionIfCustomized() {
        this.context.register(RedisRepositoriesAutoConfigurationTests.CustomizedConfiguration.class, RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBean(CityRedisRepository.class)).isNotNull();
    }

    @Configuration
    @TestAutoConfigurationPackage(City.class)
    protected static class TestConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    protected static class EmptyConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(RedisRepositoriesAutoConfigurationTests.class)
    @EnableRedisRepositories(basePackageClasses = CityRedisRepository.class)
    static class CustomizedConfiguration {}
}

