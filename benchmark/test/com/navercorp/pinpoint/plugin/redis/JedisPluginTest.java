/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.redis;


import com.navercorp.pinpoint.profiler.context.SpanEvent;
import com.navercorp.pinpoint.test.junit4.BasePinpointTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;


public class JedisPluginTest extends BasePinpointTest {
    private static final String HOST = "localhost";

    private static final int PORT = 6379;

    @Test
    public void jedis() {
        JedisPluginTest.JedisMock jedis = new JedisPluginTest.JedisMock("localhost", 6379);
        try {
            jedis.get("foo");
        } finally {
            close(jedis);
        }
        final List<SpanEvent> events = getCurrentSpanEvents();
        Assert.assertEquals(1, events.size());
        final SpanEvent eventBo = events.get(0);
        Assert.assertEquals((((JedisPluginTest.HOST) + ":") + (JedisPluginTest.PORT)), eventBo.getEndPoint());
        Assert.assertEquals("REDIS", eventBo.getDestinationId());
    }

    @Test
    public void binaryJedis() {
        JedisPluginTest.JedisMock jedis = new JedisPluginTest.JedisMock("localhost", 6379);
        try {
            get("foo".getBytes());
        } finally {
            close(jedis);
        }
        final List<SpanEvent> events = getCurrentSpanEvents();
        Assert.assertEquals(1, events.size());
        final SpanEvent eventBo = events.get(0);
        Assert.assertEquals((((JedisPluginTest.HOST) + ":") + (JedisPluginTest.PORT)), eventBo.getEndPoint());
        Assert.assertEquals("REDIS", eventBo.getDestinationId());
    }

    @Test
    public void pipeline() {
        JedisPluginTest.JedisMock jedis = new JedisPluginTest.JedisMock("localhost", 6379);
        try {
            Pipeline pipeline = pipelined();
            pipeline.get("foo");
        } finally {
            close(jedis);
        }
        final List<SpanEvent> events = getCurrentSpanEvents();
        Assert.assertEquals(1, events.size());
    }

    public class JedisMock extends Jedis {
        public JedisMock(String host, int port) {
            super(host, port);
            client = Mockito.mock(Client.class);
            // for 'get' command
            Mockito.when(client.isInMulti()).thenReturn(false);
            Mockito.when(client.getBulkReply()).thenReturn("bar");
            Mockito.when(client.getBinaryBulkReply()).thenReturn("bar".getBytes());
        }
    }
}

