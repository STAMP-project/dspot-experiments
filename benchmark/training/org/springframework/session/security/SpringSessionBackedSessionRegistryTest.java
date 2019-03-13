/**
 * Copyright 2014-2018 the original author or authors.
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
package org.springframework.session.security;


import SpringSessionBackedSessionInformation.EXPIRED_ATTR;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.userdetails.User;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;


/**
 * Tests for {@link SpringSessionBackedSessionRegistry}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SpringSessionBackedSessionRegistryTest {
    private static final String SESSION_ID = "sessionId";

    private static final String SESSION_ID2 = "otherSessionId";

    private static final String USER_NAME = "userName";

    private static final User PRINCIPAL = new User(SpringSessionBackedSessionRegistryTest.USER_NAME, "password", Collections.emptyList());

    private static final Instant NOW = Instant.now();

    @Mock
    private FindByIndexNameSessionRepository<Session> sessionRepository;

    @InjectMocks
    private SpringSessionBackedSessionRegistry<Session> sessionRegistry;

    @Test
    public void sessionInformationForExistingSession() {
        Session session = createSession(SpringSessionBackedSessionRegistryTest.SESSION_ID, SpringSessionBackedSessionRegistryTest.USER_NAME, SpringSessionBackedSessionRegistryTest.NOW);
        Mockito.when(this.sessionRepository.findById(SpringSessionBackedSessionRegistryTest.SESSION_ID)).thenReturn(session);
        SessionInformation sessionInfo = this.sessionRegistry.getSessionInformation(SpringSessionBackedSessionRegistryTest.SESSION_ID);
        assertThat(sessionInfo.getSessionId()).isEqualTo(SpringSessionBackedSessionRegistryTest.SESSION_ID);
        assertThat(sessionInfo.getLastRequest().toInstant().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(SpringSessionBackedSessionRegistryTest.NOW.truncatedTo(ChronoUnit.MILLIS));
        assertThat(sessionInfo.getPrincipal()).isEqualTo(SpringSessionBackedSessionRegistryTest.USER_NAME);
        assertThat(sessionInfo.isExpired()).isFalse();
    }

    @Test
    public void sessionInformationForExpiredSession() {
        Session session = createSession(SpringSessionBackedSessionRegistryTest.SESSION_ID, SpringSessionBackedSessionRegistryTest.USER_NAME, SpringSessionBackedSessionRegistryTest.NOW);
        session.setAttribute(EXPIRED_ATTR, Boolean.TRUE);
        Mockito.when(this.sessionRepository.findById(SpringSessionBackedSessionRegistryTest.SESSION_ID)).thenReturn(session);
        SessionInformation sessionInfo = this.sessionRegistry.getSessionInformation(SpringSessionBackedSessionRegistryTest.SESSION_ID);
        assertThat(sessionInfo.getSessionId()).isEqualTo(SpringSessionBackedSessionRegistryTest.SESSION_ID);
        assertThat(sessionInfo.getLastRequest().toInstant().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(SpringSessionBackedSessionRegistryTest.NOW.truncatedTo(ChronoUnit.MILLIS));
        assertThat(sessionInfo.getPrincipal()).isEqualTo(SpringSessionBackedSessionRegistryTest.USER_NAME);
        assertThat(sessionInfo.isExpired()).isTrue();
    }

    @Test
    public void noSessionInformationForMissingSession() {
        assertThat(this.sessionRegistry.getSessionInformation("nonExistingSessionId")).isNull();
    }

    @Test
    public void getAllSessions() {
        setUpSessions();
        List<SessionInformation> allSessionInfos = this.sessionRegistry.getAllSessions(SpringSessionBackedSessionRegistryTest.PRINCIPAL, true);
        assertThat(allSessionInfos).extracting("sessionId").containsExactly(SpringSessionBackedSessionRegistryTest.SESSION_ID, SpringSessionBackedSessionRegistryTest.SESSION_ID2);
    }

    @Test
    public void getNonExpiredSessions() {
        setUpSessions();
        List<SessionInformation> nonExpiredSessionInfos = this.sessionRegistry.getAllSessions(SpringSessionBackedSessionRegistryTest.PRINCIPAL, false);
        assertThat(nonExpiredSessionInfos).extracting("sessionId").containsExactly(SpringSessionBackedSessionRegistryTest.SESSION_ID2);
    }

    @Test
    public void expireNow() {
        Session session = createSession(SpringSessionBackedSessionRegistryTest.SESSION_ID, SpringSessionBackedSessionRegistryTest.USER_NAME, SpringSessionBackedSessionRegistryTest.NOW);
        Mockito.when(this.sessionRepository.findById(SpringSessionBackedSessionRegistryTest.SESSION_ID)).thenReturn(session);
        SessionInformation sessionInfo = this.sessionRegistry.getSessionInformation(SpringSessionBackedSessionRegistryTest.SESSION_ID);
        assertThat(sessionInfo.isExpired()).isFalse();
        sessionInfo.expireNow();
        assertThat(sessionInfo.isExpired()).isTrue();
        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        Mockito.verify(this.sessionRepository).save(captor.capture());
        assertThat(captor.getValue().<Boolean>getAttribute(EXPIRED_ATTR)).isEqualTo(Boolean.TRUE);
    }
}

