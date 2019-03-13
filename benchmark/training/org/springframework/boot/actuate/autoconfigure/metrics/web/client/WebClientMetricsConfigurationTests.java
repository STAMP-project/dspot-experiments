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
package org.springframework.boot.actuate.autoconfigure.metrics.web.client;


import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.autoconfigure.metrics.test.MetricsRun;
import org.springframework.boot.actuate.metrics.web.reactive.client.WebClientExchangeTagsProvider;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


/**
 * Tests for {@link WebClientMetricsConfiguration}
 *
 * @author Brian Clozel
 * @author Stephane Nicoll
 */
public class WebClientMetricsConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().with(MetricsRun.simple()).withConfiguration(AutoConfigurations.of(WebClientAutoConfiguration.class, HttpClientMetricsAutoConfiguration.class));

    @Rule
    public OutputCapture output = new OutputCapture();

    @Test
    public void webClientCreatedWithBuilderIsInstrumented() {
        this.contextRunner.run(( context) -> {
            MeterRegistry registry = context.getBean(.class);
            WebClient.Builder builder = context.getBean(.class);
            validateWebClient(builder, registry);
        });
    }

    @Test
    public void shouldNotOverrideCustomTagsProvider() {
        this.contextRunner.withUserConfiguration(WebClientMetricsConfigurationTests.CustomTagsProviderConfig.class).run(( context) -> assertThat(context).getBeans(.class).hasSize(1).containsKey("customTagsProvider"));
    }

    @Test
    public void afterMaxUrisReachedFurtherUrisAreDenied() {
        this.contextRunner.withPropertyValues("management.metrics.web.client.max-uri-tags=2").run(( context) -> {
            MeterRegistry registry = getInitializedMeterRegistry(context);
            assertThat(registry.get("http.client.requests").meters()).hasSize(2);
            assertThat(this.output.toString()).contains("Reached the maximum number of URI tags for 'http.client.requests'.").contains("Are you using 'uriVariables'?");
        });
    }

    @Test
    public void shouldNotDenyNorLogIfMaxUrisIsNotReached() {
        this.contextRunner.withPropertyValues("management.metrics.web.client.max-uri-tags=5").run(( context) -> {
            MeterRegistry registry = getInitializedMeterRegistry(context);
            assertThat(registry.get("http.client.requests").meters()).hasSize(3);
            assertThat(this.output.toString()).doesNotContain("Reached the maximum number of URI tags for 'http.client.requests'.").doesNotContain("Are you using 'uriVariables'?");
        });
    }

    @Configuration
    static class CustomTagsProviderConfig {
        @Bean
        public WebClientExchangeTagsProvider customTagsProvider() {
            return Mockito.mock(WebClientExchangeTagsProvider.class);
        }
    }
}

