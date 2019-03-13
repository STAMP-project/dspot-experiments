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
package org.springframework.boot.actuate.logging;


import MediaType.TEXT_PLAIN;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.boot.actuate.endpoint.web.test.WebEndpointRunners;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * Integration tests for {@link LogFileWebEndpoint} exposed by Jersey, Spring MVC, and
 * WebFlux.
 *
 * @author Andy Wilkinson
 */
@RunWith(WebEndpointRunners.class)
public class LogFileWebEndpointWebIntegrationTests {
    private static ConfigurableApplicationContext context;

    private static WebTestClient client;

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    private File logFile;

    @Test
    public void getRequestProduces404ResponseWhenLogFileNotFound() {
        LogFileWebEndpointWebIntegrationTests.client.get().uri("/actuator/logfile").exchange().expectStatus().isNotFound();
    }

    @Test
    public void getRequestProducesResponseWithLogFile() {
        TestPropertyValues.of(("logging.file.name:" + (this.logFile.getAbsolutePath()))).applyTo(LogFileWebEndpointWebIntegrationTests.context);
        LogFileWebEndpointWebIntegrationTests.client.get().uri("/actuator/logfile").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("--TEST--");
    }

    @Test
    public void getRequestThatAcceptsTextPlainProducesResponseWithLogFile() {
        TestPropertyValues.of(("logging.file:" + (this.logFile.getAbsolutePath()))).applyTo(LogFileWebEndpointWebIntegrationTests.context);
        LogFileWebEndpointWebIntegrationTests.client.get().uri("/actuator/logfile").accept(TEXT_PLAIN).exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("--TEST--");
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public LogFileWebEndpoint logFileEndpoint(Environment environment) {
            return new LogFileWebEndpoint(environment);
        }
    }
}

