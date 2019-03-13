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
import java.util.List;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.session.SessionsEndpoint.SessionDescriptor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.MapSession;
import org.springframework.session.Session;


/**
 * Tests for {@link SessionsEndpoint}.
 *
 * @author Vedran Pavic
 */
public class SessionsEndpointTests {
    private static final Session session = new MapSession();

    @SuppressWarnings("unchecked")
    private final FindByIndexNameSessionRepository<Session> repository = Mockito.mock(FindByIndexNameSessionRepository.class);

    private final SessionsEndpoint endpoint = new SessionsEndpoint(this.repository);

    @Test
    public void sessionsForUsername() {
        BDDMockito.given(this.repository.findByPrincipalName("user")).willReturn(Collections.singletonMap(SessionsEndpointTests.session.getId(), SessionsEndpointTests.session));
        List<SessionDescriptor> result = this.endpoint.sessionsForUsername("user").getSessions();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(SessionsEndpointTests.session.getId());
        assertThat(result.get(0).getAttributeNames()).isEqualTo(SessionsEndpointTests.session.getAttributeNames());
        assertThat(result.get(0).getCreationTime()).isEqualTo(SessionsEndpointTests.session.getCreationTime());
        assertThat(result.get(0).getLastAccessedTime()).isEqualTo(SessionsEndpointTests.session.getLastAccessedTime());
        assertThat(result.get(0).getMaxInactiveInterval()).isEqualTo(SessionsEndpointTests.session.getMaxInactiveInterval().getSeconds());
        assertThat(result.get(0).isExpired()).isEqualTo(SessionsEndpointTests.session.isExpired());
    }

    @Test
    public void getSession() {
        BDDMockito.given(this.repository.findById(SessionsEndpointTests.session.getId())).willReturn(SessionsEndpointTests.session);
        SessionDescriptor result = this.endpoint.getSession(SessionsEndpointTests.session.getId());
        assertThat(result.getId()).isEqualTo(SessionsEndpointTests.session.getId());
        assertThat(result.getAttributeNames()).isEqualTo(SessionsEndpointTests.session.getAttributeNames());
        assertThat(result.getCreationTime()).isEqualTo(SessionsEndpointTests.session.getCreationTime());
        assertThat(result.getLastAccessedTime()).isEqualTo(SessionsEndpointTests.session.getLastAccessedTime());
        assertThat(result.getMaxInactiveInterval()).isEqualTo(SessionsEndpointTests.session.getMaxInactiveInterval().getSeconds());
        assertThat(result.isExpired()).isEqualTo(SessionsEndpointTests.session.isExpired());
    }

    @Test
    public void getSessionWithIdNotFound() {
        BDDMockito.given(this.repository.findById("not-found")).willReturn(null);
        assertThat(this.endpoint.getSession("not-found")).isNull();
    }

    @Test
    public void deleteSession() {
        this.endpoint.deleteSession(SessionsEndpointTests.session.getId());
        Mockito.verify(this.repository).deleteById(SessionsEndpointTests.session.getId());
    }
}

