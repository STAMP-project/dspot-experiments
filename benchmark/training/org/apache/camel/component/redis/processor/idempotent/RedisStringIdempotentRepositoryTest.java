/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.redis.processor.idempotent;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;


@RunWith(MockitoJUnitRunner.class)
public class RedisStringIdempotentRepositoryTest {
    private static final String REPOSITORY = "testRepository";

    private static final String KEY = "KEY";

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @Mock
    private RedisConnection redisConnection;

    @Mock
    private RedisOperations<String, String> redisOperations;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisStringIdempotentRepository idempotentRepository;

    @Test
    public void shouldAddKey() {
        idempotentRepository.add(RedisStringIdempotentRepositoryTest.KEY);
        Mockito.verify(valueOperations).setIfAbsent(idempotentRepository.createRedisKey(RedisStringIdempotentRepositoryTest.KEY), RedisStringIdempotentRepositoryTest.KEY);
        Mockito.verify(redisOperations).expire(idempotentRepository.createRedisKey(RedisStringIdempotentRepositoryTest.KEY), 1000L, TimeUnit.SECONDS);
    }

    @Test
    public void shoulCheckForMembers() {
        idempotentRepository.contains(RedisStringIdempotentRepositoryTest.KEY);
        Mockito.verify(valueOperations).get(idempotentRepository.createRedisKey(RedisStringIdempotentRepositoryTest.KEY));
    }

    @Test
    public void shouldReturnProcessorName() {
        String processorName = idempotentRepository.getProcessorName();
        Assert.assertEquals(RedisStringIdempotentRepositoryTest.REPOSITORY, processorName);
    }
}

