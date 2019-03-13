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
package org.springframework.boot.actuate.metrics.web.reactive.client;


import HttpMethod.GET;
import HttpStatus.OK;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


/**
 * Tests for {@link MetricsWebClientFilterFunction}
 *
 * @author Brian Clozel
 */
public class MetricsWebClientFilterFunctionTests {
    private static final String URI_TEMPLATE_ATTRIBUTE = (WebClient.class.getName()) + ".uriTemplate";

    private MeterRegistry registry;

    private MetricsWebClientFilterFunction filterFunction;

    private ClientResponse response;

    private ExchangeFunction exchange;

    @Test
    public void filterShouldRecordTimer() {
        ClientRequest request = ClientRequest.create(GET, URI.create("http://example.com/projects/spring-boot")).build();
        BDDMockito.given(this.response.statusCode()).willReturn(OK);
        this.filterFunction.filter(request, this.exchange).block(Duration.ofSeconds(30));
        assertThat(this.registry.get("http.client.requests").tags("method", "GET", "uri", "/projects/spring-boot", "status", "200").timer().count()).isEqualTo(1);
    }

    @Test
    public void filterWhenUriTemplatePresentShouldRecordTimer() {
        ClientRequest request = ClientRequest.create(GET, URI.create("http://example.com/projects/spring-boot")).attribute(MetricsWebClientFilterFunctionTests.URI_TEMPLATE_ATTRIBUTE, "/projects/{project}").build();
        BDDMockito.given(this.response.statusCode()).willReturn(OK);
        this.filterFunction.filter(request, this.exchange).block(Duration.ofSeconds(30));
        assertThat(this.registry.get("http.client.requests").tags("method", "GET", "uri", "/projects/{project}", "status", "200").timer().count()).isEqualTo(1);
    }

    @Test
    public void filterWhenIoExceptionThrownShouldRecordTimer() {
        ClientRequest request = ClientRequest.create(GET, URI.create("http://example.com/projects/spring-boot")).build();
        ExchangeFunction errorExchange = ( r) -> Mono.error(new IOException());
        this.filterFunction.filter(request, errorExchange).onErrorResume(IOException.class, ( t) -> Mono.empty()).block(Duration.ofSeconds(30));
        assertThat(this.registry.get("http.client.requests").tags("method", "GET", "uri", "/projects/spring-boot", "status", "IO_ERROR").timer().count()).isEqualTo(1);
    }

    @Test
    public void filterWhenExceptionThrownShouldRecordTimer() {
        ClientRequest request = ClientRequest.create(GET, URI.create("http://example.com/projects/spring-boot")).build();
        ExchangeFunction exchange = ( r) -> Mono.error(new IllegalArgumentException());
        this.filterFunction.filter(request, exchange).onErrorResume(IllegalArgumentException.class, ( t) -> Mono.empty()).block(Duration.ofSeconds(30));
        assertThat(this.registry.get("http.client.requests").tags("method", "GET", "uri", "/projects/spring-boot", "status", "CLIENT_ERROR").timer().count()).isEqualTo(1);
    }

    @Test
    public void filterWhenExceptionAndRetryShouldNotCumulateRecordTime() {
        ClientRequest request = ClientRequest.create(GET, URI.create("http://example.com/projects/spring-boot")).build();
        ExchangeFunction exchange = ( r) -> Mono.error(new IllegalArgumentException()).delaySubscription(Duration.ofMillis(300)).cast(.class);
        this.filterFunction.filter(request, exchange).retry(1).onErrorResume(IllegalArgumentException.class, ( t) -> Mono.empty()).block(Duration.ofSeconds(30));
        Timer timer = this.registry.get("http.client.requests").tags("method", "GET", "uri", "/projects/spring-boot", "status", "CLIENT_ERROR").timer();
        assertThat(timer.count()).isEqualTo(2);
        assertThat(timer.max(TimeUnit.MILLISECONDS)).isLessThan(600);
    }
}

