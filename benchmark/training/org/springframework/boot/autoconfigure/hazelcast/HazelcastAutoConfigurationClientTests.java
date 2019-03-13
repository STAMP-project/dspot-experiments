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


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static HazelcastClientConfiguration.CONFIG_SYSTEM_PROPERTY;


/**
 * Tests for {@link HazelcastAutoConfiguration} specific to the client.
 *
 * @author Vedran Pavic
 * @author Stephane Nicoll
 */
public class HazelcastAutoConfigurationClientTests {
    /**
     * Servers the test clients will connect to.
     */
    private static HazelcastInstance hazelcastServer;

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(HazelcastAutoConfiguration.class));

    @Test
    public void systemProperty() {
        this.contextRunner.withSystemProperties((((CONFIG_SYSTEM_PROPERTY) + "=classpath:org/springframework/boot/autoconfigure/hazelcast/") + "hazelcast-client-specific.xml")).run(( context) -> assertThat(context).getBean(.class).isInstanceOf(.class).has(nameStartingWith("hz.client_")));
    }

    @Test
    public void explicitConfigFile() {
        this.contextRunner.withPropertyValues(("spring.hazelcast.config=org/springframework/boot/autoconfigure/" + "hazelcast/hazelcast-client-specific.xml")).run(( context) -> assertThat(context).getBean(.class).isInstanceOf(.class).has(nameStartingWith("hz.client_")));
    }

    @Test
    public void explicitConfigUrl() {
        this.contextRunner.withPropertyValues("spring.hazelcast.config=hazelcast-client-default.xml").run(( context) -> assertThat(context).getBean(.class).isInstanceOf(.class).has(nameStartingWith("hz.client_")));
    }

    @Test
    public void unknownConfigFile() {
        this.contextRunner.withPropertyValues("spring.hazelcast.config=foo/bar/unknown.xml").run(( context) -> assertThat(context).getFailure().isInstanceOf(.class).hasMessageContaining("foo/bar/unknown.xml"));
    }

    @Test
    public void clientConfigTakesPrecedence() {
        this.contextRunner.withUserConfiguration(HazelcastAutoConfigurationClientTests.HazelcastServerAndClientConfig.class).withPropertyValues("spring.hazelcast.config=this-is-ignored.xml").run(( context) -> assertThat(context).getBean(.class).isInstanceOf(.class));
    }

    @Configuration
    static class HazelcastServerAndClientConfig {
        @Bean
        public Config config() {
            return new Config();
        }

        @Bean
        public ClientConfig clientConfig() {
            return new ClientConfig();
        }
    }
}

