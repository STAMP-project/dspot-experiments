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
import RedisConstants.KEYS;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisTemplate;


@RunWith(MockitoJUnitRunner.class)
public class RedisTransactionTest extends RedisTestSupport {
    @Mock
    private RedisTemplate<String, ?> redisTemplate;

    @Test
    public void shouldExecuteMULTI() throws Exception {
        sendHeaders(COMMAND, "MULTI");
        Mockito.verify(redisTemplate).multi();
    }

    @Test
    public void shouldExecuteDISCARD() throws Exception {
        sendHeaders(COMMAND, "DISCARD");
        Mockito.verify(redisTemplate).discard();
    }

    @Test
    public void shouldExecuteEXEC() throws Exception {
        sendHeaders(COMMAND, "EXEC");
        Mockito.verify(redisTemplate).exec();
    }

    @Test
    public void shouldExecuteUNWATCH() throws Exception {
        sendHeaders(COMMAND, "UNWATCH");
        Mockito.verify(redisTemplate).unwatch();
    }

    @Test
    public void shouldExecuteWATCH() throws Exception {
        List<String> keys = new ArrayList<>();
        keys.add("key");
        sendHeaders(COMMAND, "WATCH", KEYS, keys);
        Mockito.verify(redisTemplate).watch(keys);
    }
}

