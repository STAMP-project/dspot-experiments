/**
 * Copyright 2014-2018 the original author or authors.
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
package de.codecentric.boot.admin.server.web;


import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.OPTIONS;
import HttpStatus.BAD_GATEWAY;
import HttpStatus.GATEWAY_TIMEOUT;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import HttpStatus.SERVICE_UNAVAILABLE;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.codecentric.boot.admin.server.utils.MediaType;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.http.ActuatorMediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;


public abstract class AbstractInstancesProxyControllerIntegrationTest {
    private static final String ACTUATOR_CONTENT_TYPE = (ActuatorMediaType.V2_JSON) + ";charset=UTF-8";

    private static ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, Object>>() {};

    @Rule
    public WireMockRule wireMock = new WireMockRule(WireMockConfiguration.options().dynamicPort().extensions(new ConnectionCloseExtension()));

    private WebTestClient client;

    private String instanceId;

    @Test
    public void should_return_status_503_404() {
        // 503 on invalid instance
        this.client.get().uri("/instances/{instanceId}/actuator/info", "UNKNOWN").accept(MediaType.ACTUATOR_V2_MEDIATYPE).exchange().expectStatus().isEqualTo(SERVICE_UNAVAILABLE);
        // 404 on non-existent endpoint
        this.client.get().uri("/instances/{instanceId}/actuator/not-exist", this.instanceId).accept(MediaType.ACTUATOR_V2_MEDIATYPE).exchange().expectStatus().isEqualTo(NOT_FOUND);
    }

    @Test
    public void should_return_status_502_504() {
        // 502 on invalid response
        this.client.get().uri("/instances/{instanceId}/actuator/invalid", this.instanceId).accept(MediaType.ACTUATOR_V2_MEDIATYPE).exchange().expectStatus().isEqualTo(BAD_GATEWAY);
        // 504 on read timeout
        this.client.get().uri("/instances/{instanceId}/actuator/timeout", this.instanceId).accept(MediaType.ACTUATOR_V2_MEDIATYPE).exchange().expectStatus().isEqualTo(GATEWAY_TIMEOUT);
    }

    @Test
    public void should_forward_requests() {
        this.client.options().uri("/instances/{instanceId}/actuator/env", this.instanceId).accept(MediaType.ACTUATOR_V2_MEDIATYPE).exchange().expectStatus().isEqualTo(OK).expectHeader().valueEquals(ALLOW, HEAD.name(), GET.name(), OPTIONS.name());
        this.client.get().uri("/instances/{instanceId}/actuator/test", this.instanceId).accept(MediaType.ACTUATOR_V2_MEDIATYPE).exchange().expectStatus().isEqualTo(OK).expectBody(String.class).isEqualTo("{ \"foo\" : \"bar\" }");
        this.client.post().uri("/instances/{instanceId}/actuator/test", this.instanceId).syncBody("PAYLOAD").exchange().expectStatus().isEqualTo(OK);
        this.wireMock.verify(postRequestedFor(urlEqualTo("/mgmt/test")).withRequestBody(equalTo("PAYLOAD")));
        this.client.delete().uri("/instances/{instanceId}/actuator/test", this.instanceId).exchange().expectStatus().isEqualTo(INTERNAL_SERVER_ERROR).expectBody(String.class).isEqualTo("{\"error\": \"You\'re doing it wrong!\"}");
        this.wireMock.verify(postRequestedFor(urlEqualTo("/mgmt/test")).withRequestBody(equalTo("PAYLOAD")));
    }

    @Test
    public void should_forward_requests_with_spaces_in_path() {
        this.client.get().uri("/instances/{instanceId}/actuator/test/has spaces", this.instanceId).accept(MediaType.ACTUATOR_V2_MEDIATYPE).exchange().expectStatus().isEqualTo(OK).expectBody(String.class).isEqualTo("{ \"foo\" : \"bar-with-spaces\" }");
        this.wireMock.verify(getRequestedFor(urlEqualTo("/mgmt/test/has%20spaces")));
    }
}

