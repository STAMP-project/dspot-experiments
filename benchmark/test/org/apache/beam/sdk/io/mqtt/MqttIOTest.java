/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.mqtt;


import MqttIO.ConnectionConfiguration;
import MqttIO.MqttCheckpointMark;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import org.apache.activemq.broker.BrokerService;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests of {@link MqttIO}.
 */
public class MqttIOTest {
    private static final Logger LOG = LoggerFactory.getLogger(MqttIOTest.class);

    private BrokerService brokerService;

    private int port;

    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create();

    /**
     * Test for BEAM-3282: this test should not timeout.
     */
    @Test(timeout = 30 * 1000)
    public void testReceiveWithTimeoutAndNoData() throws Exception {
        pipeline.apply(MqttIO.read().withConnectionConfiguration(ConnectionConfiguration.create(("tcp://localhost:" + (port)), "READ_TOPIC", "READ_PIPELINE")).withMaxReadTime(Duration.standardSeconds(2)));
        // should stop before the test timeout
        pipeline.run();
    }

    @Test
    public void testWrite() throws Exception {
        final int numberOfTestMessages = 200;
        MQTT client = new MQTT();
        client.setHost(("tcp://localhost:" + (port)));
        final BlockingConnection connection = client.blockingConnection();
        connection.connect();
        connection.subscribe(new Topic[]{ new Topic(Buffer.utf8("WRITE_TOPIC"), QoS.EXACTLY_ONCE) });
        final Set<String> messages = new ConcurrentSkipListSet<>();
        Thread subscriber = new Thread(() -> {
            try {
                for (int i = 0; i < numberOfTestMessages; i++) {
                    Message message = connection.receive();
                    messages.add(new String(message.getPayload(), StandardCharsets.UTF_8));
                    message.ack();
                }
            } catch (Exception e) {
                MqttIOTest.LOG.error("Can't receive message", e);
            }
        });
        subscriber.start();
        ArrayList<byte[]> data = new ArrayList<>();
        for (int i = 0; i < numberOfTestMessages; i++) {
            data.add(("Test " + i).getBytes(StandardCharsets.UTF_8));
        }
        pipeline.apply(Create.of(data)).apply(MqttIO.write().withConnectionConfiguration(ConnectionConfiguration.create(("tcp://localhost:" + (port)), "WRITE_TOPIC")));
        pipeline.run();
        subscriber.join();
        connection.disconnect();
        Assert.assertEquals(numberOfTestMessages, messages.size());
        for (int i = 0; i < numberOfTestMessages; i++) {
            Assert.assertTrue(messages.contains(("Test " + i)));
        }
    }

    @Test
    public void testReadObject() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        MqttIO.MqttCheckpointMark cp1 = new MqttIO.MqttCheckpointMark(UUID.randomUUID().toString());
        out.writeObject(cp1);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        MqttIO.MqttCheckpointMark cp2 = ((MqttIO.MqttCheckpointMark) (in.readObject()));
        // there should be no bytes left in the stream
        Assert.assertEquals(0, in.available());
        // the number of messages of the decoded checkpoint should be zero
        Assert.assertEquals(0, cp2.messages.size());
        Assert.assertEquals(cp1.clientId, cp2.clientId);
        Assert.assertEquals(cp1.oldestMessageTimestamp, cp2.oldestMessageTimestamp);
    }
}

