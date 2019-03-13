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
package org.springframework.boot.actuate.management;


import HttpStatus.SERVICE_UNAVAILABLE;
import MediaType.APPLICATION_OCTET_STREAM;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.actuate.endpoint.web.test.WebEndpointRunners;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileCopyUtils;


/**
 * Integration tests for {@link HeapDumpWebEndpoint} exposed by Jersey, Spring MVC, and
 * WebFlux.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
@RunWith(WebEndpointRunners.class)
public class HeapDumpWebEndpointWebIntegrationTests {
    private static WebTestClient client;

    private static ConfigurableApplicationContext context;

    private HeapDumpWebEndpointWebIntegrationTests.TestHeapDumpWebEndpoint endpoint;

    @Test
    public void invokeWhenNotAvailableShouldReturnServiceUnavailableStatus() {
        this.endpoint.setAvailable(false);
        HeapDumpWebEndpointWebIntegrationTests.client.get().uri("/actuator/heapdump").exchange().expectStatus().isEqualTo(SERVICE_UNAVAILABLE);
    }

    @Test
    public void getRequestShouldReturnHeapDumpInResponseBody() throws Exception {
        HeapDumpWebEndpointWebIntegrationTests.client.get().uri("/actuator/heapdump").exchange().expectStatus().isOk().expectHeader().contentType(APPLICATION_OCTET_STREAM).expectBody(String.class).isEqualTo("HEAPDUMP");
        assertHeapDumpFileIsDeleted();
    }

    @Configuration
    public static class TestConfiguration {
        @Bean
        public HeapDumpWebEndpoint endpoint() {
            return new HeapDumpWebEndpointWebIntegrationTests.TestHeapDumpWebEndpoint();
        }
    }

    private static class TestHeapDumpWebEndpoint extends HeapDumpWebEndpoint {
        private boolean available;

        private String heapDump = "HEAPDUMP";

        private File file;

        TestHeapDumpWebEndpoint() {
            super(TimeUnit.SECONDS.toMillis(1));
            reset();
        }

        public void reset() {
            this.available = true;
        }

        @Override
        protected HeapDumper createHeapDumper() {
            return ( file, live) -> {
                this.file = file;
                if (!(org.springframework.boot.actuate.management.TestHeapDumpWebEndpoint.this.available)) {
                    throw new HeapDumperUnavailableException("Not available", null);
                }
                if (file.exists()) {
                    throw new IOException("File exists");
                }
                FileCopyUtils.copy(org.springframework.boot.actuate.management.TestHeapDumpWebEndpoint.this.heapDump.getBytes(), file);
            };
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }
    }
}

