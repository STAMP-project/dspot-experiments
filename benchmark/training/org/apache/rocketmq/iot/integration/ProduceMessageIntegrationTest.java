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
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import java.util.List;
import org.apache.rocketmq.iot.connection.client.Client;
import org.apache.rocketmq.iot.connection.client.ClientManager;
import org.apache.rocketmq.iot.protocol.mqtt.data.MqttClient;
import org.apache.rocketmq.iot.protocol.mqtt.data.Subscription;
import org.apache.rocketmq.iot.protocol.mqtt.handler.MessageDispatcher;
import org.apache.rocketmq.iot.protocol.mqtt.handler.downstream.impl.MqttConnectMessageHandler;
import org.apache.rocketmq.iot.protocol.mqtt.handler.downstream.impl.MqttMessageForwarder;
import org.apache.rocketmq.iot.storage.subscription.SubscriptionStore;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ProduceMessageIntegrationTest {
    private final String producerId = "test-client";

    private final String topicName = "test-topic";

    private final int packetId = 1;

    private EmbeddedChannel producerChannel;

    private MessageDispatcher dispatcher;

    private ClientManager clientManager;

    private SubscriptionStore subscriptionStore;

    private MqttConnectMessageHandler mqttConnectMessageHandler;

    private MqttMessageForwarder mqttMessageForwarder;

    private List<Subscription> mockedSubscriptions;

    private MqttClient mockedConsuemr;

    private ChannelHandlerContext mockedConsumerCtx;

    private EmbeddedChannel consumerChannel = new EmbeddedChannel();

    @Test
    public void test() {
        /* handle the CONNECT message */
        MqttConnectMessage connectMessage = getConnectMessage();
        producerChannel.writeInbound(connectMessage);
        MqttConnAckMessage connAckMessage = producerChannel.readOutbound();
        Client client = clientManager.get(producerChannel);
        Assert.assertNotNull(client);
        Assert.assertEquals(producerId, client.getId());
        Assert.assertEquals(producerChannel, client.getCtx().channel());
        Assert.assertTrue(client.isConnected());
        Assert.assertTrue(producerChannel.isOpen());
        Assert.assertEquals(CONNECTION_ACCEPTED, connAckMessage.variableHeader().connectReturnCode());
        producerChannel.releaseInbound();
        /* handle the PUBLISH message when there is no online Consumers */
        MqttPublishMessage publishMessage = getMqttPublishMessage();
        producerChannel.writeInbound(publishMessage);
        MqttMessage pubackMessage = producerChannel.readOutbound();
        /* qos 0 should have no PUBACK message */
        Assert.assertNull(pubackMessage);
        /* the message would be discarded simply because there is no Consumers
        and the topic should be created
         */
        Assert.assertTrue(subscriptionStore.hasTopic(topicName));
        producerChannel.releaseInbound();
        /* handle the PUBLISH message when there are online Consumers */
        Mockito.when(subscriptionStore.get(topicName)).thenReturn(mockedSubscriptions);
        publishMessage = getMqttPublishMessage();
        byte[] publishMessagePayload = new byte[publishMessage.payload().readableBytes()];
        publishMessage.payload().getBytes(0, publishMessagePayload);
        producerChannel.writeInbound(publishMessage);
        MqttPublishMessage forwardedMessage = consumerChannel.readOutbound();
        byte[] forwardedMessagePayload = new byte[forwardedMessage.payload().readableBytes()];
        forwardedMessage.payload().getBytes(0, forwardedMessagePayload);
        Assert.assertNotNull(forwardedMessage);
        Assert.assertEquals(publishMessage.variableHeader().topicName(), forwardedMessage.variableHeader().topicName());
        Assert.assertEquals(publishMessage.variableHeader().packetId(), forwardedMessage.variableHeader().packetId());
        Assert.assertArrayEquals(publishMessagePayload, forwardedMessagePayload);
    }
}

