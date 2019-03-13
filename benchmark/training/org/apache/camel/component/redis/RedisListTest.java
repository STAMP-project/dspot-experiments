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
import RedisConstants.COUNT;
import RedisConstants.DESTINATION;
import RedisConstants.END;
import RedisConstants.INDEX;
import RedisConstants.KEY;
import RedisConstants.PIVOT;
import RedisConstants.POSITION;
import RedisConstants.START;
import RedisConstants.TIMEOUT;
import RedisConstants.VALUE;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;


@RunWith(MockitoJUnitRunner.class)
public class RedisListTest extends RedisTestSupport {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ListOperations<String, String> listOperations;

    @Test
    public void shouldExecuteLPOP() throws Exception {
        Mockito.when(listOperations.leftPop(ArgumentMatchers.anyString())).thenReturn("value");
        Object result = sendHeaders(COMMAND, "LPOP", KEY, "key");
        Mockito.verify(listOperations).leftPop("key");
        assertEquals("value", result);
    }

    @Test
    public void shouldExecuteBLPOP() throws Exception {
        Mockito.when(listOperations.leftPop(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn("value");
        Object result = sendHeaders(COMMAND, "BLPOP", KEY, "key", TIMEOUT, "10");
        Mockito.verify(listOperations).leftPop("key", 10, TimeUnit.SECONDS);
        assertEquals("value", result);
    }

    @Test
    public void shouldExecuteBRPOP() throws Exception {
        Mockito.when(listOperations.rightPop(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn("value");
        Object result = sendHeaders(COMMAND, "BRPOP", KEY, "key", TIMEOUT, "10");
        Mockito.verify(listOperations).rightPop("key", 10, TimeUnit.SECONDS);
        assertEquals("value", result);
    }

    @Test
    public void shouldExecuteRPOP() throws Exception {
        Mockito.when(listOperations.rightPop(ArgumentMatchers.anyString())).thenReturn("value");
        Object result = sendHeaders(COMMAND, "RPOP", KEY, "key");
        Mockito.verify(listOperations).rightPop("key");
        assertEquals("value", result);
    }

    @Test
    public void shouldExecuteRPOPLPUSH() throws Exception {
        Mockito.when(listOperations.rightPopAndLeftPush(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn("value");
        Object result = sendHeaders(COMMAND, "RPOPLPUSH", KEY, "key", DESTINATION, "destination");
        Mockito.verify(listOperations).rightPopAndLeftPush("key", "destination");
        assertEquals("value", result);
    }

    @Test
    public void shouldExecuteBRPOPLPUSH() throws Exception {
        Mockito.when(listOperations.rightPopAndLeftPush(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn("value");
        Object result = sendHeaders(COMMAND, "BRPOPLPUSH", KEY, "key", DESTINATION, "destination", TIMEOUT, "10");
        Mockito.verify(listOperations).rightPopAndLeftPush("key", "destination", 10, TimeUnit.SECONDS);
        assertEquals("value", result);
    }

    @Test
    public void shouldExecuteLINDEX() throws Exception {
        Mockito.when(listOperations.index(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn("value");
        Object result = sendHeaders(COMMAND, "LINDEX", KEY, "key", INDEX, "2");
        Mockito.verify(listOperations).index("key", 2);
        assertEquals("value", result);
    }

    @Test
    public void shouldExecuteLINSERTBEFORE() throws Exception {
        Mockito.when(listOperations.leftPush(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(2L);
        Object result = sendHeaders(COMMAND, "LINSERT", KEY, "key", VALUE, "value", PIVOT, "pivot", POSITION, "BEFORE");
        Mockito.verify(listOperations).leftPush("key", "pivot", "value");
        assertEquals(2L, result);
    }

    @Test
    public void shouldExecuteLINSERTAFTER() throws Exception {
        Mockito.when(listOperations.rightPush(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(2L);
        Object result = sendHeaders(COMMAND, "LINSERT", KEY, "key", VALUE, "value", PIVOT, "pivot", POSITION, "AFTER");
        Mockito.verify(listOperations).rightPush("key", "pivot", "value");
        assertEquals(2L, result);
    }

    @Test
    public void shouldExecuteLLEN() throws Exception {
        Mockito.when(listOperations.size(ArgumentMatchers.anyString())).thenReturn(2L);
        Object result = sendHeaders(COMMAND, "LLEN", KEY, "key");
        Mockito.verify(listOperations).size("key");
        assertEquals(2L, result);
    }

    @Test
    public void shouldExecuteLPUSH() throws Exception {
        Mockito.when(listOperations.leftPush(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(2L);
        Object result = sendHeaders(COMMAND, "LPUSH", KEY, "key", VALUE, "value");
        Mockito.verify(listOperations).leftPush("key", "value");
        assertEquals(2L, result);
    }

    @Test
    public void shouldExecuteLRANGE() throws Exception {
        List<String> values = new ArrayList<>();
        values.add("value");
        Mockito.when(listOperations.range(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(values);
        Object result = sendHeaders(COMMAND, "LRANGE", KEY, "key", START, "0", END, "1");
        Mockito.verify(listOperations).range("key", 0, 1);
        assertEquals(values, result);
    }

    @Test
    public void shouldExecuteLREM() throws Exception {
        Mockito.when(listOperations.remove(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(2L);
        Object result = sendHeaders(COMMAND, "LREM", KEY, "key", VALUE, "value", COUNT, "1");
        Mockito.verify(listOperations).remove("key", 1, "value");
        assertEquals(2L, result);
    }

    @Test
    public void shouldExecuteLSET() throws Exception {
        sendHeaders(COMMAND, "LSET", KEY, "key", VALUE, "value", INDEX, "1");
        Mockito.verify(listOperations).set("key", 1, "value");
    }

    @Test
    public void shouldExecuteLTRIM() throws Exception {
        sendHeaders(COMMAND, "LTRIM", KEY, "key", START, "1", END, "2");
        Mockito.verify(listOperations).trim("key", 1, 2);
    }

    @Test
    public void shouldExecuteRPUSH() throws Exception {
        Mockito.when(listOperations.rightPush(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(2L);
        Object result = sendHeaders(COMMAND, "RPUSH", KEY, "key", VALUE, "value");
        Mockito.verify(listOperations).rightPush("key", "value");
        assertEquals(2L, result);
    }

    @Test
    public void shouldExecuteRPUSHX() throws Exception {
        Mockito.when(listOperations.rightPushIfPresent(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(2L);
        Object result = sendHeaders(COMMAND, "RPUSHX", KEY, "key", VALUE, "value");
        Mockito.verify(listOperations).rightPushIfPresent("key", "value");
        assertEquals(2L, result);
    }
}

