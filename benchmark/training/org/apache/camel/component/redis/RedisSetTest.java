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
import RedisConstants.KEY;
import RedisConstants.KEYS;
import RedisConstants.VALUE;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;


@RunWith(MockitoJUnitRunner.class)
public class RedisSetTest extends RedisTestSupport {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private SetOperations<String, String> setOperations;

    @Test
    public void shouldExecuteSADD() throws Exception {
        Mockito.when(setOperations.add(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(null);
        Object result = sendHeaders(COMMAND, "SADD", KEY, "key", VALUE, "value");
        Mockito.verify(setOperations).add("key", "value");
        assertEquals(null, result);
    }

    @Test
    public void shouldExecuteSCARD() throws Exception {
        Mockito.when(setOperations.size(ArgumentMatchers.anyString())).thenReturn(2L);
        Object result = sendHeaders(COMMAND, "SCARD", KEY, "key");
        Mockito.verify(setOperations).size("key");
        assertEquals(2L, result);
    }

    @Test
    public void shouldExecuteSDIFF() throws Exception {
        Set<String> difference = new HashSet<>();
        difference.add("a");
        difference.add("b");
        Mockito.when(setOperations.difference(ArgumentMatchers.anyString(), ArgumentMatchers.anySet())).thenReturn(difference);
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key3");
        Object result = sendHeaders(COMMAND, "SDIFF", KEY, "key", KEYS, keys);
        Mockito.verify(setOperations).difference("key", keys);
        assertEquals(difference, result);
    }

    @Test
    public void shouldExecuteSDIFFSTORE() throws Exception {
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key3");
        sendHeaders(COMMAND, "SDIFFSTORE", KEY, "key", KEYS, keys, DESTINATION, "destination");
        Mockito.verify(setOperations).differenceAndStore("key", keys, "destination");
    }

    @Test
    public void shouldExecuteSINTER() throws Exception {
        Set<String> difference = new HashSet<>();
        difference.add("a");
        difference.add("b");
        Mockito.when(setOperations.intersect(ArgumentMatchers.anyString(), ArgumentMatchers.anySet())).thenReturn(difference);
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key3");
        Object result = sendHeaders(COMMAND, "SINTER", KEY, "key", KEYS, keys);
        Mockito.verify(setOperations).intersect("key", keys);
        assertEquals(difference, result);
    }

    @Test
    public void shouldExecuteSINTERSTORE() throws Exception {
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key3");
        sendHeaders(COMMAND, "SINTERSTORE", KEY, "key", DESTINATION, "destination", KEYS, keys);
        Mockito.verify(setOperations).intersectAndStore("key", keys, "destination");
    }

    @Test
    public void shouldExecuteSISMEMBER() throws Exception {
        Mockito.when(setOperations.isMember(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(true);
        Object result = sendHeaders(COMMAND, "SISMEMBER", KEY, "key", VALUE, "set");
        Mockito.verify(setOperations).isMember("key", "set");
        assertEquals(true, result);
    }

    @Test
    public void shouldExecuteSMEMBERS() throws Exception {
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key3");
        Mockito.when(setOperations.members(ArgumentMatchers.anyString())).thenReturn(keys);
        Object result = sendHeaders(COMMAND, "SMEMBERS", KEY, "key");
        Mockito.verify(setOperations).members("key");
        assertEquals(keys, result);
    }

    @Test
    public void shouldExecuteSMOVE() throws Exception {
        sendHeaders(COMMAND, "SMOVE", KEY, "key", VALUE, "value", DESTINATION, "destination");
        Mockito.verify(setOperations).move("key", "value", "destination");
    }

    @Test
    public void shouldExecuteSPOP() throws Exception {
        String field = "value";
        Mockito.when(setOperations.pop(ArgumentMatchers.anyString())).thenReturn(field);
        Object result = sendHeaders(COMMAND, "SPOP", KEY, "key");
        Mockito.verify(setOperations).pop("key");
        assertEquals(field, result);
    }

    @Test
    public void shouldExecuteSRANDMEMBER() throws Exception {
        String field = "value";
        Mockito.when(setOperations.randomMember(ArgumentMatchers.anyString())).thenReturn(field);
        Object result = sendHeaders(COMMAND, "SRANDMEMBER", KEY, "key");
        Mockito.verify(setOperations).randomMember("key");
        assertEquals(field, result);
    }

    @Test
    public void shouldExecuteSREM() throws Exception {
        Mockito.when(setOperations.remove(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Long.valueOf(1));
        Object result = sendHeaders(COMMAND, "SREM", KEY, "key", VALUE, "value");
        Mockito.verify(setOperations).remove("key", "value");
        assertEquals(1L, result);
    }

    @Test
    public void shouldExecuteSUNION() throws Exception {
        Set<String> resultKeys = new HashSet<>();
        resultKeys.add("key2");
        resultKeys.add("key3");
        Mockito.when(setOperations.union(ArgumentMatchers.anyString(), ArgumentMatchers.anySet())).thenReturn(resultKeys);
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key4");
        Object result = sendHeaders(COMMAND, "SUNION", KEY, "key", KEYS, keys);
        Mockito.verify(setOperations).union("key", keys);
        assertEquals(resultKeys, result);
    }

    @Test
    public void shouldExecuteSUNIONSTORE() throws Exception {
        Set<String> keys = new HashSet<>();
        keys.add("key2");
        keys.add("key4");
        sendHeaders(COMMAND, "SUNIONSTORE", KEY, "key", KEYS, keys, DESTINATION, "destination");
        Mockito.verify(setOperations).unionAndStore("key", keys, "destination");
    }
}

