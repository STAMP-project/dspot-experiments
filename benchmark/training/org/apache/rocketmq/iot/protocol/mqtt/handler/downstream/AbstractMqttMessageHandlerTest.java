/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.iot.protocol.mqtt.handler.downstream;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import org.apache.rocketmq.iot.common.data.Message;
import org.apache.rocketmq.iot.connection.client.ClientManager;
import org.apache.rocketmq.iot.protocol.mqtt.data.MqttClient;
import org.apache.rocketmq.iot.protocol.mqtt.handler.MessageHandler;
import org.apache.rocketmq.iot.storage.subscription.SubscriptionStore;
import org.junit.Test;


public abstract class AbstractMqttMessageHandlerTest {
    protected ClientManager clientManager;

    protected SubscriptionStore subscriptionStore;

    protected EmbeddedChannel embeddedChannel;

    protected Message message;

    protected AbstractMqttMessageHandlerTest.MockHandler mockHandler;

    protected MessageHandler messageHandler;

    protected MqttClient client;

    class MockHandler extends SimpleChannelInboundHandler<Message> {
        private MessageHandler handler;

        MockHandler(MessageHandler handler) {
            this.handler = handler;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext context, Message message) throws Exception {
            handler.handleMessage(message);
        }
    }

    @Test
    public void testHandleMessage() {
        embeddedChannel.writeInbound(message);
        assertConditions();
    }
}

