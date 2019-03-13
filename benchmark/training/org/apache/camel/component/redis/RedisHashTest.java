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
import RedisConstants.FIELD;
import RedisConstants.FIELDS;
import RedisConstants.KEY;
import RedisConstants.VALUE;
import RedisConstants.VALUES;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;


@RunWith(MockitoJUnitRunner.class)
public class RedisHashTest extends RedisTestSupport {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HashOperations<String, String, String> hashOperations;

    @Test
    public void shouldExecuteHDEL() throws Exception {
        sendHeaders(COMMAND, "HDEL", KEY, "key", FIELD, "field");
        Mockito.verify(hashOperations).delete("key", "field");
    }

    @Test
    public void shouldExecuteHEXISTS() throws Exception {
        Mockito.when(hashOperations.hasKey(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(true);
        Object result = sendHeaders(COMMAND, "HEXISTS", KEY, "key", FIELD, "field");
        Mockito.verify(hashOperations).hasKey("key", "field");
        assertEquals(true, result);
    }

    @Test
    public void shouldExecuteHINCRBY() throws Exception {
        Mockito.when(hashOperations.increment(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(1L);
        Object result = sendHeaders(COMMAND, "HINCRBY", KEY, "key", FIELD, "field", VALUE, "1");
        Mockito.verify(hashOperations).increment("key", "field", 1L);
        assertEquals(1L, result);
    }

    @Test
    public void shouldExecuteHKEYS() throws Exception {
        Set<String> fields = new HashSet<>(Arrays.asList(new String[]{ "field1, field2" }));
        Mockito.when(hashOperations.keys(ArgumentMatchers.anyString())).thenReturn(fields);
        Object result = sendHeaders(COMMAND, "HKEYS", KEY, "key");
        Mockito.verify(hashOperations).keys("key");
        assertEquals(fields, result);
    }

    @Test
    public void shouldExecuteHMSET() throws Exception {
        Map<String, String> values = new HashMap<>();
        values.put("field1", "value1");
        values.put("field2", "value");
        sendHeaders(COMMAND, "HMSET", KEY, "key", VALUES, values);
        Mockito.verify(hashOperations).putAll("key", values);
    }

    @Test
    public void shouldExecuteHVALS() throws Exception {
        List<String> values = new ArrayList<>();
        values.add("val1");
        values.add("val2");
        Mockito.when(hashOperations.values(ArgumentMatchers.anyString())).thenReturn(values);
        Object result = sendHeaders(COMMAND, "HVALS", KEY, "key", VALUES, values);
        Mockito.verify(hashOperations).values("key");
        assertEquals(values, result);
    }

    @Test
    public void shouldExecuteHLEN() throws Exception {
        Mockito.when(hashOperations.size(ArgumentMatchers.anyString())).thenReturn(2L);
        Object result = sendHeaders(COMMAND, "HLEN", KEY, "key");
        Mockito.verify(hashOperations).size("key");
        assertEquals(2L, result);
    }

    @Test
    public void shouldSetHashValue() throws Exception {
        sendHeaders(COMMAND, "HSET", KEY, "key", FIELD, "field", VALUE, "value");
        Mockito.verify(hashOperations).put("key", "field", "value");
    }

    @Test
    public void shouldExecuteHSETNX() throws Exception {
        Mockito.when(hashOperations.putIfAbsent(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(true);
        Object result = sendHeaders(COMMAND, "HSETNX", KEY, "key", FIELD, "field", VALUE, "value");
        Mockito.verify(hashOperations).putIfAbsent("key", "field", "value");
        assertEquals(true, result);
    }

    @Test
    public void shouldExecuteHGET() throws Exception {
        Mockito.when(hashOperations.get(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn("value");
        Object result = sendHeaders(COMMAND, "HGET", KEY, "key", FIELD, "field");
        Mockito.verify(hashOperations).get("key", "field");
        assertEquals("value", result);
    }

    @Test
    public void shouldExecuteHGETALL() throws Exception {
        HashMap<String, String> values = new HashMap<>();
        values.put("field1", "valu1");
        Mockito.when(hashOperations.entries(ArgumentMatchers.anyString())).thenReturn(values);
        Object result = sendHeaders(COMMAND, "HGETALL", KEY, "key");
        Mockito.verify(hashOperations).entries("key");
        assertEquals(values, result);
    }

    @Test
    public void shouldExecuteHMGET() throws Exception {
        List<String> fields = new ArrayList<>();
        fields.add("field1");
        Mockito.when(hashOperations.multiGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyList())).thenReturn(fields);
        Object result = sendHeaders(COMMAND, "HMGET", KEY, "key", FIELDS, fields);
        Mockito.verify(hashOperations).multiGet("key", fields);
        assertEquals(fields, result);
    }
}

