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
package org.springframework.boot.actuate.redis;


import io.lettuce.core.RedisConnectionException;
import java.util.Properties;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveServerCommands;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Tests for {@link RedisReactiveHealthIndicator}.
 *
 * @author Stephane Nicoll
 * @author Mark Paluch
 * @author Nikolay Rybak
 */
public class RedisReactiveHealthIndicatorTests {
    @Test
    public void redisIsUp() {
        Properties info = new Properties();
        info.put("redis_version", "2.8.9");
        ReactiveRedisConnection redisConnection = Mockito.mock(ReactiveRedisConnection.class);
        ReactiveServerCommands commands = Mockito.mock(ReactiveServerCommands.class);
        BDDMockito.given(commands.info()).willReturn(Mono.just(info));
        RedisReactiveHealthIndicator healthIndicator = createHealthIndicator(redisConnection, commands);
        Mono<Health> health = healthIndicator.health();
        StepVerifier.create(health).consumeNextWith(( h) -> {
            assertThat(h.getStatus()).isEqualTo(Status.UP);
            assertThat(h.getDetails()).containsOnlyKeys("version");
            assertThat(h.getDetails().get("version")).isEqualTo("2.8.9");
        }).verifyComplete();
        Mockito.verify(redisConnection).close();
    }

    @Test
    public void redisCommandIsDown() {
        ReactiveServerCommands commands = Mockito.mock(ReactiveServerCommands.class);
        BDDMockito.given(commands.info()).willReturn(Mono.error(new RedisConnectionFailureException("Connection failed")));
        ReactiveRedisConnection redisConnection = Mockito.mock(ReactiveRedisConnection.class);
        RedisReactiveHealthIndicator healthIndicator = createHealthIndicator(redisConnection, commands);
        Mono<Health> health = healthIndicator.health();
        StepVerifier.create(health).consumeNextWith(( h) -> assertThat(h.getStatus()).isEqualTo(Status.DOWN)).verifyComplete();
        Mockito.verify(redisConnection).close();
    }

    @Test
    public void redisConnectionIsDown() {
        ReactiveRedisConnectionFactory redisConnectionFactory = Mockito.mock(ReactiveRedisConnectionFactory.class);
        BDDMockito.given(redisConnectionFactory.getReactiveConnection()).willThrow(new RedisConnectionException("Unable to connect to localhost:6379"));
        RedisReactiveHealthIndicator healthIndicator = new RedisReactiveHealthIndicator(redisConnectionFactory);
        Mono<Health> health = healthIndicator.health();
        StepVerifier.create(health).consumeNextWith(( h) -> assertThat(h.getStatus()).isEqualTo(Status.DOWN)).verifyComplete();
    }
}

