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
package de.codecentric.boot.admin.server.services;


import Fault.EMPTY_RESPONSE;
import MediaType.APPLICATION_JSON_VALUE;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.eventstore.ConcurrentMapEventStore;
import java.time.Duration;
import org.junit.Rule;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class StatusUpdaterTest {
    @Rule
    public WireMockRule wireMock = new WireMockRule(Options.DYNAMIC_PORT);

    private StatusUpdater updater;

    private ConcurrentMapEventStore eventStore;

    private InstanceRepository repository;

    private Instance instance;

    @Test
    public void should_change_status_to_down() {
        String body = "{ \"status\" : \"UP\", \"details\" : { \"foo\" : \"bar\" } }";
        this.wireMock.stubFor(get("/health").willReturn(okForContentType(ActuatorMediaType.V2_JSON, body).withHeader("Content-Length", Integer.toString(body.length()))));
        StepVerifier.create(this.eventStore).expectSubscription().then(() -> StepVerifier.create(this.updater.updateStatus(this.instance.getId())).verifyComplete()).assertNext(( event) -> {
            assertThat(event).isInstanceOf(.class);
            assertThat(event.getInstance()).isEqualTo(this.instance.getId());
            InstanceStatusChangedEvent statusChangedEvent = ((InstanceStatusChangedEvent) (event));
            assertThat(statusChangedEvent.getStatusInfo().getStatus()).isEqualTo("UP");
            assertThat(statusChangedEvent.getStatusInfo().getDetails()).isEqualTo(singletonMap("foo", "bar"));
        }).thenCancel().verify();
        StepVerifier.create(this.repository.find(this.instance.getId())).assertNext(( app) -> assertThat(app.getStatusInfo().getStatus()).isEqualTo("UP")).verifyComplete();
        StepVerifier.create(this.repository.computeIfPresent(this.instance.getId(), ( key, instance) -> Mono.just(instance.deregister()))).then(() -> StepVerifier.create(this.updater.updateStatus(this.instance.getId())).verifyComplete()).thenCancel().verify();
        StepVerifier.create(this.repository.find(this.instance.getId())).assertNext(( app) -> assertThat(app.getStatusInfo().getStatus()).isEqualTo("UNKNOWN")).verifyComplete();
    }

    @Test
    public void should_not_change_status() {
        String body = "{ \"status\" : \"UNKNOWN\" }";
        this.wireMock.stubFor(get("/health").willReturn(okJson(body).withHeader("Content-Type", Integer.toString(body.length()))));
        StepVerifier.create(this.eventStore).expectSubscription().then(() -> StepVerifier.create(this.updater.updateStatus(this.instance.getId())).verifyComplete()).expectNoEvent(Duration.ofMillis(100L)).thenCancel().verify();
    }

    @Test
    public void should_change_status_to_up() {
        this.wireMock.stubFor(get("/health").willReturn(ok()));
        StepVerifier.create(this.eventStore).expectSubscription().then(() -> StepVerifier.create(this.updater.updateStatus(this.instance.getId())).verifyComplete()).assertNext(( event) -> assertThat(event).isInstanceOf(.class)).thenCancel().verify();
        StepVerifier.create(this.repository.find(this.instance.getId())).assertNext(( app) -> assertThat(app.getStatusInfo().getStatus()).isEqualTo("UP")).verifyComplete();
    }

    @Test
    public void should_change_status_to_down_with_details() {
        String body = "{ \"foo\" : \"bar\" }";
        this.wireMock.stubFor(get("/health").willReturn(status(503).withHeader("Content-Type", APPLICATION_JSON_VALUE).withHeader("Content-Length", Integer.toString(body.length())).withBody(body)));
        StepVerifier.create(this.eventStore).expectSubscription().then(() -> StepVerifier.create(this.updater.updateStatus(this.instance.getId())).verifyComplete()).assertNext(( event) -> assertThat(event).isInstanceOf(.class)).thenCancel().verify();
        StepVerifier.create(this.repository.find(this.instance.getId())).assertNext(( app) -> {
            assertThat(app.getStatusInfo().getStatus()).isEqualTo("DOWN");
            assertThat(app.getStatusInfo().getDetails()).containsEntry("foo", "bar");
        }).verifyComplete();
    }

    @Test
    public void should_change_status_to_down_without_details_incompatible_content_type() {
        this.wireMock.stubFor(get("/health").willReturn(status(503)));
        StepVerifier.create(this.eventStore).expectSubscription().then(() -> StepVerifier.create(this.updater.updateStatus(this.instance.getId())).verifyComplete()).assertNext(( event) -> assertThat(event).isInstanceOf(.class)).thenCancel().verify();
        StepVerifier.create(this.repository.find(this.instance.getId())).assertNext(( app) -> {
            assertThat(app.getStatusInfo().getStatus()).isEqualTo("DOWN");
            assertThat(app.getStatusInfo().getDetails()).containsEntry("status", 503).containsEntry("error", "Service Unavailable");
        }).verifyComplete();
    }

    @Test
    public void should_change_status_to_down_without_details_no_body() {
        this.wireMock.stubFor(get("/health").willReturn(status(503).withHeader("Content-Type", APPLICATION_JSON_VALUE)));
        StepVerifier.create(this.eventStore).expectSubscription().then(() -> StepVerifier.create(this.updater.updateStatus(this.instance.getId())).verifyComplete()).assertNext(( event) -> assertThat(event).isInstanceOf(.class)).thenCancel().verify();
        StepVerifier.create(this.repository.find(this.instance.getId())).assertNext(( app) -> {
            assertThat(app.getStatusInfo().getStatus()).isEqualTo("DOWN");
            assertThat(app.getStatusInfo().getDetails()).containsEntry("status", 503).containsEntry("error", "Service Unavailable");
        }).verifyComplete();
    }

    @Test
    public void should_change_status_to_offline() {
        this.wireMock.stubFor(get("/health").willReturn(aResponse().withFault(EMPTY_RESPONSE)));
        StepVerifier.create(this.eventStore).expectSubscription().then(() -> StepVerifier.create(this.updater.updateStatus(this.instance.getId())).verifyComplete()).assertNext(( event) -> assertThat(event).isInstanceOf(.class)).thenCancel().verify();
        StepVerifier.create(this.repository.find(this.instance.getId())).assertNext(( app) -> {
            assertThat(app.getStatusInfo().getStatus()).isEqualTo("OFFLINE");
            assertThat(app.getStatusInfo().getDetails()).containsKeys("message", "exception");
        }).verifyComplete();
        StepVerifier.create(this.updater.updateStatus(this.instance.getId())).verifyComplete();
    }

    @Test
    public void should_retry() {
        this.wireMock.stubFor(get("/health").inScenario("retry").whenScenarioStateIs(STARTED).willReturn(aResponse().withFixedDelay(5000)).willSetStateTo("recovered"));
        this.wireMock.stubFor(get("/health").inScenario("retry").whenScenarioStateIs("recovered").willReturn(ok()));
        StepVerifier.create(this.eventStore).expectSubscription().then(() -> StepVerifier.create(this.updater.updateStatus(this.instance.getId())).verifyComplete()).assertNext(( event) -> assertThat(event).isInstanceOf(.class)).thenCancel().verify();
        StepVerifier.create(this.repository.find(this.instance.getId())).assertNext(( app) -> assertThat(app.getStatusInfo().getStatus()).isEqualTo("UP")).verifyComplete();
    }
}

