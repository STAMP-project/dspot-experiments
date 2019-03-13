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


import RedisConstants.CHANNEL;
import RedisConstants.COMMAND;
import RedisConstants.MESSAGE;
import RedisConstants.VALUE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;


@RunWith(MockitoJUnitRunner.class)
public class RedisConnectionTest extends RedisTestSupport {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void shouldExecuteECHO() throws Exception {
        Mockito.when(redisTemplate.execute(ArgumentMatchers.<RedisCallback<String>>any())).thenReturn("value");
        Object result = sendHeaders(COMMAND, "ECHO", VALUE, "value");
        assertEquals("value", result);
    }

    @Test
    public void shouldExecutePING() throws Exception {
        Mockito.when(redisTemplate.execute(ArgumentMatchers.<RedisCallback<String>>any())).thenReturn("PONG");
        Object result = sendHeaders(COMMAND, "PING");
        assertEquals("PONG", result);
    }

    @Test
    public void shouldExecuteQUIT() throws Exception {
        sendHeaders(COMMAND, "QUIT");
        Mockito.verify(redisTemplate).execute(ArgumentMatchers.<RedisCallback<String>>any());
    }

    @Test
    public void shouldExecutePUBLISH() throws Exception {
        sendHeaders(COMMAND, "PUBLISH", CHANNEL, "channel", MESSAGE, "a message");
        Mockito.verify(redisTemplate).convertAndSend("channel", "a message");
    }
}

