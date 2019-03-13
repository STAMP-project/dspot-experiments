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
package org.apache.camel.component.redis;


import RedisConstants.COMMAND;
import RedisConstants.DESTINATION;
import RedisConstants.END;
import RedisConstants.INCREMENT;
import RedisConstants.KEY;
import RedisConstants.KEYS;
import RedisConstants.MAX;
import RedisConstants.MIN;
import RedisConstants.SCORE;
import RedisConstants.START;
import RedisConstants.VALUE;
import RedisConstants.WITHSCORE;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;


@RunWith(MockitoJUnitRunner.class)
public class RedisSortedSetTest extends RedisTestSupport {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @Test
    public void shouldExecuteZADD() {
        Mockito.when(zSetOperations.add(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyDouble())).thenReturn(false);
        Object result = sendHeaders(COMMAND, "ZADD", KEY, "key", VALUE, "value", SCORE, 1.0);
        Mockito.verify(zSetOperations).add("key", "value", 1.0);
        assertEquals(false, result);
    }

    @Test
    public void shouldExecuteZCARD() {
        Mockito.when(zSetOperations.size(ArgumentMatchers.anyString())).thenReturn(2L);
        Object result = sendHeaders(COMMAND, "ZCARD", KEY, "key");
        Mockito.verify(zSetOperations).size("key");
        assertEquals(2L, result);
    }

    @Test
    public void shouldExecuteZCOUNT() {
        Mockito.when(zSetOperations.count(ArgumentMatchers.anyString(), ArgumentMatchers.anyDouble(), ArgumentMatchers.anyDouble())).thenReturn(3L);
        Object result = sendHeaders(COMMAND, "ZCOUNT", KEY, "key", MIN, 1.0, MAX, 2.0);
        Mockito.verify(zSetOperations).count("key", 1.0, 2.0);
        assertEquals(3L, result);
    }

    @Test
    public void shouldExecuteZINCRBY() {
        Mockito.when(zSetOperations.incrementScore(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyDouble())).thenReturn(3.0);
        Object result = sendHeaders(COMMAND, "ZINCRBY", KEY, "key", VALUE, "value", INCREMENT, 2.0);
        Mockito.verify(zSetOperations).incrementScore("key", "value", 2.0);
        assertEquals(3.0, result);
    }

    @Test
    public void shouldExecuteZINTERSTORE() {
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key3");
        sendHeaders(COMMAND, "ZINTERSTORE", KEY, "key", DESTINATION, "destination", KEYS, keys);
        Mockito.verify(zSetOperations).intersectAndStore("key", keys, "destination");
    }

    @Test
    public void shouldExecuteZRANGE() {
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key3");
        Mockito.when(zSetOperations.range(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(keys);
        Object result = sendHeaders(COMMAND, "ZRANGE", KEY, "key", START, 1, END, 3);
        Mockito.verify(zSetOperations).range("key", 1, 3);
        assertEquals(keys, result);
    }

    @Test
    public void shouldExecuteZRANGEWithScores() {
        Mockito.when(zSetOperations.rangeWithScores(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(null);
        Object result = sendHeaders(COMMAND, "ZRANGE", KEY, "key", WITHSCORE, true, START, 1, END, 3);
        Mockito.verify(zSetOperations).rangeWithScores("key", 1, 3);
        assertEquals(null, result);
    }

    @Test
    public void shouldExecuteZRANGEBYSCORE() {
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key3");
        Mockito.when(zSetOperations.rangeByScore(ArgumentMatchers.anyString(), ArgumentMatchers.anyDouble(), ArgumentMatchers.anyDouble())).thenReturn(keys);
        Object result = sendHeaders(COMMAND, "ZRANGEBYSCORE", KEY, "key", MIN, 1.0, MAX, 2.0);
        Mockito.verify(zSetOperations).rangeByScore("key", 1.0, 2.0);
        assertEquals(keys, result);
    }

    @Test
    public void shouldExecuteZRANK() {
        Mockito.when(zSetOperations.rank(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(1L);
        Object result = sendHeaders(COMMAND, "ZRANK", KEY, "key", VALUE, "value");
        Mockito.verify(zSetOperations).rank("key", "value");
        assertEquals(1L, result);
    }

    @Test
    public void shouldExecuteZREM() {
        Mockito.when(zSetOperations.remove(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(Long.valueOf(1));
        Object result = sendHeaders(COMMAND, "ZREM", KEY, "key", VALUE, "value");
        Mockito.verify(zSetOperations).remove("key", "value");
        assertEquals(1L, result);
    }

    @Test
    public void shouldExecuteZREMRANGEBYRANK() {
        sendHeaders(COMMAND, "ZREMRANGEBYRANK", KEY, "key", START, 1, END, 2);
        Mockito.verify(zSetOperations).removeRange("key", 1, 2);
    }

    @Test
    public void shouldExecuteZREMRANGEBYSCORE() {
        sendHeaders(COMMAND, "ZREMRANGEBYSCORE", KEY, "key", START, 1, END, 2);
        Mockito.verify(zSetOperations).removeRangeByScore("key", 1.0, 2.0);
    }

    @Test
    public void shouldExecuteZREVRANGE() {
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key3");
        Mockito.when(zSetOperations.reverseRange(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(keys);
        Object result = sendHeaders(COMMAND, "ZREVRANGE", KEY, "key", START, 1, END, 3);
        Mockito.verify(zSetOperations).reverseRange("key", 1, 3);
        assertEquals(keys, result);
    }

    @Test
    public void shouldExecuteZREVRANGEWithScores() {
        Mockito.when(zSetOperations.reverseRangeWithScores(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(null);
        Object result = sendHeaders(COMMAND, "ZREVRANGE", KEY, "key", WITHSCORE, true, START, 1, END, 3);
        Mockito.verify(zSetOperations).reverseRangeWithScores("key", 1, 3);
        assertEquals(null, result);
    }

    @Test
    public void shouldExecuteZREVRANGEBYSCORE() {
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key3");
        Mockito.when(zSetOperations.reverseRangeByScore(ArgumentMatchers.anyString(), ArgumentMatchers.anyDouble(), ArgumentMatchers.anyDouble())).thenReturn(keys);
        Object result = sendHeaders(COMMAND, "ZREVRANGEBYSCORE", KEY, "key", MIN, 1.0, MAX, 2.0);
        Mockito.verify(zSetOperations).reverseRangeByScore("key", 1.0, 2.0);
        assertEquals(keys, result);
    }

    @Test
    public void shouldExecuteZREVRANK() {
        Mockito.when(zSetOperations.reverseRank(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(1L);
        Object result = sendHeaders(COMMAND, "ZREVRANK", KEY, "key", VALUE, "value");
        Mockito.verify(zSetOperations).reverseRank("key", "value");
        assertEquals(1L, result);
    }

    @Test
    public void shouldExecuteZUNIONSTORE() {
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key3");
        sendHeaders(COMMAND, "ZUNIONSTORE", KEY, "key", DESTINATION, "destination", KEYS, keys);
        Mockito.verify(zSetOperations).unionAndStore("key", keys, "destination");
    }
}

