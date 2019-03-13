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
package org.apache.activemq.transport.mqtt;


import QoS.AT_LEAST_ONCE;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that the maxFrameSize configuration value is applied across the transports.
 */
@RunWith(Parameterized.class)
public class MQTTMaxFrameSizeTest extends MQTTTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(MQTTMaxFrameSizeTest.class);

    private final int maxFrameSize;

    public MQTTMaxFrameSizeTest(String connectorScheme, boolean useSSL, int maxFrameSize) {
        super(connectorScheme, useSSL);
        this.maxFrameSize = maxFrameSize;
    }

    @Test(timeout = 30000)
    public void testFrameSizeToLargeClosesConnection() throws Exception {
        MQTTMaxFrameSizeTest.LOG.debug("Starting test on connector {} for frame size: {}", getProtocolScheme(), maxFrameSize);
        MQTT mqtt = createMQTTConnection();
        mqtt.setClientId(getTestName());
        mqtt.setKeepAlive(((short) (10)));
        mqtt.setVersion("3.1.1");
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        final int payloadSize = (maxFrameSize) + 100;
        byte[] payload = new byte[payloadSize];
        for (int i = 0; i < payloadSize; ++i) {
            payload[i] = 42;
        }
        try {
            connection.publish(getTopicName(), payload, QoS.AT_LEAST_ONCE, false);
            Assert.fail("should have thrown an exception");
        } catch (Exception ex) {
        } finally {
            connection.disconnect();
        }
    }

    @Test(timeout = 30000)
    public void testFrameSizeNotExceededWorks() throws Exception {
        MQTTMaxFrameSizeTest.LOG.debug("Starting test on connector {} for frame size: {}", getProtocolScheme(), maxFrameSize);
        MQTT mqtt = createMQTTConnection();
        mqtt.setClientId(getTestName());
        mqtt.setKeepAlive(((short) (10)));
        mqtt.setVersion("3.1.1");
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        final int payloadSize = (maxFrameSize) / 2;
        byte[] payload = new byte[payloadSize];
        for (int i = 0; i < payloadSize; ++i) {
            payload[i] = 42;
        }
        try {
            connection.publish(getTopicName(), payload, QoS.AT_LEAST_ONCE, false);
        } catch (Exception ex) {
            Assert.fail("should not have thrown an exception");
        } finally {
            connection.disconnect();
        }
    }
}

