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


import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.values.Endpoints;
import de.codecentric.boot.admin.server.domain.values.Info;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import de.codecentric.boot.admin.server.eventstore.InMemoryEventStore;
import java.time.Duration;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import reactor.test.StepVerifier;


public class InfoUpdaterTest {
    @Rule
    public WireMockRule wireMock = new WireMockRule(Options.DYNAMIC_PORT);

    private InfoUpdater updater;

    private InMemoryEventStore eventStore;

    private InstanceRepository repository;

    @Test
    public void should_update_info_for_online_with_info_endpoint_only() {
        // given
        Registration registration = Registration.create("foo", this.wireMock.url("/health")).build();
        Instance instance = Instance.create(InstanceId.of("onl")).register(registration).withEndpoints(Endpoints.single("info", this.wireMock.url("/info"))).withStatusInfo(StatusInfo.ofUp());
        StepVerifier.create(this.repository.save(instance)).expectNextCount(1).verifyComplete();
        String body = "{ \"foo\": \"bar\" }";
        this.wireMock.stubFor(get("/info").willReturn(okJson(body).withHeader("Content-Length", Integer.toString(body.length()))));
        Instance noInfo = Instance.create(InstanceId.of("noinfo")).register(registration).withEndpoints(Endpoints.single("beans", this.wireMock.url("/beans"))).withStatusInfo(StatusInfo.ofUp());
        StepVerifier.create(this.repository.save(noInfo)).expectNextCount(1).verifyComplete();
        Instance offline = Instance.create(InstanceId.of("off")).register(registration).withStatusInfo(StatusInfo.ofOffline());
        StepVerifier.create(this.repository.save(offline)).expectNextCount(1).verifyComplete();
        Instance unknown = Instance.create(InstanceId.of("unk")).register(registration).withStatusInfo(StatusInfo.ofUnknown());
        StepVerifier.create(this.repository.save(unknown)).expectNextCount(1).verifyComplete();
        // when
        // then
        StepVerifier.create(this.eventStore).expectSubscription().then(() -> StepVerifier.create(this.updater.updateInfo(offline.getId())).verifyComplete()).then(() -> StepVerifier.create(this.updater.updateInfo(unknown.getId())).verifyComplete()).then(() -> StepVerifier.create(this.updater.updateInfo(noInfo.getId())).verifyComplete()).expectNoEvent(Duration.ofMillis(100L)).then(() -> StepVerifier.create(this.updater.updateInfo(instance.getId())).verifyComplete()).assertNext(( event) -> assertThat(event).isInstanceOf(.class)).thenCancel().verify();
        StepVerifier.create(this.repository.find(instance.getId())).assertNext(( app) -> assertThat(app.getInfo()).isEqualTo(Info.from(singletonMap("foo", "bar")))).verifyComplete();
    }

    @Test
    public void should_clear_info_on_http_error() {
        // given
        Instance instance = Instance.create(InstanceId.of("onl")).register(Registration.create("foo", this.wireMock.url("/health")).build()).withEndpoints(Endpoints.single("info", this.wireMock.url("/info"))).withStatusInfo(StatusInfo.ofUp()).withInfo(Info.from(Collections.singletonMap("foo", "bar")));
        StepVerifier.create(this.repository.save(instance)).expectNextCount(1).verifyComplete();
        this.wireMock.stubFor(get("/info").willReturn(serverError()));
        // when
        // then
        StepVerifier.create(this.eventStore).expectSubscription().then(() -> StepVerifier.create(this.updater.updateInfo(instance.getId())).verifyComplete()).assertNext(( event) -> assertThat(event).isInstanceOf(.class)).thenCancel().verify();
        StepVerifier.create(this.repository.find(instance.getId())).assertNext(( app) -> assertThat(app.getInfo()).isEqualTo(Info.empty())).verifyComplete();
    }

    @Test
    public void should_clear_info_on_exception() {
        this.updater = new InfoUpdater(this.repository, de.codecentric.boot.admin.server.web.client.InstanceWebClient.builder().build());
        // given
        Instance instance = Instance.create(InstanceId.of("onl")).register(Registration.create("foo", this.wireMock.url("/health")).build()).withEndpoints(Endpoints.single("info", this.wireMock.url("/info"))).withStatusInfo(StatusInfo.ofUp()).withInfo(Info.from(Collections.singletonMap("foo", "bar")));
        StepVerifier.create(this.repository.save(instance)).expectNextCount(1).verifyComplete();
        this.wireMock.stubFor(get("/info").willReturn(okJson("{ \"foo\": \"bar\" }").withFixedDelay(1500)));
        // when
        // then
        StepVerifier.create(this.eventStore).expectSubscription().then(() -> StepVerifier.create(this.updater.updateInfo(instance.getId())).verifyComplete()).assertNext(( event) -> assertThat(event).isInstanceOf(.class)).thenCancel().verify();
        StepVerifier.create(this.repository.find(instance.getId())).assertNext(( app) -> assertThat(app.getInfo()).isEqualTo(Info.empty())).verifyComplete();
    }

    @Test
    public void should_retry() {
        // given
        Registration registration = Registration.create("foo", this.wireMock.url("/health")).build();
        Instance instance = Instance.create(InstanceId.of("onl")).register(registration).withEndpoints(Endpoints.single("info", this.wireMock.url("/info"))).withStatusInfo(StatusInfo.ofUp());
        StepVerifier.create(this.repository.save(instance)).expectNextCount(1).verifyComplete();
        this.wireMock.stubFor(get("/info").inScenario("retry").whenScenarioStateIs(STARTED).willReturn(aResponse().withFixedDelay(5000)).willSetStateTo("recovered"));
        String body = "{ \"foo\": \"bar\" }";
        this.wireMock.stubFor(get("/info").inScenario("retry").whenScenarioStateIs("recovered").willReturn(okJson(body).withHeader("Content-Length", Integer.toString(body.length()))));
        // when
        // then
        StepVerifier.create(this.eventStore).expectSubscription().then(() -> StepVerifier.create(this.updater.updateInfo(instance.getId())).verifyComplete()).assertNext(( event) -> assertThat(event).isInstanceOf(.class)).thenCancel().verify();
        StepVerifier.create(this.repository.find(instance.getId())).assertNext(( app) -> assertThat(app.getInfo()).isEqualTo(Info.from(singletonMap("foo", "bar")))).verifyComplete();
    }
}

