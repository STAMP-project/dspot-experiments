/**
 * Copyright 2019 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.spring.actuate;


import HttpMethod.POST;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import MediaType.JSON_UTF_8;
import MediaType.PROTOBUF;
import TextFormat.CONTENT_TYPE_004;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.linecorp.armeria.client.ClientOptionsBuilder;
import com.linecorp.armeria.client.Clients;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.server.Server;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Inject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;


/**
 * This uses {@link com.linecorp.armeria.spring.ArmeriaAutoConfiguration} for integration tests.
 * application-autoConfTest.yml will be loaded with minimal settings to make it work.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = org.springframework.boot.test.context.TestConfiguration.class)
@ActiveProfiles({ "local", "autoConfTest" })
@DirtiesContext
@EnableAutoConfiguration
@ImportAutoConfiguration(ArmeriaSpringActuatorAutoConfiguration.class)
public class ArmeriaSpringActuatorAutoConfigurationTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final TypeReference<Map<String, Object>> JSON_MAP = new TypeReference<Map<String, Object>>() {};

    private static final String TEST_LOGGER_NAME = "com.linecorp.armeria.spring.actuate.testing.TestLogger";

    // We use this logger to test the /loggers endpoint, so set the name manually instead of using class name.
    @SuppressWarnings("unused")
    private static final Logger TEST_LOGGER = LoggerFactory.getLogger(ArmeriaSpringActuatorAutoConfigurationTest.TEST_LOGGER_NAME);

    @SpringBootApplication
    public static class TestConfiguration {}

    @Rule
    public TestRule globalTimeout = new DisableOnDebug(new Timeout(10, TimeUnit.SECONDS));

    @Inject
    private Server server;

    private HttpClient client;

    @Test
    public void testHealth() throws Exception {
        final AggregatedHttpMessage res = client.get("/internal/actuator/health").aggregate().get();
        assertThat(res.status()).isEqualTo(OK);
        final Map<String, Object> values = ArmeriaSpringActuatorAutoConfigurationTest.OBJECT_MAPPER.readValue(res.content().array(), ArmeriaSpringActuatorAutoConfigurationTest.JSON_MAP);
        assertThat(values).containsEntry("status", "UP");
    }

    @Test
    public void testLoggers() throws Exception {
        final String loggerPath = "/internal/actuator/loggers/" + (ArmeriaSpringActuatorAutoConfigurationTest.TEST_LOGGER_NAME);
        AggregatedHttpMessage res = client.get(loggerPath).aggregate().get();
        assertThat(res.status()).isEqualTo(OK);
        Map<String, Object> values = ArmeriaSpringActuatorAutoConfigurationTest.OBJECT_MAPPER.readValue(res.content().array(), ArmeriaSpringActuatorAutoConfigurationTest.JSON_MAP);
        assertThat(values).containsEntry("effectiveLevel", "DEBUG");
        res = client.execute(HttpHeaders.of(POST, loggerPath).contentType(JSON_UTF_8), ArmeriaSpringActuatorAutoConfigurationTest.OBJECT_MAPPER.writeValueAsBytes(ImmutableMap.of("configuredLevel", "info"))).aggregate().get();
        assertThat(res.status()).isEqualTo(NO_CONTENT);
        res = client.get(loggerPath).aggregate().get();
        values = ArmeriaSpringActuatorAutoConfigurationTest.OBJECT_MAPPER.readValue(res.content().array(), ArmeriaSpringActuatorAutoConfigurationTest.JSON_MAP);
        assertThat(values).containsEntry("effectiveLevel", "INFO");
        client.post(loggerPath, ArmeriaSpringActuatorAutoConfigurationTest.OBJECT_MAPPER.writeValueAsBytes(ImmutableMap.of())).aggregate().get();
    }

    @Test
    public void testPrometheus() throws Exception {
        final AggregatedHttpMessage res = client.get("/internal/actuator/prometheus").aggregate().get();
        assertThat(res.status()).isEqualTo(OK);
        assertThat(res.contentType()).isEqualTo(MediaType.parse(CONTENT_TYPE_004));
        assertThat(res.contentAscii()).startsWith("# HELP ");
    }

    @Test
    public void testHeapdump() throws Exception {
        final HttpClient client = Clients.newDerivedClient(this.client, ( options) -> {
            return new ClientOptionsBuilder(options).defaultMaxResponseLength(0).build();
        });
        final HttpResponse res = client.get("/internal/actuator/heapdump");
        final AtomicLong remainingBytes = new AtomicLong();
        // Skip the last HttpData.
        StepVerifier.create(res).assertNext(( obj) -> {
            assertThat(obj).isInstanceOf(.class);
            final HttpHeaders headers = ((HttpHeaders) (obj));
            assertThat(headers.status()).isEqualTo(HttpStatus.OK);
            assertThat(headers.contentType()).isEqualTo(MediaType.OCTET_STREAM);
            assertThat(headers.get(HttpHeaderNames.CONTENT_DISPOSITION)).startsWith("attachment;filename=heapdump");
            final Long contentLength = headers.getLong(HttpHeaderNames.CONTENT_LENGTH);
            assertThat(contentLength).isPositive();
            remainingBytes.set(contentLength);
        }).thenConsumeWhile(( obj) -> {
            assertThat(obj).isInstanceOf(.class);
            final HttpData data = ((HttpData) (obj));
            final long newRemainingBytes = remainingBytes.addAndGet((-(data.length())));
            assertThat(newRemainingBytes).isNotNegative();
            return newRemainingBytes > 0;// Stop at the last HttpData.

        }).expectNextCount(1).verifyComplete();
        assertThat(remainingBytes).hasValue(0);
    }

    @Test
    public void testLinks() throws Exception {
        final AggregatedHttpMessage res = client.get("/internal/actuator").aggregate().get();
        assertThat(res.status()).isEqualTo(OK);
        final Map<String, Object> values = ArmeriaSpringActuatorAutoConfigurationTest.OBJECT_MAPPER.readValue(res.content().array(), ArmeriaSpringActuatorAutoConfigurationTest.JSON_MAP);
        assertThat(values).containsKey("_links");
    }

    @Test
    public void testMissingMediaType() throws Exception {
        final String loggerPath = "/internal/actuator/loggers/" + (ArmeriaSpringActuatorAutoConfigurationTest.TEST_LOGGER_NAME);
        final AggregatedHttpMessage res = client.execute(HttpHeaders.of(POST, loggerPath), ArmeriaSpringActuatorAutoConfigurationTest.OBJECT_MAPPER.writeValueAsBytes(ImmutableMap.of("configuredLevel", "info"))).aggregate().get();
        assertThat(res.status()).isEqualTo(UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    public void testInvalidMediaType() throws Exception {
        final String loggerPath = "/internal/actuator/loggers/" + (ArmeriaSpringActuatorAutoConfigurationTest.TEST_LOGGER_NAME);
        final AggregatedHttpMessage res = client.execute(HttpHeaders.of(POST, loggerPath).contentType(PROTOBUF), ArmeriaSpringActuatorAutoConfigurationTest.OBJECT_MAPPER.writeValueAsBytes(ImmutableMap.of("configuredLevel", "info"))).aggregate().get();
        assertThat(res.status()).isEqualTo(UNSUPPORTED_MEDIA_TYPE);
    }
}

