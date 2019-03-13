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
package org.springframework.boot.autoconfigure.hazelcast;


import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testsupport.runner.classpath.ClassPathExclusions;
import org.springframework.boot.testsupport.runner.classpath.ModifiedClassPathRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import static HazelcastServerConfiguration.CONFIG_SYSTEM_PROPERTY;


/**
 * Tests for {@link HazelcastAutoConfiguration} when the client library is not present.
 *
 * @author Stephane Nicoll
 */
@RunWith(ModifiedClassPathRunner.class)
@ClassPathExclusions("hazelcast-client-*.jar")
public class HazelcastAutoConfigurationServerTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(HazelcastAutoConfiguration.class));

    @Test
    public void defaultConfigFile() {
        // hazelcast.xml present in root classpath
        this.contextRunner.run(( context) -> {
            Config config = context.getBean(.class).getConfig();
            assertThat(config.getConfigurationUrl()).isEqualTo(new ClassPathResource("hazelcast.xml").getURL());
        });
    }

    @Test
    public void systemProperty() {
        this.contextRunner.withSystemProperties(((CONFIG_SYSTEM_PROPERTY) + "=classpath:org/springframework/boot/autoconfigure/hazelcast/hazelcast-specific.xml")).run(( context) -> {
            Config config = context.getBean(.class).getConfig();
            assertThat(config.getQueueConfigs().keySet()).containsOnly("foobar");
        });
    }

    @Test
    public void explicitConfigFile() {
        this.contextRunner.withPropertyValues(("spring.hazelcast.config=org/springframework/boot/autoconfigure/hazelcast/" + "hazelcast-specific.xml")).run(( context) -> {
            Config config = context.getBean(.class).getConfig();
            assertThat(config.getConfigurationFile()).isEqualTo(new ClassPathResource(("org/springframework/boot/autoconfigure/hazelcast" + "/hazelcast-specific.xml")).getFile());
        });
    }

    @Test
    public void explicitConfigUrl() {
        this.contextRunner.withPropertyValues("spring.hazelcast.config=hazelcast-default.xml").run(( context) -> {
            Config config = context.getBean(.class).getConfig();
            assertThat(config.getConfigurationUrl()).isEqualTo(new ClassPathResource("hazelcast-default.xml").getURL());
        });
    }

    @Test
    public void unknownConfigFile() {
        this.contextRunner.withPropertyValues("spring.hazelcast.config=foo/bar/unknown.xml").run(( context) -> assertThat(context).getFailure().isInstanceOf(.class).hasMessageContaining("foo/bar/unknown.xml"));
    }

    @Test
    public void configInstanceWithName() {
        Config config = new Config("my-test-instance");
        HazelcastInstance existing = Hazelcast.newHazelcastInstance(config);
        try {
            this.contextRunner.withUserConfiguration(HazelcastAutoConfigurationServerTests.HazelcastConfigWithName.class).withPropertyValues("spring.hazelcast.config=this-is-ignored.xml").run(( context) -> {
                HazelcastInstance hazelcast = context.getBean(.class);
                assertThat(hazelcast.getConfig().getInstanceName()).isEqualTo("my-test-instance");
                // Should reuse any existing instance by default.
                assertThat(hazelcast).isEqualTo(existing);
            });
        } finally {
            existing.shutdown();
        }
    }

    @Test
    public void configInstanceWithoutName() {
        this.contextRunner.withUserConfiguration(HazelcastAutoConfigurationServerTests.HazelcastConfigNoName.class).withPropertyValues("spring.hazelcast.config=this-is-ignored.xml").run(( context) -> {
            Config config = context.getBean(.class).getConfig();
            Map<String, QueueConfig> queueConfigs = config.getQueueConfigs();
            assertThat(queueConfigs.keySet()).containsOnly("another-queue");
        });
    }

    @Configuration
    static class HazelcastConfigWithName {
        @Bean
        public Config myHazelcastConfig() {
            return new Config("my-test-instance");
        }
    }

    @Configuration
    static class HazelcastConfigNoName {
        @Bean
        public Config anotherHazelcastConfig() {
            Config config = new Config();
            config.addQueueConfig(new QueueConfig("another-queue"));
            return config;
        }
    }
}

