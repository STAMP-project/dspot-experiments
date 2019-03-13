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
package org.springframework.boot.actuate.logging;


import ActuatorMediaType.V2_JSON;
import LogLevel.DEBUG;
import MediaType.APPLICATION_JSON;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.endpoint.web.test.WebEndpointRunners;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * Integration tests for {@link LoggersEndpoint} when exposed via Jersey, Spring MVC, and
 * WebFlux.
 *
 * @author Ben Hale
 * @author Phillip Webb
 * @author Edd? Mel?ndez
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 */
@RunWith(WebEndpointRunners.class)
public class LoggersEndpointWebIntegrationTests {
    private static ConfigurableApplicationContext context;

    private static WebTestClient client;

    private LoggingSystem loggingSystem;

    @Test
    public void getLoggerShouldReturnAllLoggerConfigurations() {
        BDDMockito.given(this.loggingSystem.getLoggerConfigurations()).willReturn(Collections.singletonList(new org.springframework.boot.logging.LoggerConfiguration("ROOT", null, LogLevel.DEBUG)));
        LoggersEndpointWebIntegrationTests.client.get().uri("/actuator/loggers").exchange().expectStatus().isOk().expectBody().jsonPath("$.length()").isEqualTo(2).jsonPath("levels").isEqualTo(jsonArrayOf("OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE")).jsonPath("loggers.length()").isEqualTo(1).jsonPath("loggers.ROOT.length()").isEqualTo(2).jsonPath("loggers.ROOT.configuredLevel").isEqualTo(null).jsonPath("loggers.ROOT.effectiveLevel").isEqualTo("DEBUG");
    }

    @Test
    public void getLoggerShouldReturnLogLevels() {
        BDDMockito.given(this.loggingSystem.getLoggerConfiguration("ROOT")).willReturn(new org.springframework.boot.logging.LoggerConfiguration("ROOT", null, LogLevel.DEBUG));
        LoggersEndpointWebIntegrationTests.client.get().uri("/actuator/loggers/ROOT").exchange().expectStatus().isOk().expectBody().jsonPath("$.length()").isEqualTo(2).jsonPath("configuredLevel").isEqualTo(null).jsonPath("effectiveLevel").isEqualTo("DEBUG");
    }

    @Test
    public void getLoggersWhenLoggerNotFoundShouldReturnNotFound() {
        LoggersEndpointWebIntegrationTests.client.get().uri("/actuator/loggers/com.does.not.exist").exchange().expectStatus().isNotFound();
    }

    @Test
    public void setLoggerUsingApplicationJsonShouldSetLogLevel() {
        LoggersEndpointWebIntegrationTests.client.post().uri("/actuator/loggers/ROOT").contentType(APPLICATION_JSON).syncBody(Collections.singletonMap("configuredLevel", "debug")).exchange().expectStatus().isNoContent();
        Mockito.verify(this.loggingSystem).setLogLevel("ROOT", DEBUG);
    }

    @Test
    public void setLoggerUsingActuatorV2JsonShouldSetLogLevel() {
        LoggersEndpointWebIntegrationTests.client.post().uri("/actuator/loggers/ROOT").contentType(MediaType.parseMediaType(V2_JSON)).syncBody(Collections.singletonMap("configuredLevel", "debug")).exchange().expectStatus().isNoContent();
        Mockito.verify(this.loggingSystem).setLogLevel("ROOT", DEBUG);
    }

    @Test
    public void setLoggerWithWrongLogLevelResultInBadRequestResponse() {
        LoggersEndpointWebIntegrationTests.client.post().uri("/actuator/loggers/ROOT").contentType(APPLICATION_JSON).syncBody(Collections.singletonMap("configuredLevel", "other")).exchange().expectStatus().isBadRequest();
        Mockito.verifyZeroInteractions(this.loggingSystem);
    }

    @Test
    public void setLoggerWithNullLogLevel() {
        LoggersEndpointWebIntegrationTests.client.post().uri("/actuator/loggers/ROOT").contentType(MediaType.parseMediaType(V2_JSON)).syncBody(Collections.singletonMap("configuredLevel", null)).exchange().expectStatus().isNoContent();
        Mockito.verify(this.loggingSystem).setLogLevel("ROOT", null);
    }

    @Test
    public void setLoggerWithNoLogLevel() {
        LoggersEndpointWebIntegrationTests.client.post().uri("/actuator/loggers/ROOT").contentType(MediaType.parseMediaType(V2_JSON)).syncBody(Collections.emptyMap()).exchange().expectStatus().isNoContent();
        Mockito.verify(this.loggingSystem).setLogLevel("ROOT", null);
    }

    @Test
    public void logLevelForLoggerWithNameThatCouldBeMistakenForAPathExtension() {
        BDDMockito.given(this.loggingSystem.getLoggerConfiguration("com.png")).willReturn(new org.springframework.boot.logging.LoggerConfiguration("com.png", null, LogLevel.DEBUG));
        LoggersEndpointWebIntegrationTests.client.get().uri("/actuator/loggers/com.png").exchange().expectStatus().isOk().expectBody().jsonPath("$.length()").isEqualTo(2).jsonPath("configuredLevel").isEqualTo(null).jsonPath("effectiveLevel").isEqualTo("DEBUG");
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public LoggingSystem loggingSystem() {
            return Mockito.mock(LoggingSystem.class);
        }

        @Bean
        public LoggersEndpoint endpoint(LoggingSystem loggingSystem) {
            return new LoggersEndpoint(loggingSystem);
        }
    }
}

