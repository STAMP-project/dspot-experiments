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
package org.springframework.boot.autoconfigure.hazelcast;


import com.hazelcast.core.HazelcastInstance;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link HazelcastJpaDependencyAutoConfiguration}.
 *
 * @author Stephane Nicoll
 */
public class HazelcastJpaDependencyAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, HazelcastJpaDependencyAutoConfiguration.class)).withPropertyValues("spring.datasource.generate-unique-name=true", "spring.datasource.initialization-mode=never");

    @Test
    public void registrationIfHazelcastInstanceHasRegularBeanName() {
        this.contextRunner.withUserConfiguration(HazelcastJpaDependencyAutoConfigurationTests.HazelcastConfiguration.class).run(( context) -> {
            assertThat(postProcessors(context)).containsKey("hazelcastInstanceJpaDependencyPostProcessor");
            assertThat(entityManagerFactoryDependencies(context)).contains("hazelcastInstance");
        });
    }

    @Test
    public void noRegistrationIfHazelcastInstanceHasCustomBeanName() {
        this.contextRunner.withUserConfiguration(HazelcastJpaDependencyAutoConfigurationTests.HazelcastCustomNameConfiguration.class).run(( context) -> {
            assertThat(entityManagerFactoryDependencies(context)).doesNotContain("hazelcastInstance");
            assertThat(postProcessors(context)).doesNotContainKey("hazelcastInstanceJpaDependencyPostProcessor");
        });
    }

    @Test
    public void noRegistrationWithNoHazelcastInstance() {
        this.contextRunner.run(( context) -> {
            assertThat(entityManagerFactoryDependencies(context)).doesNotContain("hazelcastInstance");
            assertThat(postProcessors(context)).doesNotContainKey("hazelcastInstanceJpaDependencyPostProcessor");
        });
    }

    @Test
    public void noRegistrationWithNoEntityManagerFactory() {
        new ApplicationContextRunner().withUserConfiguration(HazelcastJpaDependencyAutoConfigurationTests.HazelcastConfiguration.class).withConfiguration(AutoConfigurations.of(HazelcastJpaDependencyAutoConfiguration.class)).run(( context) -> assertThat(postProcessors(context)).doesNotContainKey("hazelcastInstanceJpaDependencyPostProcessor"));
    }

    @Configuration
    static class HazelcastConfiguration {
        @Bean
        public HazelcastInstance hazelcastInstance() {
            return Mockito.mock(HazelcastInstance.class);
        }
    }

    @Configuration
    static class HazelcastCustomNameConfiguration {
        @Bean
        public HazelcastInstance myHazelcastInstance() {
            return Mockito.mock(HazelcastInstance.class);
        }
    }
}

