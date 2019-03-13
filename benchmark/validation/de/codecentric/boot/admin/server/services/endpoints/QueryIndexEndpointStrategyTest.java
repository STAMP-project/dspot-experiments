/**
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.boot.admin.server.services.endpoints;


import ActuatorMediaType.V2_JSON;
import Endpoint.ACTUATOR_INDEX;
import MediaType.TEXT_PLAIN_VALUE;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.values.Endpoints;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import java.time.Duration;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import reactor.test.StepVerifier;

import static de.codecentric.boot.admin.server.web.client.InstanceExchangeFilterFunctions.retry;
import static de.codecentric.boot.admin.server.web.client.InstanceExchangeFilterFunctions.rewriteEndpointUrl;
import static de.codecentric.boot.admin.server.web.client.InstanceExchangeFilterFunctions.timeout;


public class QueryIndexEndpointStrategyTest {
    @Rule
    public WireMockRule wireMock = new WireMockRule(Options.DYNAMIC_PORT);

    private de.codecentric.boot.admin.server.web.client.InstanceWebClient instanceWebClient = de.codecentric.boot.admin.server.web.client.InstanceWebClient.builder().filter(rewriteEndpointUrl()).filter(retry(0, Collections.singletonMap(ACTUATOR_INDEX, 1))).filter(timeout(Duration.ofSeconds(2), Collections.emptyMap())).build();

    @Test
    public void should_return_endpoints() {
        // given
        Instance instance = Instance.create(InstanceId.of("id")).register(Registration.create("test", this.wireMock.url("/mgmt/health")).managementUrl(this.wireMock.url("/mgmt")).build());
        String body = "{\"_links\":{\"metrics-requiredMetricName\":{\"templated\":true,\"href\":\"\\/mgmt\\/metrics\\/{requiredMetricName}\"},\"self\":{\"templated\":false,\"href\":\"\\/mgmt\"},\"metrics\":{\"templated\":false,\"href\":\"\\/mgmt\\/stats\"},\"info\":{\"templated\":false,\"href\":\"\\/mgmt\\/info\"}}}";
        this.wireMock.stubFor(get("/mgmt").willReturn(ok(body).withHeader("Content-Type", V2_JSON).withHeader("Content-Length", Integer.toString(body.length()))));
        QueryIndexEndpointStrategy strategy = new QueryIndexEndpointStrategy(this.instanceWebClient);
        // when
        // 
        // then
        StepVerifier.create(strategy.detectEndpoints(instance)).expectNext(Endpoints.single("metrics", "/mgmt/stats").withEndpoint("info", "/mgmt/info")).verifyComplete();
    }

    @Test
    public void should_return_empty_on_empty_endpoints() {
        // given
        Instance instance = Instance.create(InstanceId.of("id")).register(Registration.create("test", this.wireMock.url("/mgmt/health")).managementUrl(this.wireMock.url("/mgmt")).build());
        String body = "{\"_links\":{}}";
        this.wireMock.stubFor(get("/mgmt").willReturn(okJson(body).withHeader("Content-Type", V2_JSON).withHeader("Content-Length", Integer.toString(body.length()))));
        QueryIndexEndpointStrategy strategy = new QueryIndexEndpointStrategy(this.instanceWebClient);
        // when
        // then
        StepVerifier.create(strategy.detectEndpoints(instance)).verifyComplete();
    }

    @Test
    public void should_return_empty_on_not_found() {
        // given
        Instance instance = Instance.create(InstanceId.of("id")).register(Registration.create("test", this.wireMock.url("/mgmt/health")).managementUrl(this.wireMock.url("/mgmt")).build());
        this.wireMock.stubFor(get("/mgmt").willReturn(notFound()));
        QueryIndexEndpointStrategy strategy = new QueryIndexEndpointStrategy(this.instanceWebClient);
        // when
        // then
        StepVerifier.create(strategy.detectEndpoints(instance)).verifyComplete();
    }

    @Test
    public void should_return_empty_on_wrong_content_type() {
        // given
        Instance instance = Instance.create(InstanceId.of("id")).register(Registration.create("test", this.wireMock.url("/mgmt/health")).managementUrl(this.wireMock.url("/mgmt")).build());
        String body = "HELLOW WORLD";
        this.wireMock.stubFor(get("/mgmt").willReturn(ok(body).withHeader("Content-Type", TEXT_PLAIN_VALUE).withHeader("Content-Length", Integer.toString(body.length()))));
        QueryIndexEndpointStrategy strategy = new QueryIndexEndpointStrategy(this.instanceWebClient);
        // when
        // then
        StepVerifier.create(strategy.detectEndpoints(instance)).verifyComplete();
    }

    @Test
    public void should_return_empty_when_mgmt_equals_service_url() {
        // given
        Instance instance = Instance.create(InstanceId.of("id")).register(Registration.create("test", this.wireMock.url("/app/health")).managementUrl(this.wireMock.url("/app")).serviceUrl(this.wireMock.url("/app")).build());
        QueryIndexEndpointStrategy strategy = new QueryIndexEndpointStrategy(this.instanceWebClient);
        // when/then
        StepVerifier.create(strategy.detectEndpoints(instance)).verifyComplete();
        this.wireMock.verify(0, anyRequestedFor(urlPathEqualTo("/app")));
    }

    @Test
    public void should_retry() {
        // given
        Instance instance = Instance.create(InstanceId.of("id")).register(Registration.create("test", this.wireMock.url("/mgmt/health")).managementUrl(this.wireMock.url("/mgmt")).build());
        String body = "{\"_links\":{\"metrics-requiredMetricName\":{\"templated\":true,\"href\":\"\\/mgmt\\/metrics\\/{requiredMetricName}\"},\"self\":{\"templated\":false,\"href\":\"\\/mgmt\"},\"metrics\":{\"templated\":false,\"href\":\"\\/mgmt\\/stats\"},\"info\":{\"templated\":false,\"href\":\"\\/mgmt\\/info\"}}}";
        this.wireMock.stubFor(get("/mgmt").inScenario("retry").whenScenarioStateIs(STARTED).willReturn(aResponse().withFixedDelay(5000)).willSetStateTo("recovered"));
        this.wireMock.stubFor(get("/mgmt").inScenario("retry").whenScenarioStateIs("recovered").willReturn(ok(body).withHeader("Content-Type", V2_JSON).withHeader("Content-Length", Integer.toString(body.length()))));
        QueryIndexEndpointStrategy strategy = new QueryIndexEndpointStrategy(this.instanceWebClient);
        // when
        // 
        // then
        StepVerifier.create(strategy.detectEndpoints(instance)).expectNext(Endpoints.single("metrics", "/mgmt/stats").withEndpoint("info", "/mgmt/info")).verifyComplete();
    }
}

