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
package org.springframework.boot.autoconfigure.couchbase;


import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseBucket;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Integration tests for {@link CouchbaseAutoConfiguration}.
 *
 * @author Stephane Nicoll
 */
public class CouchbaseAutoConfigurationIntegrationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(PropertyPlaceholderAutoConfiguration.class, CouchbaseAutoConfiguration.class));

    @Rule
    public final CouchbaseTestServer couchbase = new CouchbaseTestServer();

    @Test
    public void defaultConfiguration() {
        this.contextRunner.withPropertyValues("spring.couchbase.bootstrapHosts=localhost").run(( context) -> assertThat(context).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class).hasSingleBean(.class));
    }

    @Test
    public void customConfiguration() {
        this.contextRunner.withUserConfiguration(CouchbaseAutoConfigurationIntegrationTests.CustomConfiguration.class).withPropertyValues("spring.couchbase.bootstrapHosts=localhost").run(( context) -> {
            assertThat(context.getBeansOfType(.class)).hasSize(2);
            assertThat(context.getBeansOfType(.class)).hasSize(1);
            assertThat(context.getBeansOfType(.class)).hasSize(1);
            assertThat(context.getBeansOfType(.class)).hasSize(2);
        });
    }

    @Configuration
    static class CustomConfiguration {
        @Bean
        public Cluster myCustomCouchbaseCluster() {
            return Mockito.mock(Cluster.class);
        }

        @Bean
        public Bucket myCustomCouchbaseClient() {
            return Mockito.mock(CouchbaseBucket.class);
        }
    }
}

