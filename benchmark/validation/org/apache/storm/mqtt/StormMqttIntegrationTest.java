/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.mqtt;


import java.io.Serializable;
import java.net.URI;
import org.apache.activemq.broker.BrokerService;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalCluster.LocalTopology;
import org.apache.storm.mqtt.common.MqttOptions;
import org.apache.storm.mqtt.common.MqttPublisher;
import org.apache.storm.mqtt.spout.MqttSpout;
import org.apache.storm.testing.IntegrationTest;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@IntegrationTest
public class StormMqttIntegrationTest implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(StormMqttIntegrationTest.class);

    private static final String TEST_TOPIC = "/mqtt-topology";

    private static final String RESULT_TOPIC = "/integration-result";

    private static final String RESULT_PAYLOAD = "Storm MQTT Spout";

    static boolean spoutActivated = false;

    private static BrokerService broker;

    @Test
    public void testMqttTopology() throws Exception {
        MQTT client = new MQTT();
        client.setTracer(new MqttLogger());
        URI uri = URI.create("tcp://localhost:1883");
        client.setHost(uri);
        client.setClientId("MQTTSubscriber");
        client.setCleanSession(false);
        BlockingConnection connection = client.blockingConnection();
        connection.connect();
        Topic[] topics = new Topic[]{ new Topic("/integration-result", QoS.AT_LEAST_ONCE) };
        byte[] qoses = connection.subscribe(topics);
        try (LocalCluster cluster = new LocalCluster();LocalTopology topo = cluster.submitTopology("test", new Config(), buildMqttTopology())) {
            StormMqttIntegrationTest.LOG.info("topology started");
            while (!(StormMqttIntegrationTest.spoutActivated)) {
                Thread.sleep(500);
            } 
            // publish a retained message to the broker
            MqttOptions options = new MqttOptions();
            options.setCleanConnection(false);
            MqttPublisher publisher = new MqttPublisher(options, true);
            publisher.connectMqtt("MqttPublisher");
            publisher.publish(new MqttMessage(StormMqttIntegrationTest.TEST_TOPIC, "test".getBytes()));
            StormMqttIntegrationTest.LOG.info("published message");
            Message message = connection.receive();
            StormMqttIntegrationTest.LOG.info("Message recieved on topic: {}", message.getTopic());
            StormMqttIntegrationTest.LOG.info("Payload: {}", new String(message.getPayload()));
            message.ack();
            Assert.assertArrayEquals(message.getPayload(), StormMqttIntegrationTest.RESULT_PAYLOAD.getBytes());
            Assert.assertEquals(message.getTopic(), StormMqttIntegrationTest.RESULT_TOPIC);
        }
    }

    public static class TestSpout extends MqttSpout {
        public TestSpout(MqttMessageMapper type, MqttOptions options) {
            super(type, options);
        }

        @Override
        public void activate() {
            super.activate();
            StormMqttIntegrationTest.LOG.info("Spout activated.");
            StormMqttIntegrationTest.spoutActivated = true;
        }
    }
}

