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
package org.springframework.session.jdbc;


import FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME;
import JdbcOperationsSessionRepository.JdbcSession;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Tests for {@link JdbcOperationsSessionRepository}.
 *
 * @author Vedran Pavic
 * @author Craig Andrews
 * @since 1.2.0
 */
public class JdbcOperationsSessionRepositoryTests {
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";

    private JdbcOperations jdbcOperations = Mockito.mock(JdbcOperations.class);

    private PlatformTransactionManager transactionManager = Mockito.mock(PlatformTransactionManager.class);

    private JdbcOperationsSessionRepository repository;

    @Test
    public void constructorNullJdbcOperations() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new JdbcOperationsSessionRepository(null, this.transactionManager)).withMessage("JdbcOperations must not be null");
    }

    @Test
    public void constructorNullTransactionManager() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new JdbcOperationsSessionRepository(this.jdbcOperations, null)).withMessage("TransactionManager must not be null");
    }

    @Test
    public void setTableNameNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setTableName(null)).withMessage("Table name must not be empty");
    }

    @Test
    public void setTableNameEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setTableName(" ")).withMessage("Table name must not be empty");
    }

    @Test
    public void setCreateSessionQueryNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setCreateSessionQuery(null)).withMessage("Query must not be empty");
    }

    @Test
    public void setCreateSessionQueryEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setCreateSessionQuery(" ")).withMessage("Query must not be empty");
    }

    @Test
    public void setCreateSessionAttributeQueryNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setCreateSessionAttributeQuery(null)).withMessage("Query must not be empty");
    }

    @Test
    public void setCreateSessionAttributeQueryEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setCreateSessionAttributeQuery(" ")).withMessage("Query must not be empty");
    }

    @Test
    public void setGetSessionQueryNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setGetSessionQuery(null)).withMessage("Query must not be empty");
    }

    @Test
    public void setGetSessionQueryEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setGetSessionQuery(" ")).withMessage("Query must not be empty");
    }

    @Test
    public void setUpdateSessionQueryNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setUpdateSessionQuery(null)).withMessage("Query must not be empty");
    }

    @Test
    public void setUpdateSessionQueryEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setUpdateSessionQuery(" ")).withMessage("Query must not be empty");
    }

    @Test
    public void setUpdateSessionAttributeQueryNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setUpdateSessionAttributeQuery(null)).withMessage("Query must not be empty");
    }

    @Test
    public void setUpdateSessionAttributeQueryEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setUpdateSessionAttributeQuery(" ")).withMessage("Query must not be empty");
    }

    @Test
    public void setDeleteSessionAttributeQueryNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setDeleteSessionAttributeQuery(null)).withMessage("Query must not be empty");
    }

    @Test
    public void setDeleteSessionAttributeQueryEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setDeleteSessionAttributeQuery(" ")).withMessage("Query must not be empty");
    }

    @Test
    public void setDeleteSessionQueryNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setDeleteSessionQuery(null)).withMessage("Query must not be empty");
    }

    @Test
    public void setDeleteSessionQueryEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setDeleteSessionQuery(" ")).withMessage("Query must not be empty");
    }

    @Test
    public void setListSessionsByPrincipalNameQueryNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setListSessionsByPrincipalNameQuery(null)).withMessage("Query must not be empty");
    }

    @Test
    public void setListSessionsByPrincipalNameQueryEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setListSessionsByPrincipalNameQuery(" ")).withMessage("Query must not be empty");
    }

    @Test
    public void setDeleteSessionsByLastAccessTimeQueryNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setDeleteSessionsByExpiryTimeQuery(null)).withMessage("Query must not be empty");
    }

    @Test
    public void setDeleteSessionsByLastAccessTimeQueryEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setDeleteSessionsByExpiryTimeQuery(" ")).withMessage("Query must not be empty");
    }

    @Test
    public void setLobHandlerNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setLobHandler(null)).withMessage("LobHandler must not be null");
    }

    @Test
    public void setConversionServiceNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setConversionService(null)).withMessage("conversionService must not be null");
    }

    @Test
    public void createSessionDefaultMaxInactiveInterval() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.createSession();
        assertThat(session.isNew()).isTrue();
        assertThat(session.getMaxInactiveInterval()).isEqualTo(new MapSession().getMaxInactiveInterval());
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void createSessionCustomMaxInactiveInterval() {
        int interval = 1;
        this.repository.setDefaultMaxInactiveInterval(interval);
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.createSession();
        assertThat(session.isNew()).isTrue();
        assertThat(session.getMaxInactiveInterval()).isEqualTo(Duration.ofSeconds(interval));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void saveNewWithoutAttributes() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.createSession();
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("INSERT"), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void saveNewWithSingleAttribute() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.createSession();
        session.setAttribute("testName", "testValue");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("INSERT INTO SPRING_SESSION("), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("INSERT INTO SPRING_SESSION_ATTRIBUTES("), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void saveNewWithMultipleAttributes() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.createSession();
        session.setAttribute("testName1", "testValue1");
        session.setAttribute("testName2", "testValue2");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("INSERT INTO SPRING_SESSION("), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).batchUpdate(ArgumentMatchers.startsWith("INSERT INTO SPRING_SESSION_ATTRIBUTES("), ArgumentMatchers.isA(BatchPreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void saveUpdatedAddSingleAttribute() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.setAttribute("testName", "testValue");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("INSERT INTO SPRING_SESSION_ATTRIBUTES("), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void saveUpdatedAddMultipleAttributes() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.setAttribute("testName1", "testValue1");
        session.setAttribute("testName2", "testValue2");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).batchUpdate(ArgumentMatchers.startsWith("INSERT INTO SPRING_SESSION_ATTRIBUTES("), ArgumentMatchers.isA(BatchPreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void saveUpdatedModifySingleAttribute() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.setAttribute("testName", "testValue");
        session.clearChangeFlags();
        session.setAttribute("testName", "testValue");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("UPDATE SPRING_SESSION_ATTRIBUTES SET"), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void saveUpdatedModifyMultipleAttributes() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.setAttribute("testName1", "testValue1");
        session.setAttribute("testName2", "testValue2");
        session.clearChangeFlags();
        session.setAttribute("testName1", "testValue1");
        session.setAttribute("testName2", "testValue2");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).batchUpdate(ArgumentMatchers.startsWith("UPDATE SPRING_SESSION_ATTRIBUTES SET"), ArgumentMatchers.isA(BatchPreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void saveUpdatedRemoveSingleAttribute() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.setAttribute("testName", "testValue");
        session.clearChangeFlags();
        session.removeAttribute("testName");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("DELETE FROM SPRING_SESSION_ATTRIBUTES WHERE"), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void saveUpdatedRemoveNonExistingAttribute() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.removeAttribute("testName");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void saveUpdatedRemoveMultipleAttributes() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.setAttribute("testName1", "testValue1");
        session.setAttribute("testName2", "testValue2");
        session.clearChangeFlags();
        session.removeAttribute("testName1");
        session.removeAttribute("testName2");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).batchUpdate(ArgumentMatchers.startsWith("DELETE FROM SPRING_SESSION_ATTRIBUTES WHERE"), ArgumentMatchers.isA(BatchPreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    // gh-1070
    @Test
    public void saveUpdatedAddAndModifyAttribute() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.setAttribute("testName", "testValue1");
        session.setAttribute("testName", "testValue2");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations).update(ArgumentMatchers.startsWith("INSERT INTO SPRING_SESSION_ATTRIBUTES("), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    // gh-1070
    @Test
    public void saveUpdatedAddAndRemoveAttribute() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.setAttribute("testName", "testValue");
        session.removeAttribute("testName");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    // gh-1070
    @Test
    public void saveUpdatedModifyAndRemoveAttribute() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.setAttribute("testName", "testValue1");
        session.clearChangeFlags();
        session.setAttribute("testName", "testValue2");
        session.removeAttribute("testName");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations).update(ArgumentMatchers.startsWith("DELETE FROM SPRING_SESSION_ATTRIBUTES WHERE"), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    // gh-1070
    @Test
    public void saveUpdatedRemoveAndAddAttribute() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.setAttribute("testName", "testValue1");
        session.clearChangeFlags();
        session.removeAttribute("testName");
        session.setAttribute("testName", "testValue2");
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations).update(ArgumentMatchers.startsWith("UPDATE SPRING_SESSION_ATTRIBUTES SET"), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void saveUpdatedLastAccessedTime() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.setLastAccessedTime(Instant.now());
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("UPDATE SPRING_SESSION SET"), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    public void saveUnchanged() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        this.repository.save(session);
        assertThat(session.isNew()).isFalse();
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getSessionNotFound() {
        String sessionId = "testSessionId";
        BDDMockito.given(this.jdbcOperations.query(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(PreparedStatementSetter.class), ArgumentMatchers.isA(ResultSetExtractor.class))).willReturn(Collections.emptyList());
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.findById(sessionId);
        assertThat(session).isNull();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).query(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(PreparedStatementSetter.class), ArgumentMatchers.isA(ResultSetExtractor.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getSessionExpired() {
        Session expired = this.repository.new JdbcSession();
        expired.setLastAccessedTime(Instant.now().minusSeconds(((MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS) + 1)));
        BDDMockito.given(this.jdbcOperations.query(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(PreparedStatementSetter.class), ArgumentMatchers.isA(ResultSetExtractor.class))).willReturn(Collections.singletonList(expired));
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.findById(expired.getId());
        assertThat(session).isNull();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).query(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(PreparedStatementSetter.class), ArgumentMatchers.isA(ResultSetExtractor.class));
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("DELETE"), ArgumentMatchers.eq(expired.getId()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getSessionFound() {
        Session saved = this.repository.new JdbcSession("primaryKey", new MapSession());
        saved.setAttribute("savedName", "savedValue");
        BDDMockito.given(this.jdbcOperations.query(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(PreparedStatementSetter.class), ArgumentMatchers.isA(ResultSetExtractor.class))).willReturn(Collections.singletonList(saved));
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.findById(saved.getId());
        assertThat(session.getId()).isEqualTo(saved.getId());
        assertThat(session.isNew()).isFalse();
        assertThat(session.<String>getAttribute("savedName")).isEqualTo("savedValue");
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).query(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(PreparedStatementSetter.class), ArgumentMatchers.isA(ResultSetExtractor.class));
    }

    @Test
    public void delete() {
        String sessionId = "testSessionId";
        this.repository.deleteById(sessionId);
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("DELETE"), ArgumentMatchers.eq(sessionId));
    }

    @Test
    public void findByIndexNameAndIndexValueUnknownIndexName() {
        String indexValue = "testIndexValue";
        Map<String, JdbcOperationsSessionRepository.JdbcSession> sessions = this.repository.findByIndexNameAndIndexValue("testIndexName", indexValue);
        assertThat(sessions).isEmpty();
        Mockito.verifyZeroInteractions(this.jdbcOperations);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findByIndexNameAndIndexValuePrincipalIndexNameNotFound() {
        String principal = "username";
        BDDMockito.given(this.jdbcOperations.query(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(PreparedStatementSetter.class), ArgumentMatchers.isA(ResultSetExtractor.class))).willReturn(Collections.emptyList());
        Map<String, JdbcOperationsSessionRepository.JdbcSession> sessions = this.repository.findByIndexNameAndIndexValue(PRINCIPAL_NAME_INDEX_NAME, principal);
        assertThat(sessions).isEmpty();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).query(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(PreparedStatementSetter.class), ArgumentMatchers.isA(ResultSetExtractor.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findByIndexNameAndIndexValuePrincipalIndexNameFound() {
        String principal = "username";
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(principal, "notused", AuthorityUtils.createAuthorityList("ROLE_USER"));
        List<Session> saved = new ArrayList<>(2);
        Session saved1 = this.repository.new JdbcSession();
        saved1.setAttribute(JdbcOperationsSessionRepositoryTests.SPRING_SECURITY_CONTEXT, authentication);
        saved.add(saved1);
        Session saved2 = this.repository.new JdbcSession();
        saved2.setAttribute(JdbcOperationsSessionRepositoryTests.SPRING_SECURITY_CONTEXT, authentication);
        saved.add(saved2);
        BDDMockito.given(this.jdbcOperations.query(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(PreparedStatementSetter.class), ArgumentMatchers.isA(ResultSetExtractor.class))).willReturn(saved);
        Map<String, JdbcOperationsSessionRepository.JdbcSession> sessions = this.repository.findByIndexNameAndIndexValue(PRINCIPAL_NAME_INDEX_NAME, principal);
        assertThat(sessions).hasSize(2);
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).query(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(PreparedStatementSetter.class), ArgumentMatchers.isA(ResultSetExtractor.class));
    }

    @Test
    public void cleanupExpiredSessions() {
        this.repository.cleanUpExpiredSessions();
        assertPropagationRequiresNew();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("DELETE"), ArgumentMatchers.anyLong());
    }

    // gh-1120
    @Test
    public void getAttributeNamesAndRemove() {
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.createSession();
        session.setAttribute("attribute1", "value1");
        session.setAttribute("attribute2", "value2");
        for (String attributeName : session.getAttributeNames()) {
            session.removeAttribute(attributeName);
        }
        assertThat(session.getAttributeNames()).isEmpty();
    }

    @Test
    public void saveNewWithoutTransaction() {
        this.repository = new JdbcOperationsSessionRepository(this.jdbcOperations);
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.createSession();
        this.repository.save(session);
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("INSERT INTO SPRING_SESSION"), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
        Mockito.verifyZeroInteractions(this.transactionManager);
    }

    @Test
    public void saveUpdatedWithoutTransaction() {
        this.repository = new JdbcOperationsSessionRepository(this.jdbcOperations);
        JdbcOperationsSessionRepository.JdbcSession session = this.repository.new JdbcSession("primaryKey", new MapSession());
        session.setLastAccessedTime(Instant.now());
        this.repository.save(session);
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.startsWith("UPDATE SPRING_SESSION"), ArgumentMatchers.isA(PreparedStatementSetter.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
        Mockito.verifyZeroInteractions(this.transactionManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findByIdWithoutTransaction() {
        BDDMockito.given(this.jdbcOperations.query(ArgumentMatchers.anyString(), ArgumentMatchers.any(PreparedStatementSetter.class), ArgumentMatchers.any(ResultSetExtractor.class))).willReturn(Collections.emptyList());
        this.repository = new JdbcOperationsSessionRepository(this.jdbcOperations);
        this.repository.findById("testSessionId");
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).query(ArgumentMatchers.endsWith("WHERE S.SESSION_ID = ?"), ArgumentMatchers.isA(PreparedStatementSetter.class), ArgumentMatchers.isA(ResultSetExtractor.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
        Mockito.verifyZeroInteractions(this.transactionManager);
    }

    @Test
    public void deleteByIdWithoutTransaction() {
        this.repository = new JdbcOperationsSessionRepository(this.jdbcOperations);
        this.repository.deleteById("testSessionId");
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.eq("DELETE FROM SPRING_SESSION WHERE SESSION_ID = ?"), ArgumentMatchers.anyString());
        Mockito.verifyZeroInteractions(this.jdbcOperations);
        Mockito.verifyZeroInteractions(this.transactionManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findByIndexNameAndIndexValueWithoutTransaction() {
        BDDMockito.given(this.jdbcOperations.query(ArgumentMatchers.anyString(), ArgumentMatchers.any(PreparedStatementSetter.class), ArgumentMatchers.any(ResultSetExtractor.class))).willReturn(Collections.emptyList());
        this.repository = new JdbcOperationsSessionRepository(this.jdbcOperations);
        this.repository.findByIndexNameAndIndexValue(PRINCIPAL_NAME_INDEX_NAME, "testIndexValue");
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).query(ArgumentMatchers.endsWith("WHERE S.PRINCIPAL_NAME = ?"), ArgumentMatchers.isA(PreparedStatementSetter.class), ArgumentMatchers.isA(ResultSetExtractor.class));
        Mockito.verifyZeroInteractions(this.jdbcOperations);
        Mockito.verifyZeroInteractions(this.transactionManager);
    }

    @Test
    public void cleanUpExpiredSessionsWithoutTransaction() {
        this.repository = new JdbcOperationsSessionRepository(this.jdbcOperations);
        this.repository.cleanUpExpiredSessions();
        Mockito.verify(this.jdbcOperations, Mockito.times(1)).update(ArgumentMatchers.eq("DELETE FROM SPRING_SESSION WHERE EXPIRY_TIME < ?"), ArgumentMatchers.anyLong());
        Mockito.verifyZeroInteractions(this.jdbcOperations);
        Mockito.verifyZeroInteractions(this.transactionManager);
    }
}

