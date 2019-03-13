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
package org.springframework.boot.actuate.autoconfigure.endpoint.web.documentation;


import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.session.SessionsEndpoint;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.test.context.TestPropertySource;


/**
 * Tests for generating documentation describing the {@link ShutdownEndpoint}.
 *
 * @author Andy Wilkinson
 */
@TestPropertySource(properties = "spring.jackson.serialization.write-dates-as-timestamps=false")
public class SessionsEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    private static final Session sessionOne = SessionsEndpointDocumentationTests.createSession(Instant.now().minusSeconds(((60 * 60) * 12)), Instant.now().minusSeconds(45));

    private static final Session sessionTwo = SessionsEndpointDocumentationTests.createSession("4db5efcc-99cb-4d05-a52c-b49acfbb7ea9", Instant.now().minusSeconds(((60 * 60) * 5)), Instant.now().minusSeconds(37));

    private static final Session sessionThree = SessionsEndpointDocumentationTests.createSession(Instant.now().minusSeconds(((60 * 60) * 2)), Instant.now().minusSeconds(12));

    private static final List<FieldDescriptor> sessionFields = Arrays.asList(fieldWithPath("id").description("ID of the session."), fieldWithPath("attributeNames").description("Names of the attributes stored in the session."), fieldWithPath("creationTime").description("Timestamp of when the session was created."), fieldWithPath("lastAccessedTime").description("Timestamp of when the session was last accessed."), fieldWithPath("maxInactiveInterval").description(("Maximum permitted period of inactivity, in seconds, " + "before the session will expire.")), fieldWithPath("expired").description("Whether the session has expired."));

    @MockBean
    private FindByIndexNameSessionRepository<Session> sessionRepository;

    @Test
    public void sessionsForUsername() throws Exception {
        Map<String, Session> sessions = new HashMap<>();
        sessions.put(SessionsEndpointDocumentationTests.sessionOne.getId(), SessionsEndpointDocumentationTests.sessionOne);
        sessions.put(SessionsEndpointDocumentationTests.sessionTwo.getId(), SessionsEndpointDocumentationTests.sessionTwo);
        sessions.put(SessionsEndpointDocumentationTests.sessionThree.getId(), SessionsEndpointDocumentationTests.sessionThree);
        BDDMockito.given(this.sessionRepository.findByPrincipalName("alice")).willReturn(sessions);
        this.mockMvc.perform(get("/actuator/sessions").param("username", "alice")).andExpect(status().isOk()).andDo(document("sessions/username", responseFields(fieldWithPath("sessions").description("Sessions for the given username.")).andWithPrefix("sessions.[].", SessionsEndpointDocumentationTests.sessionFields), requestParameters(parameterWithName("username").description("Name of the user."))));
    }

    @Test
    public void sessionWithId() throws Exception {
        Map<String, Session> sessions = new HashMap<>();
        sessions.put(SessionsEndpointDocumentationTests.sessionOne.getId(), SessionsEndpointDocumentationTests.sessionOne);
        sessions.put(SessionsEndpointDocumentationTests.sessionTwo.getId(), SessionsEndpointDocumentationTests.sessionTwo);
        sessions.put(SessionsEndpointDocumentationTests.sessionThree.getId(), SessionsEndpointDocumentationTests.sessionThree);
        BDDMockito.given(this.sessionRepository.findById(SessionsEndpointDocumentationTests.sessionTwo.getId())).willReturn(SessionsEndpointDocumentationTests.sessionTwo);
        this.mockMvc.perform(get("/actuator/sessions/{id}", SessionsEndpointDocumentationTests.sessionTwo.getId())).andExpect(status().isOk()).andDo(document("sessions/id", responseFields(SessionsEndpointDocumentationTests.sessionFields)));
    }

    @Test
    public void deleteASession() throws Exception {
        this.mockMvc.perform(delete("/actuator/sessions/{id}", SessionsEndpointDocumentationTests.sessionTwo.getId())).andExpect(status().isNoContent()).andDo(document("sessions/delete"));
        Mockito.verify(this.sessionRepository).deleteById(SessionsEndpointDocumentationTests.sessionTwo.getId());
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public SessionsEndpoint endpoint(FindByIndexNameSessionRepository<?> sessionRepository) {
            return new SessionsEndpoint(sessionRepository);
        }
    }
}

