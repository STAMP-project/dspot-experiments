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
package org.springframework.boot.actuate.session;


import java.util.Collections;
import net.minidev.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.endpoint.web.test.WebEndpointRunners;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * Integration tests for {@link SessionsEndpoint} exposed by Jersey, Spring MVC, and
 * WebFlux.
 *
 * @author Vedran Pavic
 */
@RunWith(WebEndpointRunners.class)
public class SessionsEndpointWebIntegrationTests {
    private static final Session session = new MapSession();

    @SuppressWarnings("unchecked")
    private static final FindByIndexNameSessionRepository<Session> repository = Mockito.mock(FindByIndexNameSessionRepository.class);

    private static WebTestClient client;

    @Test
    public void sessionsForUsernameWithoutUsernameParam() {
        SessionsEndpointWebIntegrationTests.client.get().uri(( builder) -> builder.path("/actuator/sessions").build()).exchange().expectStatus().isBadRequest();
    }

    @Test
    public void sessionsForUsernameNoResults() {
        BDDMockito.given(SessionsEndpointWebIntegrationTests.repository.findByPrincipalName("user")).willReturn(Collections.emptyMap());
        SessionsEndpointWebIntegrationTests.client.get().uri(( builder) -> builder.path("/actuator/sessions").queryParam("username", "user").build()).exchange().expectStatus().isOk().expectBody().jsonPath("sessions").isEmpty();
    }

    @Test
    public void sessionsForUsernameFound() {
        BDDMockito.given(SessionsEndpointWebIntegrationTests.repository.findByPrincipalName("user")).willReturn(Collections.singletonMap(SessionsEndpointWebIntegrationTests.session.getId(), SessionsEndpointWebIntegrationTests.session));
        SessionsEndpointWebIntegrationTests.client.get().uri(( builder) -> builder.path("/actuator/sessions").queryParam("username", "user").build()).exchange().expectStatus().isOk().expectBody().jsonPath("sessions.[*].id").isEqualTo(new JSONArray().appendElement(SessionsEndpointWebIntegrationTests.session.getId()));
    }

    @Test
    public void sessionForIdNotFound() {
        SessionsEndpointWebIntegrationTests.client.get().uri(( builder) -> builder.path("/actuator/sessions/session-id-not-found").build()).exchange().expectStatus().isNotFound();
    }

    @Configuration
    protected static class TestConfiguration {
        @Bean
        public SessionsEndpoint sessionsEndpoint() {
            return new SessionsEndpoint(SessionsEndpointWebIntegrationTests.repository);
        }
    }
}

