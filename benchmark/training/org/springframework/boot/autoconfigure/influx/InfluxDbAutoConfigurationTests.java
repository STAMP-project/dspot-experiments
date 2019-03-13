/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.autoconfigure.influx;


import okhttp3.OkHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link InfluxDbAutoConfiguration}.
 *
 * @author Sergey Kuptsov
 * @author Stephane Nicoll
 * @author Edd? Mel?ndez
 */
public class InfluxDbAutoConfigurationTests {
    @Rule
    public final OutputCapture output = new OutputCapture();

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(InfluxDbAutoConfiguration.class));

    @Test
    public void influxDbRequiresUrl() {
        this.contextRunner.run(( context) -> assertThat(context.getBeansOfType(.class)).isEmpty());
    }

    @Test
    public void influxDbCanBeCustomized() {
        this.contextRunner.withPropertyValues("spring.influx.url=http://localhost", "spring.influx.password:password", "spring.influx.user:user").run(( context) -> assertThat(context.getBeansOfType(.class)).hasSize(1));
    }

    @Test
    public void influxDbCanBeCreatedWithoutCredentials() {
        this.contextRunner.withPropertyValues("spring.influx.url=http://localhost").run(( context) -> {
            assertThat(context.getBeansOfType(.class)).hasSize(1);
            int readTimeout = getReadTimeoutProperty(context);
            assertThat(readTimeout).isEqualTo(10000);
        });
    }

    @Test
    public void influxDbWithOkHttpClientBuilderProvider() {
        this.contextRunner.withUserConfiguration(InfluxDbAutoConfigurationTests.CustomOkHttpClientBuilderProviderConfig.class).withPropertyValues("spring.influx.url=http://localhost").run(( context) -> {
            assertThat(context.getBeansOfType(.class)).hasSize(1);
            int readTimeout = getReadTimeoutProperty(context);
            assertThat(readTimeout).isEqualTo(40000);
        });
    }

    @Configuration
    static class CustomOkHttpClientBuilderProviderConfig {
        @Bean
        public InfluxDbOkHttpClientBuilderProvider influxDbOkHttpClientBuilderProvider() {
            return () -> new OkHttpClient.Builder().readTimeout(40, TimeUnit.SECONDS);
        }
    }
}

