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


import ReactiveRedisOperationsSessionRepository.CREATION_TIME_KEY;
import ReactiveRedisOperationsSessionRepository.LAST_ACCESSED_TIME_KEY;
import ReactiveRedisOperationsSessionRepository.MAX_INACTIVE_INTERVAL_KEY;
import RedisFlushMode.IMMEDIATE;
import RedisOperationsSessionRepository.LAST_ACCESSED_ATTR;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.session.MapSession;
import org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository.RedisSession;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static ReactiveRedisOperationsSessionRepository.ATTRIBUTE_PREFIX;


/**
 * Tests for {@link ReactiveRedisOperationsSessionRepository}.
 *
 * @author Vedran Pavic
 */
public class ReactiveRedisOperationsSessionRepositoryTests {
    @SuppressWarnings("unchecked")
    private ReactiveRedisOperations<String, Object> redisOperations = Mockito.mock(ReactiveRedisOperations.class);

    @SuppressWarnings("unchecked")
    private ReactiveHashOperations<String, Object, Object> hashOperations = Mockito.mock(ReactiveHashOperations.class);

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<Map<String, Object>> delta = ArgumentCaptor.forClass(Map.class);

    private ReactiveRedisOperationsSessionRepository repository;

    private MapSession cached;

    @Test
    public void constructorWithNullReactiveRedisOperations() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new ReactiveRedisOperationsSessionRepository(null)).withMessageContaining("sessionRedisOperations cannot be null");
    }

    @Test
    public void customRedisKeyNamespace() {
        this.repository.setRedisKeyNamespace("test");
        assertThat(ReflectionTestUtils.getField(this.repository, "namespace")).isEqualTo("test:");
    }

    @Test
    public void nullRedisKeyNamespace() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setRedisKeyNamespace(null)).withMessage("namespace cannot be null or empty");
    }

    @Test
    public void emptyRedisKeyNamespace() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setRedisKeyNamespace("")).withMessage("namespace cannot be null or empty");
    }

    @Test
    public void customMaxInactiveInterval() {
        this.repository.setDefaultMaxInactiveInterval(600);
        assertThat(ReflectionTestUtils.getField(this.repository, "defaultMaxInactiveInterval")).isEqualTo(600);
    }

    @Test
    public void customRedisFlushMode() {
        this.repository.setRedisFlushMode(IMMEDIATE);
        assertThat(ReflectionTestUtils.getField(this.repository, "redisFlushMode")).isEqualTo(IMMEDIATE);
    }

    @Test
    public void nullRedisFlushMode() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.repository.setRedisFlushMode(null)).withMessage("redisFlushMode cannot be null");
    }

    @Test
    public void createSessionDefaultMaxInactiveInterval() {
        StepVerifier.create(this.repository.createSession()).consumeNextWith(( session) -> assertThat(session.getMaxInactiveInterval()).isEqualTo(Duration.ofSeconds(MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS))).verifyComplete();
    }

    @Test
    public void createSessionCustomMaxInactiveInterval() {
        this.repository.setDefaultMaxInactiveInterval(600);
        StepVerifier.create(this.repository.createSession()).consumeNextWith(( session) -> assertThat(session.getMaxInactiveInterval()).isEqualTo(Duration.ofSeconds(600))).verifyComplete();
    }

    @Test
    public void saveNewSession() {
        BDDMockito.given(this.redisOperations.opsForHash()).willReturn(this.hashOperations);
        BDDMockito.given(this.hashOperations.putAll(ArgumentMatchers.anyString(), ArgumentMatchers.any())).willReturn(Mono.just(true));
        BDDMockito.given(this.redisOperations.expire(ArgumentMatchers.anyString(), ArgumentMatchers.any())).willReturn(Mono.just(true));
        StepVerifier.create(this.repository.createSession().doOnNext(this.repository::save)).consumeNextWith(( session) -> {
            verify(this.redisOperations).opsForHash();
            verify(this.hashOperations).putAll(anyString(), this.delta.capture());
            verify(this.redisOperations).expire(anyString(), any());
            verifyZeroInteractions(this.redisOperations);
            verifyZeroInteractions(this.hashOperations);
            Map<String, Object> delta = this.delta.getAllValues().get(0);
            assertThat(delta.size()).isEqualTo(3);
            assertThat(delta.get(ReactiveRedisOperationsSessionRepository.CREATION_TIME_KEY)).isEqualTo(session.getCreationTime().toEpochMilli());
            assertThat(delta.get(ReactiveRedisOperationsSessionRepository.MAX_INACTIVE_INTERVAL_KEY)).isEqualTo(((int) (Duration.ofSeconds(MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS).getSeconds())));
            assertThat(delta.get(ReactiveRedisOperationsSessionRepository.LAST_ACCESSED_TIME_KEY)).isEqualTo(session.getLastAccessedTime().toEpochMilli());
        }).verifyComplete();
    }

    @Test
    public void saveSessionNothingChanged() {
        BDDMockito.given(this.redisOperations.hasKey(ArgumentMatchers.anyString())).willReturn(Mono.just(true));
        BDDMockito.given(this.redisOperations.expire(ArgumentMatchers.anyString(), ArgumentMatchers.any())).willReturn(Mono.just(true));
        RedisSession session = this.repository.new RedisSession(new MapSession(this.cached));
        StepVerifier.create(this.repository.save(session)).verifyComplete();
        Mockito.verify(this.redisOperations).hasKey(ArgumentMatchers.anyString());
        Mockito.verifyZeroInteractions(this.redisOperations);
        Mockito.verifyZeroInteractions(this.hashOperations);
    }

    @Test
    public void saveLastAccessChanged() {
        BDDMockito.given(this.redisOperations.hasKey(ArgumentMatchers.anyString())).willReturn(Mono.just(true));
        BDDMockito.given(this.redisOperations.opsForHash()).willReturn(this.hashOperations);
        BDDMockito.given(this.hashOperations.putAll(ArgumentMatchers.anyString(), ArgumentMatchers.any())).willReturn(Mono.just(true));
        BDDMockito.given(this.redisOperations.expire(ArgumentMatchers.anyString(), ArgumentMatchers.any())).willReturn(Mono.just(true));
        RedisSession session = this.repository.new RedisSession(this.cached);
        session.setLastAccessedTime(Instant.ofEpochMilli(12345678L));
        Mono.just(session).subscribe(this.repository::save);
        Mockito.verify(this.redisOperations).hasKey(ArgumentMatchers.anyString());
        Mockito.verify(this.redisOperations).opsForHash();
        Mockito.verify(this.hashOperations).putAll(ArgumentMatchers.anyString(), this.delta.capture());
        Mockito.verify(this.redisOperations).expire(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(this.redisOperations);
        Mockito.verifyZeroInteractions(this.hashOperations);
        assertThat(this.delta.getAllValues().get(0)).isEqualTo(map(LAST_ACCESSED_ATTR, session.getLastAccessedTime().toEpochMilli()));
    }

    @Test
    public void saveSetAttribute() {
        BDDMockito.given(this.redisOperations.hasKey(ArgumentMatchers.anyString())).willReturn(Mono.just(true));
        BDDMockito.given(this.redisOperations.opsForHash()).willReturn(this.hashOperations);
        BDDMockito.given(this.hashOperations.putAll(ArgumentMatchers.anyString(), ArgumentMatchers.any())).willReturn(Mono.just(true));
        BDDMockito.given(this.redisOperations.expire(ArgumentMatchers.anyString(), ArgumentMatchers.any())).willReturn(Mono.just(true));
        String attrName = "attrName";
        RedisSession session = this.repository.new RedisSession(this.cached);
        session.setAttribute(attrName, "attrValue");
        Mono.just(session).subscribe(this.repository::save);
        Mockito.verify(this.redisOperations).hasKey(ArgumentMatchers.anyString());
        Mockito.verify(this.redisOperations).opsForHash();
        Mockito.verify(this.hashOperations).putAll(ArgumentMatchers.anyString(), this.delta.capture());
        Mockito.verify(this.redisOperations).expire(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(this.redisOperations);
        Mockito.verifyZeroInteractions(this.hashOperations);
        assertThat(this.delta.getAllValues().get(0)).isEqualTo(map(RedisOperationsSessionRepository.getSessionAttrNameKey(attrName), session.getAttribute(attrName)));
    }

    @Test
    public void saveRemoveAttribute() {
        BDDMockito.given(this.redisOperations.hasKey(ArgumentMatchers.anyString())).willReturn(Mono.just(true));
        BDDMockito.given(this.redisOperations.opsForHash()).willReturn(this.hashOperations);
        BDDMockito.given(this.hashOperations.putAll(ArgumentMatchers.anyString(), ArgumentMatchers.any())).willReturn(Mono.just(true));
        BDDMockito.given(this.redisOperations.expire(ArgumentMatchers.anyString(), ArgumentMatchers.any())).willReturn(Mono.just(true));
        String attrName = "attrName";
        RedisSession session = this.repository.new RedisSession(new MapSession());
        session.removeAttribute(attrName);
        Mono.just(session).subscribe(this.repository::save);
        Mockito.verify(this.redisOperations).hasKey(ArgumentMatchers.anyString());
        Mockito.verify(this.redisOperations).opsForHash();
        Mockito.verify(this.hashOperations).putAll(ArgumentMatchers.anyString(), this.delta.capture());
        Mockito.verify(this.redisOperations).expire(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(this.redisOperations);
        Mockito.verifyZeroInteractions(this.hashOperations);
        assertThat(this.delta.getAllValues().get(0)).isEqualTo(map(RedisOperationsSessionRepository.getSessionAttrNameKey(attrName), null));
    }

    @Test
    public void redisSessionGetAttributes() {
        String attrName = "attrName";
        RedisSession session = this.repository.new RedisSession(this.cached);
        assertThat(session.getAttributeNames()).isEmpty();
        session.setAttribute(attrName, "attrValue");
        assertThat(session.getAttributeNames()).containsOnly(attrName);
        session.removeAttribute(attrName);
        assertThat(session.getAttributeNames()).isEmpty();
    }

    @Test
    public void delete() {
        BDDMockito.given(this.redisOperations.delete(ArgumentMatchers.anyString())).willReturn(Mono.just(1L));
        StepVerifier.create(this.repository.deleteById("test")).verifyComplete();
        Mockito.verify(this.redisOperations).delete(ArgumentMatchers.anyString());
        Mockito.verifyZeroInteractions(this.redisOperations);
        Mockito.verifyZeroInteractions(this.hashOperations);
    }

    @Test
    public void getSessionNotFound() {
        BDDMockito.given(this.redisOperations.opsForHash()).willReturn(this.hashOperations);
        BDDMockito.given(this.hashOperations.entries(ArgumentMatchers.anyString())).willReturn(Flux.empty());
        BDDMockito.given(this.redisOperations.delete(ArgumentMatchers.anyString())).willReturn(Mono.just(0L));
        StepVerifier.create(this.repository.findById("test")).verifyComplete();
        Mockito.verify(this.redisOperations).opsForHash();
        Mockito.verify(this.hashOperations).entries(ArgumentMatchers.anyString());
        Mockito.verify(this.redisOperations).delete(ArgumentMatchers.anyString());
        Mockito.verifyZeroInteractions(this.redisOperations);
        Mockito.verifyZeroInteractions(this.hashOperations);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getSessionFound() {
        BDDMockito.given(this.redisOperations.opsForHash()).willReturn(this.hashOperations);
        String attribute1 = "attribute1";
        String attribute2 = "attribute2";
        MapSession expected = new MapSession("test");
        expected.setLastAccessedTime(Instant.now().minusSeconds(60));
        expected.setAttribute(attribute1, "test");
        expected.setAttribute(attribute2, null);
        Map map = map(((ATTRIBUTE_PREFIX) + attribute1), expected.getAttribute(attribute1), ((ATTRIBUTE_PREFIX) + attribute2), expected.getAttribute(attribute2), CREATION_TIME_KEY, expected.getCreationTime().toEpochMilli(), MAX_INACTIVE_INTERVAL_KEY, ((int) (expected.getMaxInactiveInterval().getSeconds())), LAST_ACCESSED_TIME_KEY, expected.getLastAccessedTime().toEpochMilli());
        BDDMockito.given(this.hashOperations.entries(ArgumentMatchers.anyString())).willReturn(Flux.fromIterable(map.entrySet()));
        StepVerifier.create(this.repository.findById("test")).consumeNextWith(( session) -> {
            verify(this.redisOperations).opsForHash();
            verify(this.hashOperations).entries(anyString());
            verifyZeroInteractions(this.redisOperations);
            verifyZeroInteractions(this.hashOperations);
            assertThat(session.getId()).isEqualTo(expected.getId());
            assertThat(session.getAttributeNames()).isEqualTo(expected.getAttributeNames());
            assertThat(session.<String>getAttribute(attribute1)).isEqualTo(expected.getAttribute(attribute1));
            assertThat(session.<String>getAttribute(attribute2)).isEqualTo(expected.getAttribute(attribute2));
            assertThat(session.getCreationTime().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(expected.getCreationTime().truncatedTo(ChronoUnit.MILLIS));
            assertThat(session.getMaxInactiveInterval()).isEqualTo(expected.getMaxInactiveInterval());
            assertThat(session.getLastAccessedTime().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(expected.getLastAccessedTime().truncatedTo(ChronoUnit.MILLIS));
        }).verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getSessionExpired() {
        BDDMockito.given(this.redisOperations.opsForHash()).willReturn(this.hashOperations);
        Map map = map(CREATION_TIME_KEY, 0L, MAX_INACTIVE_INTERVAL_KEY, 1, LAST_ACCESSED_TIME_KEY, Instant.now().minus(5, ChronoUnit.MINUTES).toEpochMilli());
        BDDMockito.given(this.hashOperations.entries(ArgumentMatchers.anyString())).willReturn(Flux.fromIterable(map.entrySet()));
        BDDMockito.given(this.redisOperations.delete(ArgumentMatchers.anyString())).willReturn(Mono.just(0L));
        StepVerifier.create(this.repository.findById("test")).verifyComplete();
        Mockito.verify(this.redisOperations).opsForHash();
        Mockito.verify(this.hashOperations).entries(ArgumentMatchers.anyString());
        Mockito.verify(this.redisOperations).delete(ArgumentMatchers.anyString());
        Mockito.verifyZeroInteractions(this.redisOperations);
        Mockito.verifyZeroInteractions(this.hashOperations);
    }

    // gh-1120
    @Test
    public void getAttributeNamesAndRemove() {
        RedisSession session = this.repository.new RedisSession(this.cached);
        session.setAttribute("attribute1", "value1");
        session.setAttribute("attribute2", "value2");
        for (String attributeName : session.getAttributeNames()) {
            session.removeAttribute(attributeName);
        }
        assertThat(session.getAttributeNames()).isEmpty();
    }
}

