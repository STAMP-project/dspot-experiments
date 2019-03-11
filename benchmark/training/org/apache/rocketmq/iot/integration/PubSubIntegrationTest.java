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
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import java.util.ArrayList;
import java.util.List;
import org.apache.rocketmq.iot.connection.client.ClientManager;
import org.apache.rocketmq.iot.protocol.mqtt.data.MqttClient;
import org.apache.rocketmq.iot.protocol.mqtt.handler.MessageDispatcher;
import org.apache.rocketmq.iot.protocol.mqtt.handler.downstream.impl.MqttConnectMessageHandler;
import org.apache.rocketmq.iot.protocol.mqtt.handler.downstream.impl.MqttMessageForwarder;
import org.apache.rocketmq.iot.protocol.mqtt.handler.downstream.impl.MqttSubscribeMessageHandler;
import org.apache.rocketmq.iot.storage.subscription.SubscriptionStore;
import org.junit.Assert;
import org.junit.Test;


public class PubSubIntegrationTest {
    private final String producerId = "test-producer-id";

    private final String consumerId = "test-consumer-id";

    private final String topicName = "test-topic";

    private final int producerPublishId = 2;

    private final int consumerSubscribeId = 4;

    private ClientManager clientManager;

    private SubscriptionStore subscriptionStore;

    private MqttClient producer;

    private MqttClient consumer;

    private EmbeddedChannel producerChannel;

    private EmbeddedChannel consumerChannel;

    private MessageDispatcher messageDispatcher;

    private MqttConnectMessageHandler mqttConnectMessageHandler;

    private MqttMessageForwarder mqttMessageForwarder;

    private MqttSubscribeMessageHandler mqttSubscribeMessageHandler;

    private List<MqttTopicSubscription> topicSubscriptions = new ArrayList<>();

    @Test
    public void test() {
        /* the consumer connect and subscribe */
        /* handle CONNECT message from consumer */
        MqttConnectMessage consuemrConnectMessage = getConnectMessage(consumerId);
        consumerChannel.writeInbound(consuemrConnectMessage);
        MqttClient managedConsuemr = ((MqttClient) (clientManager.get(consumerChannel)));
        MqttConnAckMessage consumerConnAckMessage = consumerChannel.readOutbound();
        Assert.assertNotNull(managedConsuemr);
        Assert.assertEquals(consumerId, managedConsuemr.getId());
        Assert.assertTrue(managedConsuemr.isConnected());
        Assert.assertTrue(consumerChannel.isOpen());
        Assert.assertEquals(consumerId, consuemrConnectMessage.payload().clientIdentifier());
        Assert.assertEquals(CONNECTION_ACCEPTED, consumerConnAckMessage.variableHeader().connectReturnCode());
        Assert.assertEquals(consuemrConnectMessage.variableHeader().isCleanSession(), consumerConnAckMessage.variableHeader().isSessionPresent());
        consumerChannel.releaseInbound();
        /* handle SUBSCRIBE message from consumer */
        MqttSubscribeMessage consumerSubscribeMessage = getMqttSubscribeMessage();
        consumerChannel.writeInbound(consumerSubscribeMessage);
        MqttSubAckMessage consuemrSubAckMessage = consumerChannel.readOutbound();
        Assert.assertNotNull(consuemrSubAckMessage);
        Assert.assertEquals(consumerSubscribeMessage.variableHeader().messageId(), consuemrSubAckMessage.variableHeader().messageId());
        Assert.assertEquals(topicSubscriptions.size(), consuemrSubAckMessage.payload().grantedQoSLevels().size());
        consumerChannel.releaseInbound();
        /* the producer publish message to the topic */
        /* handle CONNECT message from producer */
        MqttConnectMessage producerConnectMessage = getConnectMessage(producerId);
        producerChannel.writeInbound(producerConnectMessage);
        MqttConnAckMessage producerConnAckMessage = producerChannel.readOutbound();
        MqttClient managedProducer = ((MqttClient) (clientManager.get(producerChannel)));
        Assert.assertNotNull(managedProducer);
        Assert.assertNotNull(producerConnAckMessage);
        Assert.assertTrue(managedProducer.isConnected());
        Assert.assertTrue(producerChannel.isOpen());
        Assert.assertEquals(producerId, managedProducer.getId());
        Assert.assertEquals(CONNECTION_ACCEPTED, producerConnAckMessage.variableHeader().connectReturnCode());
        Assert.assertEquals(producerConnectMessage.variableHeader().isCleanSession(), producerConnAckMessage.variableHeader().isSessionPresent());
        producerChannel.releaseInbound();
        /* handle PUBLISH message from producer */
        MqttPublishMessage producerPublishMessage = getMqttPublishMessage();
        byte[] expectedPayload = new byte[producerPublishMessage.payload().readableBytes()];
        producerPublishMessage.payload().getBytes(0, expectedPayload);
        producerChannel.writeInbound(producerPublishMessage);
        MqttPublishMessage consumerReceivedMessage = consumerChannel.readOutbound();
        byte[] actualPayload = new byte[consumerReceivedMessage.payload().readableBytes()];
        consumerReceivedMessage.payload().getBytes(0, actualPayload);
        Assert.assertNotNull(consumerReceivedMessage);
        Assert.assertEquals(producerPublishMessage.variableHeader().packetId(), consumerReceivedMessage.variableHeader().packetId());
        Assert.assertEquals(producerPublishMessage.variableHeader().topicName(), consumerReceivedMessage.variableHeader().topicName());
        Assert.assertArrayEquals(expectedPayload, actualPayload);
    }
}

