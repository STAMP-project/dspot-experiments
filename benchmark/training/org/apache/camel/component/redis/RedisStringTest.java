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
import RedisConstants.END;
import RedisConstants.FIELDS;
import RedisConstants.KEY;
import RedisConstants.OFFSET;
import RedisConstants.START;
import RedisConstants.TIMEOUT;
import RedisConstants.VALUE;
import RedisConstants.VALUES;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ValueOperations;


@RunWith(MockitoJUnitRunner.class)
public class RedisStringTest extends RedisTestSupport {
    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    public void shouldExecuteSET() throws Exception {
        sendHeaders(COMMAND, "SET", KEY, "key", VALUE, "value");
        Mockito.verify(valueOperations).set("key", "value");
    }

    @Test
    public void shouldExecuteSETNX() throws Exception {
        sendHeaders(COMMAND, "SETNX", KEY, "key", VALUE, "value");
        Mockito.verify(valueOperations).setIfAbsent("key", "value");
    }

    @Test
    public void shouldExecuteSETEX() throws Exception {
        sendHeaders(COMMAND, "SETEX", KEY, "key", TIMEOUT, "10", VALUE, "value");
        Mockito.verify(valueOperations).set("key", "value", 10, TimeUnit.SECONDS);
    }

    @Test
    public void shouldExecuteSETRANGE() throws Exception {
        sendHeaders(COMMAND, "SETRANGE", KEY, "key", OFFSET, "10", VALUE, "value");
        Mockito.verify(valueOperations).set("key", "value", 10);
    }

    @Test
    public void shouldExecuteGETRANGE() throws Exception {
        Mockito.when(valueOperations.get(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn("test");
        Object result = sendHeaders(COMMAND, "GETRANGE", KEY, "key", START, "2", END, "4");
        Mockito.verify(valueOperations).get("key", 2, 4);
        assertEquals("test", result);
    }

    @Test
    public void shouldExecuteSETBIT() throws Exception {
        sendHeaders(COMMAND, "SETBIT", KEY, "key", OFFSET, "10", VALUE, "0");
        Mockito.verify(redisTemplate).execute(ArgumentMatchers.<RedisCallback<String>>any());
    }

    @Test
    public void shouldExecuteGETBIT() throws Exception {
        Mockito.when(redisTemplate.execute(ArgumentMatchers.<RedisCallback<Boolean>>any())).thenReturn(true);
        Object result = sendHeaders(COMMAND, "GETBIT", KEY, "key", OFFSET, "2");
        Mockito.verify(redisTemplate).execute(ArgumentMatchers.<RedisCallback<String>>any());
        assertEquals(true, result);
    }

    @Test
    public void shouldExecuteGET() throws Exception {
        Mockito.when(valueOperations.get("key")).thenReturn("value");
        Object result = sendHeaders(COMMAND, "GET", KEY, "key");
        Mockito.verify(valueOperations).get("key");
        assertEquals("value", result);
    }

    @Test
    public void shouldExecuteAPPEND() throws Exception {
        Mockito.when(valueOperations.append(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(5);
        Object result = sendHeaders(COMMAND, "APPEND", KEY, "key", VALUE, "value");
        Mockito.verify(valueOperations).append("key", "value");
        assertEquals(5, result);
    }

    @Test
    public void shouldExecuteDECR() throws Exception {
        Mockito.when(valueOperations.increment(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(2L);
        Object result = sendHeaders(COMMAND, "DECR", KEY, "key");
        Mockito.verify(valueOperations).increment("key", (-1));
        assertEquals(2L, result);
    }

    @Test
    public void shouldExecuteDECRBY() throws Exception {
        Mockito.when(valueOperations.increment(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(1L);
        Object result = sendHeaders(COMMAND, "DECRBY", VALUE, "2", KEY, "key");
        Mockito.verify(valueOperations).increment("key", (-2));
        assertEquals(1L, result);
    }

    @Test
    public void shouldExecuteINCR() throws Exception {
        Mockito.when(valueOperations.increment(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(2L);
        Object result = sendHeaders(COMMAND, "INCR", KEY, "key");
        Mockito.verify(valueOperations).increment("key", 1);
        assertEquals(2L, result);
    }

    @Test
    public void shouldExecuteINCRBY() throws Exception {
        Mockito.when(valueOperations.increment(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(1L);
        Object result = sendHeaders(COMMAND, "INCRBY", VALUE, "2", KEY, "key");
        Mockito.verify(valueOperations).increment("key", 2);
        assertEquals(1L, result);
    }

    @Test
    public void shouldExecuteSTRLEN() throws Exception {
        Mockito.when(valueOperations.size(ArgumentMatchers.anyString())).thenReturn(5L);
        Object result = sendHeaders(COMMAND, "STRLEN", KEY, "key");
        Mockito.verify(valueOperations).size("key");
        assertEquals(5L, result);
    }

    @Test
    public void shouldExecuteMGET() throws Exception {
        List<String> fields = new ArrayList<>();
        fields.add("field1");
        List<String> values = new ArrayList<>();
        values.add("value1");
        Mockito.when(valueOperations.multiGet(fields)).thenReturn(values);
        Object result = sendHeaders(COMMAND, "MGET", FIELDS, fields);
        Mockito.verify(valueOperations).multiGet(fields);
        assertEquals(values, result);
    }

    @Test
    public void shouldExecuteMSET() throws Exception {
        Map<String, String> values = new HashMap<>();
        values.put("field1", "valu1");
        sendHeaders(COMMAND, "MSET", VALUES, values);
        Mockito.verify(valueOperations).multiSet(values);
    }

    @Test
    public void shouldExecuteMSETNX() throws Exception {
        Map<String, String> values = new HashMap<>();
        values.put("field1", "valu1");
        sendHeaders(COMMAND, "MSETNX", VALUES, values);
        Mockito.verify(valueOperations).multiSetIfAbsent(values);
    }

    @Test
    public void shouldExecuteGETSET() throws Exception {
        Mockito.when(valueOperations.getAndSet(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn("old value");
        String value = "new value";
        Object result = sendHeaders(COMMAND, "GETSET", KEY, "key", VALUE, value);
        Mockito.verify(valueOperations).getAndSet("key", value);
        assertEquals("old value", result);
    }
}

