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
package org.apache.camel.component.rabbitmq;


import ConnectionFactory.DEFAULT_CHANNEL_MAX;
import ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT;
import ConnectionFactory.DEFAULT_FRAME_MAX;
import ConnectionFactory.DEFAULT_HEARTBEAT;
import com.rabbitmq.client.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.support.SimpleRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class RabbitMQComponentTest {
    private CamelContext context = Mockito.mock(CamelContext.class);

    @Test
    public void testDefaultProperties() throws Exception {
        RabbitMQEndpoint endpoint = createEndpoint(new HashMap<String, Object>());
        Assert.assertEquals(14, endpoint.getPortNumber());
        Assert.assertEquals(10, endpoint.getThreadPoolSize());
        Assert.assertEquals(true, endpoint.isAutoAck());
        Assert.assertEquals(true, endpoint.isAutoDelete());
        Assert.assertEquals(true, endpoint.isDurable());
        Assert.assertEquals(false, endpoint.isExclusiveConsumer());
        Assert.assertEquals(false, endpoint.isAllowNullHeaders());
        Assert.assertEquals("direct", endpoint.getExchangeType());
        Assert.assertEquals(DEFAULT_CONNECTION_TIMEOUT, endpoint.getConnectionTimeout());
        Assert.assertEquals(DEFAULT_CHANNEL_MAX, endpoint.getRequestedChannelMax());
        Assert.assertEquals(DEFAULT_FRAME_MAX, endpoint.getRequestedFrameMax());
        Assert.assertEquals(DEFAULT_HEARTBEAT, endpoint.getRequestedHeartbeat());
        Assert.assertNull(endpoint.getConnectionFactory());
    }

    @Test
    public void testPropertiesSet() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "coldplay");
        params.put("password", "chrism");
        params.put("autoAck", true);
        params.put("vhost", "vman");
        params.put("threadPoolSize", 515);
        params.put("portNumber", 14123);
        params.put("hostname", "special.host");
        params.put("queue", "queuey");
        params.put("exchangeType", "topic");
        params.put("connectionTimeout", 123);
        params.put("requestedChannelMax", 456);
        params.put("requestedFrameMax", 789);
        params.put("requestedHeartbeat", 321);
        params.put("exclusiveConsumer", true);
        params.put("allowNullHeaders", true);
        RabbitMQEndpoint endpoint = createEndpoint(params);
        Assert.assertEquals("chrism", endpoint.getPassword());
        Assert.assertEquals("coldplay", endpoint.getUsername());
        Assert.assertEquals("queuey", endpoint.getQueue());
        Assert.assertEquals("vman", endpoint.getVhost());
        Assert.assertEquals("special.host", endpoint.getHostname());
        Assert.assertEquals(14123, endpoint.getPortNumber());
        Assert.assertEquals(515, endpoint.getThreadPoolSize());
        Assert.assertEquals(true, endpoint.isAutoAck());
        Assert.assertEquals(true, endpoint.isAutoDelete());
        Assert.assertEquals(true, endpoint.isDurable());
        Assert.assertEquals("topic", endpoint.getExchangeType());
        Assert.assertEquals(123, endpoint.getConnectionTimeout());
        Assert.assertEquals(456, endpoint.getRequestedChannelMax());
        Assert.assertEquals(789, endpoint.getRequestedFrameMax());
        Assert.assertEquals(321, endpoint.getRequestedHeartbeat());
        Assert.assertEquals(true, endpoint.isExclusiveConsumer());
        Assert.assertEquals(true, endpoint.isAllowNullHeaders());
    }

    @Test
    public void testConnectionFactoryRef() throws Exception {
        SimpleRegistry registry = new SimpleRegistry();
        ConnectionFactory connectionFactoryMock = Mockito.mock(ConnectionFactory.class);
        registry.put("connectionFactoryMock", connectionFactoryMock);
        CamelContext defaultContext = new org.apache.camel.impl.DefaultCamelContext(registry);
        Map<String, Object> params = new HashMap<>();
        params.put("connectionFactory", "#connectionFactoryMock");
        RabbitMQEndpoint endpoint = createEndpoint("rabbitmq:localhost/exchange", "localhost/exchange", params);
        Assert.assertSame(connectionFactoryMock, endpoint.getConnectionFactory());
    }
}

