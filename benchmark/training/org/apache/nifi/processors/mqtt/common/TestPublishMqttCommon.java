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
package org.apache.nifi.processors.mqtt.common;


import PublishMQTT.PROP_CLEAN_SESSION;
import PublishMQTT.PROP_QOS;
import PublishMQTT.PROP_RETAIN;
import io.moquette.server.Server;
import org.apache.nifi.processors.mqtt.PublishMQTT;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;


public abstract class TestPublishMqttCommon {
    public Server MQTT_server;

    public TestRunner testRunner;

    public String topic;

    @Test
    public void testQoS0() {
        testRunner.setProperty(PROP_QOS, "0");
        testRunner.assertValid();
        String testMessage = "testMessage";
        testRunner.enqueue(testMessage.getBytes());
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(PublishMQTT.REL_SUCCESS);
        testRunner.assertTransferCount(PublishMQTT.REL_SUCCESS, 1);
        assertProvenanceEvents();
        verifyPublishedMessage(testMessage.getBytes(), 0, false);
    }

    @Test
    public void testQoS1() {
        testRunner.setProperty(PROP_QOS, "1");
        testRunner.assertValid();
        String testMessage = "testMessage";
        testRunner.enqueue(testMessage.getBytes());
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(PublishMQTT.REL_SUCCESS);
        assertProvenanceEvents();
        testRunner.assertTransferCount(PublishMQTT.REL_SUCCESS, 1);
        verifyPublishedMessage(testMessage.getBytes(), 1, false);
    }

    @Test
    public void testQoS2NotCleanSession() {
        // Publisher executes synchronously so the only time whether its Clean or Not matters is when the processor stops in the middle of the publishing
        testRunner.setProperty(PROP_QOS, "2");
        testRunner.setProperty(PROP_CLEAN_SESSION, MqttConstants.ALLOWABLE_VALUE_CLEAN_SESSION_FALSE);
        testRunner.assertValid();
        String testMessage = "testMessage";
        testRunner.enqueue(testMessage.getBytes());
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(PublishMQTT.REL_SUCCESS);
        assertProvenanceEvents();
        testRunner.assertTransferCount(PublishMQTT.REL_SUCCESS, 1);
        verifyPublishedMessage(testMessage.getBytes(), 2, false);
    }

    @Test
    public void testQoS2() {
        testRunner.setProperty(PROP_QOS, "2");
        testRunner.assertValid();
        String testMessage = "testMessage";
        testRunner.enqueue(testMessage.getBytes());
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(PublishMQTT.REL_SUCCESS);
        assertProvenanceEvents();
        testRunner.assertTransferCount(PublishMQTT.REL_SUCCESS, 1);
        verifyPublishedMessage(testMessage.getBytes(), 2, false);
    }

    @Test
    public void testRetainQoS2() {
        testRunner.setProperty(PROP_QOS, "2");
        testRunner.setProperty(PROP_RETAIN, "true");
        testRunner.assertValid();
        String testMessage = "testMessage";
        testRunner.enqueue(testMessage.getBytes());
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(PublishMQTT.REL_SUCCESS);
        assertProvenanceEvents();
        testRunner.assertTransferCount(PublishMQTT.REL_SUCCESS, 1);
        verifyPublishedMessage(testMessage.getBytes(), 2, true);
    }
}

