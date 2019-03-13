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
package org.apache.rocketmq.iot.integration;


import MqttConnectReturnCode.CONNECTION_ACCEPTED;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import java.util.List;
import org.apache.rocketmq.iot.connection.client.Client;
import org.apache.rocketmq.iot.connection.client.ClientManager;
import org.apache.rocketmq.iot.protocol.mqtt.data.MqttClient;
import org.apache.rocketmq.iot.protocol.mqtt.data.Subscription;
import org.apache.rocketmq.iot.protocol.mqtt.handler.MessageDispatcher;
import org.apache.rocketmq.iot.protocol.mqtt.handler.downstream.impl.MqttConnectMessageHandler;
import org.apache.rocketmq.iot.protocol.mqtt.handler.downstream.impl.MqttMessageForwarder;
import org.apache.rocketmq.iot.protocol.mqtt.handler.downstream.impl.MqttSubscribeMessageHandler;
import org.apache.rocketmq.iot.storage.subscription.SubscriptionStore;
import org.junit.Assert;
import org.junit.Test;


public class ConsumeMessageIntegrationTest {
    private ClientManager clientManager;

    private SubscriptionStore subscriptionStore;

    private MessageDispatcher messageDispatcher;

    private MqttConnectMessageHandler mqttConnectMessageHandler;

    private MqttSubscribeMessageHandler mqttSubscribeMessageHandler;

    private MqttMessageForwarder mqttMessageForwarder;

    private EmbeddedChannel embeddedChannel;

    private MqttClient consuemr;

    private ChannelHandlerContext consuermCtx;

    private final String consumerId = "test-consumer-id";

    private final String topicName = "test-topic";

    private final int subscribePacketId = 1;

    private final int publishPakcetId = 2;

    private List<MqttTopicSubscription> subscriptions;

    @Test
    public void test() {
        /* handle the CONNECT message */
        MqttConnectMessage connectMessage = getConnectMessage();
        embeddedChannel.writeInbound(connectMessage);
        MqttConnAckMessage connAckMessage = embeddedChannel.readOutbound();
        Client client = clientManager.get(embeddedChannel);
        Assert.assertNotNull(client);
        Assert.assertTrue(consumerId.equals(client.getId()));
        Assert.assertEquals(consumerId, client.getId());
        Assert.assertTrue(client.isConnected());
        Assert.assertEquals(embeddedChannel, client.getCtx().channel());
        Assert.assertTrue(client.isConnected());
        Assert.assertTrue(embeddedChannel.isOpen());
        Assert.assertEquals(CONNECTION_ACCEPTED, connAckMessage.variableHeader().connectReturnCode());
        embeddedChannel.releaseInbound();
        /* handle the SUBSCRIBE message */
        MqttSubscribeMessage subscribeMessage = getMqttSubscribeMessage();
        embeddedChannel.writeInbound(subscribeMessage);
        List<Subscription> subscriptions = subscriptionStore.get(topicName);
        List<String> topics = subscriptionStore.getTopics(topicName);
        MqttSubAckMessage ackMessage = embeddedChannel.readOutbound();
        Assert.assertNotNull(subscriptionStore.get(topicName));
        Assert.assertNotNull(subscriptions);
        Assert.assertNotNull(ackMessage);
        Assert.assertEquals(subscribePacketId, ackMessage.variableHeader().messageId());
        Assert.assertTrue(topics.contains(topicName));
        Assert.assertTrue(isClientInSubscriptions(subscriptions, client));
        embeddedChannel.releaseInbound();
        /* send message to the client */
        MqttPublishMessage publishMessage = getMqttPublishMessage();
        byte[] expectedPayload = new byte[publishMessage.payload().readableBytes()];
        publishMessage.payload().getBytes(0, expectedPayload);
        embeddedChannel.writeInbound(publishMessage);
        MqttPublishMessage receivedMessage = embeddedChannel.readOutbound();
        byte[] actualPayload = new byte[receivedMessage.payload().readableBytes()];
        receivedMessage.payload().getBytes(0, actualPayload);
        Assert.assertEquals(publishMessage.variableHeader().packetId(), receivedMessage.variableHeader().packetId());
        Assert.assertEquals(publishMessage.variableHeader().topicName(), receivedMessage.variableHeader().topicName());
        Assert.assertArrayEquals(expectedPayload, actualPayload);
    }
}

