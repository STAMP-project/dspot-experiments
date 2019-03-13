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
package org.springframework.boot.actuate.autoconfigure.elasticsearch;


import io.searchbox.client.JestClient;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link ElasticSearchClientHealthIndicatorAutoConfiguration} and
 * {@link ElasticSearchJestHealthIndicatorAutoConfiguration}.
 *
 * @author Phillip Webb
 */
public class ElasticsearchHealthIndicatorAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(ElasticsearchAutoConfiguration.class, ElasticSearchClientHealthIndicatorAutoConfiguration.class, ElasticSearchJestHealthIndicatorAutoConfiguration.class, HealthIndicatorAutoConfiguration.class));

    @Test
    public void runShouldCreateIndicator() {
        this.contextRunner.withPropertyValues("spring.data.elasticsearch.cluster-nodes:localhost:0").withSystemProperties("es.set.netty.runtime.available.processors=false").run(( context) -> assertThat(context).hasSingleBean(.class).doesNotHaveBean(.class).doesNotHaveBean(.class));
    }

    @Test
    public void runWhenUsingJestClientShouldCreateIndicator() {
        this.contextRunner.withUserConfiguration(ElasticsearchHealthIndicatorAutoConfigurationTests.JestClientConfiguration.class).withSystemProperties("es.set.netty.runtime.available.processors=false").run(( context) -> assertThat(context).hasSingleBean(.class).doesNotHaveBean(.class).doesNotHaveBean(.class));
    }

    @Test
    public void runWhenDisabledShouldNotCreateIndicator() {
        this.contextRunner.withPropertyValues("management.health.elasticsearch.enabled:false").run(( context) -> assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class).hasSingleBean(.class));
    }

    @Configuration
    @AutoConfigureBefore(JestAutoConfiguration.class)
    protected static class JestClientConfiguration {
        @Bean
        public JestClient jestClient() {
            return Mockito.mock(JestClient.class);
        }
    }
}

