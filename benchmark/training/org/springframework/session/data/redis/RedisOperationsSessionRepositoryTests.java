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
package org.springframework.session.data.redis;


import FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME;
import MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS;
import RedisFlushMode.IMMEDIATE;
import RedisOperationsSessionRepository.CREATION_TIME_ATTR;
import RedisOperationsSessionRepository.LAST_ACCESSED_ATTR;
import RedisOperationsSessionRepository.MAX_INACTIVE_ATTR;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisOperationsSessionRepository.PrincipalNameResolver;
import org.springframework.session.data.redis.RedisOperationsSessionRepository.RedisSession;
import org.springframework.session.events.AbstractSessionEvent;

import static RedisOperationsSessionRepository.PRINCIPAL_NAME_RESOLVER;


@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RedisOperationsSessionRepositoryTests {
    static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";

    @Mock
    RedisConnectionFactory factory;

    @Mock
    RedisConnection connection;

    @Mock
    RedisOperations<Object, Object> redisOperations;

    @Mock
    BoundValueOperations<Object, Object> boundValueOperations;

    @Mock
    BoundHashOperations<Object, Object, Object> boundHashOperations;

    @Mock
    BoundSetOperations<Object, Object> boundSetOperations;

    @Mock
    ApplicationEventPublisher publisher;

    @Mock
    RedisSerializer<Object> defaultSerializer;

    @Captor
    ArgumentCaptor<AbstractSessionEvent> event;

    @Captor
    ArgumentCaptor<Map<String, Object>> delta;

    private MapSession cached;

    private RedisOperationsSessionRepository redisRepository;

    @Test
    public void setApplicationEventPublisherNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.redisRepository.setApplicationEventPublisher(null)).withMessage("applicationEventPublisher cannot be null");
    }

    @Test
    public void changeSessionIdWhenNotSaved() {
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps(ArgumentMatchers.anyString())).willReturn(this.boundValueOperations);
        RedisSession createSession = this.redisRepository.createSession();
        String originalId = createSession.getId();
        String changeSessionId = createSession.changeSessionId();
        this.redisRepository.save(createSession);
        Mockito.verify(this.redisOperations, Mockito.never()).rename(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        assertThat(originalId).isNotEqualTo(changeSessionId);
        assertThat(createSession.getId()).isEqualTo(createSession.getId());
    }

    @Test
    public void changeSessionIdWhenSaved() {
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps(ArgumentMatchers.anyString())).willReturn(this.boundValueOperations);
        RedisSession session = this.redisRepository.new RedisSession(this.cached);
        session.setLastAccessedTime(session.getLastAccessedTime());
        String originalId = session.getId();
        String changeSessionId = session.changeSessionId();
        this.redisRepository.save(session);
        Mockito.verify(this.redisOperations, Mockito.times(2)).rename(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        assertThat(originalId).isNotEqualTo(changeSessionId);
        assertThat(session.getId()).isEqualTo(session.getId());
    }

    @Test
    public void createSessionDefaultMaxInactiveInterval() throws Exception {
        Session session = this.redisRepository.createSession();
        assertThat(session.getMaxInactiveInterval()).isEqualTo(new MapSession().getMaxInactiveInterval());
    }

    @Test
    public void createSessionCustomMaxInactiveInterval() throws Exception {
        int interval = 1;
        this.redisRepository.setDefaultMaxInactiveInterval(interval);
        Session session = this.redisRepository.createSession();
        assertThat(session.getMaxInactiveInterval()).isEqualTo(Duration.ofSeconds(interval));
    }

    @Test
    public void saveNewSession() {
        RedisSession session = this.redisRepository.createSession();
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps(ArgumentMatchers.anyString())).willReturn(this.boundValueOperations);
        this.redisRepository.save(session);
        Map<String, Object> delta = getDelta();
        assertThat(delta.size()).isEqualTo(3);
        Object creationTime = delta.get(CREATION_TIME_ATTR);
        assertThat(creationTime).isEqualTo(session.getCreationTime().toEpochMilli());
        assertThat(delta.get(MAX_INACTIVE_ATTR)).isEqualTo(((int) (Duration.ofSeconds(DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS).getSeconds())));
        assertThat(delta.get(LAST_ACCESSED_ATTR)).isEqualTo(session.getCreationTime().toEpochMilli());
    }

    // gh-467
    @Test
    public void saveSessionNothingChanged() {
        RedisSession session = this.redisRepository.new RedisSession(this.cached);
        this.redisRepository.save(session);
        Mockito.verifyZeroInteractions(this.redisOperations);
    }

    @Test
    public void saveJavadocSummary() {
        RedisSession session = this.redisRepository.createSession();
        String sessionKey = "spring:session:sessions:" + (session.getId());
        String backgroundExpireKey = "spring:session:expirations:" + (RedisSessionExpirationPolicy.roundUpToNextMinute(RedisSessionExpirationPolicy.expiresInMillis(session)));
        String destroyedTriggerKey = "spring:session:sessions:expires:" + (session.getId());
        BDDMockito.given(this.redisOperations.boundHashOps(sessionKey)).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(backgroundExpireKey)).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps(destroyedTriggerKey)).willReturn(this.boundValueOperations);
        this.redisRepository.save(session);
        // the actual data in the session expires 5 minutes after expiration so the data
        // can be accessed in expiration events
        // if the session is retrieved and expired it will not be returned since
        // findById checks if it is expired
        long fiveMinutesAfterExpires = session.getMaxInactiveInterval().plusMinutes(5).getSeconds();
        Mockito.verify(this.boundHashOperations).expire(fiveMinutesAfterExpires, TimeUnit.SECONDS);
        Mockito.verify(this.boundSetOperations).expire(fiveMinutesAfterExpires, TimeUnit.SECONDS);
        Mockito.verify(this.boundSetOperations).add(("expires:" + (session.getId())));
        Mockito.verify(this.boundValueOperations).expire(1800L, TimeUnit.SECONDS);
        Mockito.verify(this.boundValueOperations).append("");
    }

    @Test
    public void saveJavadoc() {
        RedisSession session = this.redisRepository.new RedisSession(this.cached);
        session.setLastAccessedTime(session.getLastAccessedTime());
        BDDMockito.given(this.redisOperations.boundHashOps("spring:session:sessions:session-id")).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps("spring:session:expirations:1404361860000")).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps("spring:session:sessions:expires:session-id")).willReturn(this.boundValueOperations);
        this.redisRepository.save(session);
        // the actual data in the session expires 5 minutes after expiration so the data
        // can be accessed in expiration events
        // if the session is retrieved and expired it will not be returned since
        // findById checks if it is expired
        Mockito.verify(this.boundHashOperations).expire(session.getMaxInactiveInterval().plusMinutes(5).getSeconds(), TimeUnit.SECONDS);
    }

    @Test
    public void saveLastAccessChanged() {
        RedisSession session = this.redisRepository.new RedisSession(new MapSession(this.cached));
        session.setLastAccessedTime(Instant.ofEpochMilli(12345678L));
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps(ArgumentMatchers.anyString())).willReturn(this.boundValueOperations);
        this.redisRepository.save(session);
        assertThat(getDelta()).isEqualTo(map(LAST_ACCESSED_ATTR, session.getLastAccessedTime().toEpochMilli()));
    }

    @Test
    public void saveSetAttribute() {
        String attrName = "attrName";
        RedisSession session = this.redisRepository.new RedisSession(new MapSession());
        session.setAttribute(attrName, "attrValue");
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps(ArgumentMatchers.anyString())).willReturn(this.boundValueOperations);
        this.redisRepository.save(session);
        assertThat(getDelta()).isEqualTo(map(RedisOperationsSessionRepository.getSessionAttrNameKey(attrName), session.getAttribute(attrName)));
    }

    @Test
    public void saveRemoveAttribute() {
        String attrName = "attrName";
        RedisSession session = this.redisRepository.new RedisSession(new MapSession());
        session.removeAttribute(attrName);
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps(ArgumentMatchers.anyString())).willReturn(this.boundValueOperations);
        this.redisRepository.save(session);
        assertThat(getDelta()).isEqualTo(map(RedisOperationsSessionRepository.getSessionAttrNameKey(attrName), null));
    }

    @Test
    public void saveExpired() {
        RedisSession session = this.redisRepository.new RedisSession(new MapSession());
        session.setMaxInactiveInterval(Duration.ZERO);
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        this.redisRepository.save(session);
        String id = session.getId();
        Mockito.verify(this.redisOperations, Mockito.atLeastOnce()).delete(getKey(("expires:" + id)));
        Mockito.verify(this.redisOperations, Mockito.never()).boundValueOps(getKey(("expires:" + id)));
    }

    @Test
    public void redisSessionGetAttributes() {
        String attrName = "attrName";
        RedisSession session = this.redisRepository.new RedisSession();
        assertThat(session.getAttributeNames()).isEmpty();
        session.setAttribute(attrName, "attrValue");
        assertThat(session.getAttributeNames()).containsOnly(attrName);
        session.removeAttribute(attrName);
        assertThat(session.getAttributeNames()).isEmpty();
    }

    @Test
    public void delete() {
        String attrName = "attrName";
        MapSession expected = new MapSession();
        expected.setLastAccessedTime(Instant.now().minusSeconds(60));
        expected.setAttribute(attrName, "attrValue");
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        Map map = map(RedisOperationsSessionRepository.getSessionAttrNameKey(attrName), expected.getAttribute(attrName), CREATION_TIME_ATTR, expected.getCreationTime().toEpochMilli(), MAX_INACTIVE_ATTR, ((int) (expected.getMaxInactiveInterval().getSeconds())), LAST_ACCESSED_ATTR, expected.getLastAccessedTime().toEpochMilli());
        BDDMockito.given(this.boundHashOperations.entries()).willReturn(map);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        String id = expected.getId();
        this.redisRepository.deleteById(id);
        assertThat(getDelta().get(MAX_INACTIVE_ATTR)).isEqualTo(0);
        Mockito.verify(this.redisOperations, Mockito.atLeastOnce()).delete(getKey(("expires:" + id)));
        Mockito.verify(this.redisOperations, Mockito.never()).boundValueOps(getKey(("expires:" + id)));
    }

    @Test
    public void deleteNullSession() {
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        String id = "abc";
        this.redisRepository.deleteById(id);
        Mockito.verify(this.redisOperations, Mockito.times(0)).delete(ArgumentMatchers.anyString());
        Mockito.verify(this.redisOperations, Mockito.times(0)).delete(ArgumentMatchers.anyString());
    }

    @Test
    public void getSessionNotFound() {
        String id = "abc";
        BDDMockito.given(this.redisOperations.boundHashOps(getKey(id))).willReturn(this.boundHashOperations);
        BDDMockito.given(this.boundHashOperations.entries()).willReturn(map());
        assertThat(this.redisRepository.findById(id)).isNull();
    }

    @Test
    public void getSessionFound() {
        String attribute1 = "attribute1";
        String attribute2 = "attribute2";
        MapSession expected = new MapSession();
        expected.setLastAccessedTime(Instant.now().minusSeconds(60));
        expected.setAttribute(attribute1, "test");
        expected.setAttribute(attribute2, null);
        BDDMockito.given(this.redisOperations.boundHashOps(getKey(expected.getId()))).willReturn(this.boundHashOperations);
        Map map = map(RedisOperationsSessionRepository.getSessionAttrNameKey(attribute1), expected.getAttribute(attribute1), RedisOperationsSessionRepository.getSessionAttrNameKey(attribute2), expected.getAttribute(attribute2), CREATION_TIME_ATTR, expected.getCreationTime().toEpochMilli(), MAX_INACTIVE_ATTR, ((int) (expected.getMaxInactiveInterval().getSeconds())), LAST_ACCESSED_ATTR, expected.getLastAccessedTime().toEpochMilli());
        BDDMockito.given(this.boundHashOperations.entries()).willReturn(map);
        RedisSession session = this.redisRepository.findById(expected.getId());
        assertThat(session.getId()).isEqualTo(expected.getId());
        assertThat(session.getAttributeNames()).isEqualTo(expected.getAttributeNames());
        assertThat(session.<String>getAttribute(attribute1)).isEqualTo(expected.getAttribute(attribute1));
        assertThat(session.<String>getAttribute(attribute2)).isEqualTo(expected.getAttribute(attribute2));
        assertThat(session.getCreationTime().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(expected.getCreationTime().truncatedTo(ChronoUnit.MILLIS));
        assertThat(session.getMaxInactiveInterval()).isEqualTo(expected.getMaxInactiveInterval());
        assertThat(session.getLastAccessedTime().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(expected.getLastAccessedTime().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    public void getSessionExpired() {
        String expiredId = "expired-id";
        BDDMockito.given(this.redisOperations.boundHashOps(getKey(expiredId))).willReturn(this.boundHashOperations);
        Map map = map(MAX_INACTIVE_ATTR, 1, LAST_ACCESSED_ATTR, Instant.now().minus(5, ChronoUnit.MINUTES).toEpochMilli());
        BDDMockito.given(this.boundHashOperations.entries()).willReturn(map);
        assertThat(this.redisRepository.findById(expiredId)).isNull();
    }

    @Test
    public void findByPrincipalNameExpired() {
        String expiredId = "expired-id";
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.boundSetOperations.members()).willReturn(Collections.singleton(expiredId));
        BDDMockito.given(this.redisOperations.boundHashOps(getKey(expiredId))).willReturn(this.boundHashOperations);
        Map map = map(MAX_INACTIVE_ATTR, 1, LAST_ACCESSED_ATTR, Instant.now().minus(5, ChronoUnit.MINUTES).toEpochMilli());
        BDDMockito.given(this.boundHashOperations.entries()).willReturn(map);
        assertThat(this.redisRepository.findByIndexNameAndIndexValue(PRINCIPAL_NAME_INDEX_NAME, "principal")).isEmpty();
    }

    @Test
    public void findByPrincipalName() {
        Instant lastAccessed = Instant.now().minusMillis(10);
        Instant createdTime = lastAccessed.minusMillis(10);
        Duration maxInactive = Duration.ofHours(1);
        String sessionId = "some-id";
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.boundSetOperations.members()).willReturn(Collections.singleton(sessionId));
        BDDMockito.given(this.redisOperations.boundHashOps(getKey(sessionId))).willReturn(this.boundHashOperations);
        Map map = map(CREATION_TIME_ATTR, createdTime.toEpochMilli(), MAX_INACTIVE_ATTR, ((int) (maxInactive.getSeconds())), LAST_ACCESSED_ATTR, lastAccessed.toEpochMilli());
        BDDMockito.given(this.boundHashOperations.entries()).willReturn(map);
        Map<String, RedisSession> sessionIdToSessions = this.redisRepository.findByIndexNameAndIndexValue(PRINCIPAL_NAME_INDEX_NAME, "principal");
        assertThat(sessionIdToSessions).hasSize(1);
        RedisSession session = sessionIdToSessions.get(sessionId);
        assertThat(session).isNotNull();
        assertThat(session.getId()).isEqualTo(sessionId);
        assertThat(session.getLastAccessedTime().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(lastAccessed.truncatedTo(ChronoUnit.MILLIS));
        assertThat(session.getMaxInactiveInterval()).isEqualTo(maxInactive);
        assertThat(session.getCreationTime().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(createdTime.truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    public void cleanupExpiredSessions() {
        String expiredId = "expired-id";
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        Set<Object> expiredIds = new HashSet<>(Arrays.asList("expired-key1", "expired-key2"));
        BDDMockito.given(this.boundSetOperations.members()).willReturn(expiredIds);
        this.redisRepository.cleanupExpiredSessions();
        for (Object id : expiredIds) {
            String expiredKey = "spring:session:sessions:" + id;
            // https://github.com/spring-projects/spring-session/issues/93
            Mockito.verify(this.redisOperations).hasKey(expiredKey);
        }
    }

    @Test
    public void onMessageCreated() {
        MapSession session = this.cached;
        byte[] pattern = "".getBytes(StandardCharsets.UTF_8);
        String channel = "spring:session:event:0:created:" + (session.getId());
        JdkSerializationRedisSerializer defaultSerailizer = new JdkSerializationRedisSerializer();
        this.redisRepository.setDefaultSerializer(defaultSerailizer);
        byte[] body = defaultSerailizer.serialize(new HashMap());
        DefaultMessage message = new DefaultMessage(channel.getBytes(StandardCharsets.UTF_8), body);
        this.redisRepository.setApplicationEventPublisher(this.publisher);
        this.redisRepository.onMessage(message, pattern);
        Mockito.verify(this.publisher).publishEvent(this.event.capture());
        assertThat(this.event.getValue().getSessionId()).isEqualTo(session.getId());
    }

    // gh-309
    @Test
    public void onMessageCreatedCustomSerializer() {
        MapSession session = this.cached;
        byte[] pattern = "".getBytes(StandardCharsets.UTF_8);
        byte[] body = new byte[0];
        String channel = "spring:session:event:0:created:" + (session.getId());
        BDDMockito.given(this.defaultSerializer.deserialize(body)).willReturn(new HashMap<String, Object>());
        DefaultMessage message = new DefaultMessage(channel.getBytes(StandardCharsets.UTF_8), body);
        this.redisRepository.setApplicationEventPublisher(this.publisher);
        this.redisRepository.onMessage(message, pattern);
        Mockito.verify(this.publisher).publishEvent(this.event.capture());
        assertThat(this.event.getValue().getSessionId()).isEqualTo(session.getId());
        Mockito.verify(this.defaultSerializer).deserialize(body);
    }

    @Test
    public void onMessageDeletedSessionFound() {
        String deletedId = "deleted-id";
        BDDMockito.given(this.redisOperations.boundHashOps(getKey(deletedId))).willReturn(this.boundHashOperations);
        Map map = map(MAX_INACTIVE_ATTR, 0, LAST_ACCESSED_ATTR, ((System.currentTimeMillis()) - (TimeUnit.MINUTES.toMillis(5))));
        BDDMockito.given(this.boundHashOperations.entries()).willReturn(map);
        String channel = "__keyevent@0__:del";
        String body = "spring:session:sessions:expires:" + deletedId;
        DefaultMessage message = new DefaultMessage(channel.getBytes(StandardCharsets.UTF_8), body.getBytes(StandardCharsets.UTF_8));
        this.redisRepository.setApplicationEventPublisher(this.publisher);
        this.redisRepository.onMessage(message, "".getBytes(StandardCharsets.UTF_8));
        Mockito.verify(this.redisOperations).boundHashOps(ArgumentMatchers.eq(getKey(deletedId)));
        Mockito.verify(this.boundHashOperations).entries();
        Mockito.verify(this.publisher).publishEvent(this.event.capture());
        assertThat(this.event.getValue().getSessionId()).isEqualTo(deletedId);
        Mockito.verifyZeroInteractions(this.defaultSerializer);
        Mockito.verifyZeroInteractions(this.publisher);
        Mockito.verifyZeroInteractions(this.redisOperations);
        Mockito.verifyZeroInteractions(this.boundHashOperations);
    }

    @Test
    public void onMessageDeletedSessionNotFound() {
        String deletedId = "deleted-id";
        BDDMockito.given(this.redisOperations.boundHashOps(getKey(deletedId))).willReturn(this.boundHashOperations);
        BDDMockito.given(this.boundHashOperations.entries()).willReturn(map());
        String channel = "__keyevent@0__:del";
        String body = "spring:session:sessions:expires:" + deletedId;
        DefaultMessage message = new DefaultMessage(channel.getBytes(StandardCharsets.UTF_8), body.getBytes(StandardCharsets.UTF_8));
        this.redisRepository.setApplicationEventPublisher(this.publisher);
        this.redisRepository.onMessage(message, "".getBytes(StandardCharsets.UTF_8));
        Mockito.verify(this.redisOperations).boundHashOps(ArgumentMatchers.eq(getKey(deletedId)));
        Mockito.verify(this.boundHashOperations).entries();
        Mockito.verifyZeroInteractions(this.defaultSerializer);
        Mockito.verifyZeroInteractions(this.publisher);
        Mockito.verifyZeroInteractions(this.redisOperations);
        Mockito.verifyZeroInteractions(this.boundHashOperations);
    }

    @Test
    public void onMessageExpiredSessionFound() {
        String expiredId = "expired-id";
        BDDMockito.given(this.redisOperations.boundHashOps(getKey(expiredId))).willReturn(this.boundHashOperations);
        Map map = map(MAX_INACTIVE_ATTR, 1, LAST_ACCESSED_ATTR, ((System.currentTimeMillis()) - (TimeUnit.MINUTES.toMillis(5))));
        BDDMockito.given(this.boundHashOperations.entries()).willReturn(map);
        String channel = "__keyevent@0__:expired";
        String body = "spring:session:sessions:expires:" + expiredId;
        DefaultMessage message = new DefaultMessage(channel.getBytes(StandardCharsets.UTF_8), body.getBytes(StandardCharsets.UTF_8));
        this.redisRepository.setApplicationEventPublisher(this.publisher);
        this.redisRepository.onMessage(message, "".getBytes(StandardCharsets.UTF_8));
        Mockito.verify(this.redisOperations).boundHashOps(ArgumentMatchers.eq(getKey(expiredId)));
        Mockito.verify(this.boundHashOperations).entries();
        Mockito.verify(this.publisher).publishEvent(this.event.capture());
        assertThat(this.event.getValue().getSessionId()).isEqualTo(expiredId);
        Mockito.verifyZeroInteractions(this.defaultSerializer);
        Mockito.verifyZeroInteractions(this.publisher);
        Mockito.verifyZeroInteractions(this.redisOperations);
        Mockito.verifyZeroInteractions(this.boundHashOperations);
    }

    @Test
    public void onMessageExpiredSessionNotFound() {
        String expiredId = "expired-id";
        BDDMockito.given(this.redisOperations.boundHashOps(getKey(expiredId))).willReturn(this.boundHashOperations);
        BDDMockito.given(this.boundHashOperations.entries()).willReturn(map());
        String channel = "__keyevent@0__:expired";
        String body = "spring:session:sessions:expires:" + expiredId;
        DefaultMessage message = new DefaultMessage(channel.getBytes(StandardCharsets.UTF_8), body.getBytes(StandardCharsets.UTF_8));
        this.redisRepository.setApplicationEventPublisher(this.publisher);
        this.redisRepository.onMessage(message, "".getBytes(StandardCharsets.UTF_8));
        Mockito.verify(this.redisOperations).boundHashOps(ArgumentMatchers.eq(getKey(expiredId)));
        Mockito.verify(this.boundHashOperations).entries();
        Mockito.verifyZeroInteractions(this.defaultSerializer);
        Mockito.verifyZeroInteractions(this.publisher);
        Mockito.verifyZeroInteractions(this.redisOperations);
        Mockito.verifyZeroInteractions(this.boundHashOperations);
    }

    @Test
    public void resolvePrincipalIndex() {
        PrincipalNameResolver resolver = PRINCIPAL_NAME_RESOLVER;
        String username = "username";
        RedisSession session = this.redisRepository.createSession();
        session.setAttribute(PRINCIPAL_NAME_INDEX_NAME, username);
        assertThat(resolver.resolvePrincipal(session)).isEqualTo(username);
    }

    @Test
    public void resolveIndexOnSecurityContext() {
        String principal = "resolveIndexOnSecurityContext";
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(principal, "notused", AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(authentication);
        PrincipalNameResolver resolver = PRINCIPAL_NAME_RESOLVER;
        RedisSession session = this.redisRepository.createSession();
        session.setAttribute(RedisOperationsSessionRepositoryTests.SPRING_SECURITY_CONTEXT_KEY, context);
        assertThat(resolver.resolvePrincipal(session)).isEqualTo(principal);
    }

    @Test
    public void flushModeOnSaveCreate() {
        this.redisRepository.createSession();
        Mockito.verifyZeroInteractions(this.boundHashOperations);
    }

    @Test
    public void flushModeOnSaveSetAttribute() {
        RedisSession session = this.redisRepository.createSession();
        session.setAttribute("something", "here");
        Mockito.verifyZeroInteractions(this.boundHashOperations);
    }

    @Test
    public void flushModeOnSaveRemoveAttribute() {
        RedisSession session = this.redisRepository.createSession();
        session.removeAttribute("remove");
        Mockito.verifyZeroInteractions(this.boundHashOperations);
    }

    @Test
    public void flushModeOnSaveSetLastAccessedTime() {
        RedisSession session = this.redisRepository.createSession();
        session.setLastAccessedTime(Instant.ofEpochMilli(1L));
        Mockito.verifyZeroInteractions(this.boundHashOperations);
    }

    @Test
    public void flushModeOnSaveSetMaxInactiveIntervalInSeconds() {
        RedisSession session = this.redisRepository.createSession();
        session.setMaxInactiveInterval(Duration.ofSeconds(1));
        Mockito.verifyZeroInteractions(this.boundHashOperations);
    }

    @Test
    public void flushModeImmediateCreate() {
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps(ArgumentMatchers.anyString())).willReturn(this.boundValueOperations);
        this.redisRepository.setRedisFlushMode(IMMEDIATE);
        RedisSession session = this.redisRepository.createSession();
        Map<String, Object> delta = getDelta();
        assertThat(delta.size()).isEqualTo(3);
        Object creationTime = delta.get(CREATION_TIME_ATTR);
        assertThat(creationTime).isEqualTo(session.getCreationTime().toEpochMilli());
        assertThat(delta.get(MAX_INACTIVE_ATTR)).isEqualTo(((int) (Duration.ofSeconds(DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS).getSeconds())));
        assertThat(delta.get(LAST_ACCESSED_ATTR)).isEqualTo(session.getCreationTime().toEpochMilli());
    }

    @Test
    public void flushModeImmediateSetAttribute() {
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps(ArgumentMatchers.anyString())).willReturn(this.boundValueOperations);
        this.redisRepository.setRedisFlushMode(IMMEDIATE);
        RedisSession session = this.redisRepository.createSession();
        String attrName = "someAttribute";
        session.setAttribute(attrName, "someValue");
        Map<String, Object> delta = getDelta(2);
        assertThat(delta.size()).isEqualTo(1);
        assertThat(delta).isEqualTo(map(RedisOperationsSessionRepository.getSessionAttrNameKey(attrName), session.getAttribute(attrName)));
    }

    @Test
    public void flushModeImmediateRemoveAttribute() {
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps(ArgumentMatchers.anyString())).willReturn(this.boundValueOperations);
        this.redisRepository.setRedisFlushMode(IMMEDIATE);
        RedisSession session = this.redisRepository.createSession();
        String attrName = "someAttribute";
        session.removeAttribute(attrName);
        Map<String, Object> delta = getDelta(2);
        assertThat(delta.size()).isEqualTo(1);
        assertThat(delta).isEqualTo(map(RedisOperationsSessionRepository.getSessionAttrNameKey(attrName), session.getAttribute(attrName)));
    }

    @Test
    public void flushModeSetMaxInactiveIntervalInSeconds() {
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps(ArgumentMatchers.anyString())).willReturn(this.boundValueOperations);
        this.redisRepository.setRedisFlushMode(IMMEDIATE);
        RedisSession session = this.redisRepository.createSession();
        Mockito.reset(this.boundHashOperations);
        session.setMaxInactiveInterval(Duration.ofSeconds(1));
        Mockito.verify(this.boundHashOperations).expire(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
    }

    @Test
    public void flushModeSetLastAccessedTime() {
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        BDDMockito.given(this.redisOperations.boundValueOps(ArgumentMatchers.anyString())).willReturn(this.boundValueOperations);
        this.redisRepository.setRedisFlushMode(IMMEDIATE);
        RedisSession session = this.redisRepository.createSession();
        session.setLastAccessedTime(Instant.now());
        Map<String, Object> delta = getDelta(2);
        assertThat(delta.size()).isEqualTo(1);
        assertThat(delta).isEqualTo(map(LAST_ACCESSED_ATTR, session.getLastAccessedTime().toEpochMilli()));
    }

    @Test
    public void setRedisFlushModeNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.redisRepository.setRedisFlushMode(null)).withMessage("redisFlushMode cannot be null");
    }

    @Test
    public void changeRedisNamespace() {
        String namespace = "foo:bar";
        this.redisRepository.setRedisKeyNamespace(namespace);
        RedisSession session = this.redisRepository.new RedisSession(new MapSession());
        session.setMaxInactiveInterval(Duration.ZERO);
        BDDMockito.given(this.redisOperations.boundHashOps(ArgumentMatchers.anyString())).willReturn(this.boundHashOperations);
        BDDMockito.given(this.redisOperations.boundSetOps(ArgumentMatchers.anyString())).willReturn(this.boundSetOperations);
        this.redisRepository.save(session);
        String id = session.getId();
        Mockito.verify(this.redisOperations, Mockito.atLeastOnce()).delete(((namespace + ":sessions:expires:") + id));
        Mockito.verify(this.redisOperations, Mockito.never()).boundValueOps(((namespace + ":sessions:expires:") + id));
    }

    @Test
    public void setRedisKeyNamespaceNullNamespace() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.redisRepository.setRedisKeyNamespace(null)).withMessage("namespace cannot be null or empty");
    }

    @Test
    public void setRedisKeyNamespaceEmptyNamespace() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.redisRepository.setRedisKeyNamespace(" ")).withMessage("namespace cannot be null or empty");
    }

    // gh-1120
    @Test
    public void getAttributeNamesAndRemove() {
        RedisSession session = this.redisRepository.new RedisSession(this.cached);
        session.setAttribute("attribute1", "value1");
        session.setAttribute("attribute2", "value2");
        for (String attributeName : session.getAttributeNames()) {
            session.removeAttribute(attributeName);
        }
        assertThat(session.getAttributeNames()).isEmpty();
    }

    @Test
    public void onMessageCreatedInOtherDatabase() {
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
        this.redisRepository.setApplicationEventPublisher(this.publisher);
        this.redisRepository.setDefaultSerializer(serializer);
        MapSession session = this.cached;
        String channel = "spring:session:event:created:1:" + (session.getId());
        byte[] body = serializer.serialize(new HashMap());
        DefaultMessage message = new DefaultMessage(channel.getBytes(StandardCharsets.UTF_8), body);
        this.redisRepository.onMessage(message, "".getBytes(StandardCharsets.UTF_8));
        assertThat(this.event.getAllValues()).isEmpty();
        Mockito.verifyZeroInteractions(this.publisher);
    }

    @Test
    public void onMessageDeletedInOtherDatabase() {
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
        this.redisRepository.setApplicationEventPublisher(this.publisher);
        this.redisRepository.setDefaultSerializer(serializer);
        MapSession session = this.cached;
        String channel = "__keyevent@1__:del";
        String body = "spring:session:sessions:expires:" + (session.getId());
        DefaultMessage message = new DefaultMessage(channel.getBytes(StandardCharsets.UTF_8), body.getBytes(StandardCharsets.UTF_8));
        this.redisRepository.onMessage(message, "".getBytes(StandardCharsets.UTF_8));
        assertThat(this.event.getAllValues()).isEmpty();
        Mockito.verifyZeroInteractions(this.publisher);
    }

    @Test
    public void onMessageExpiredInOtherDatabase() {
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
        this.redisRepository.setApplicationEventPublisher(this.publisher);
        this.redisRepository.setDefaultSerializer(serializer);
        MapSession session = this.cached;
        String channel = "__keyevent@1__:expired";
        String body = "spring:session:sessions:expires:" + (session.getId());
        DefaultMessage message = new DefaultMessage(channel.getBytes(StandardCharsets.UTF_8), body.getBytes(StandardCharsets.UTF_8));
        this.redisRepository.onMessage(message, "".getBytes(StandardCharsets.UTF_8));
        assertThat(this.event.getAllValues()).isEmpty();
        Mockito.verifyZeroInteractions(this.publisher);
    }
}

