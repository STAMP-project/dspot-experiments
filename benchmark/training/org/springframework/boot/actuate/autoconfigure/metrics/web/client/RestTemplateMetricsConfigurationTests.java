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
import org.springframework.boot.actuate.autoconfigure.metrics.test.MetricsRun;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.boot.web.client.RestTemplateBuilder;


/**
 * Tests for {@link RestTemplateMetricsConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Jon Schneider
 * @author Raheela Aslam
 */
public class RestTemplateMetricsConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().with(MetricsRun.simple()).withConfiguration(AutoConfigurations.of(RestTemplateAutoConfiguration.class, HttpClientMetricsAutoConfiguration.class));

    @Rule
    public final OutputCapture output = new OutputCapture();

    @Test
    public void restTemplateCreatedWithBuilderIsInstrumented() {
        this.contextRunner.run(( context) -> {
            MeterRegistry registry = context.getBean(.class);
            RestTemplateBuilder builder = context.getBean(.class);
            validateRestTemplate(builder, registry);
        });
    }

    @Test
    public void restTemplateCanBeCustomizedManually() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            RestTemplateBuilder customBuilder = new RestTemplateBuilder().customizers(context.getBean(.class));
            MeterRegistry registry = context.getBean(.class);
            validateRestTemplate(customBuilder, registry);
        });
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

    @Test
    public void backsOffWhenRestTemplateBuilderIsMissing() {
        new ApplicationContextRunner().with(MetricsRun.simple()).withConfiguration(AutoConfigurations.of(HttpClientMetricsAutoConfiguration.class)).run(( context) -> assertThat(context).doesNotHaveBean(.class).doesNotHaveBean(.class));
    }
}

