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
package org.apache.nifi.processors.mqtt.integration;


import AbstractMessage.QOSType.EXACTLY_ONCE;
import ConsumeMQTT.PROP_QOS;
import ConsumeMQTT.REL_MESSAGE;
import io.moquette.proto.messages.PublishMessage;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.nifi.processors.mqtt.ConsumeMQTT;
import org.apache.nifi.processors.mqtt.common.TestConsumeMqttCommon;
import org.apache.nifi.util.MockFlowFile;
import org.junit.Test;


public class TestConsumeMqttSSL extends TestConsumeMqttCommon {
    @Test
    public void testRetainedQoS2() throws Exception {
        testRunner.setProperty(PROP_QOS, "2");
        testRunner.assertValid();
        PublishMessage testMessage = new PublishMessage();
        testMessage.setPayload(ByteBuffer.wrap("testMessage".getBytes()));
        testMessage.setTopicName("testTopic");
        testMessage.setDupFlag(false);
        testMessage.setQos(EXACTLY_ONCE);
        testMessage.setRetainFlag(true);
        internalPublish(testMessage);
        ConsumeMQTT consumeMQTT = ((ConsumeMQTT) (testRunner.getProcessor()));
        consumeMQTT.onScheduled(testRunner.getProcessContext());
        TestConsumeMqttCommon.reconnect(consumeMQTT, testRunner.getProcessContext());
        Thread.sleep(PUBLISH_WAIT_MS);
        testRunner.run(1, false, false);
        testRunner.assertTransferCount(REL_MESSAGE, 1);
        List<MockFlowFile> flowFiles = testRunner.getFlowFilesForRelationship(REL_MESSAGE);
        MockFlowFile flowFile = flowFiles.get(0);
        flowFile.assertContentEquals("testMessage");
        flowFile.assertAttributeEquals(ConsumeMQTT.BROKER_ATTRIBUTE_KEY, broker);
        flowFile.assertAttributeEquals(ConsumeMQTT.TOPIC_ATTRIBUTE_KEY, "testTopic");
        flowFile.assertAttributeEquals(ConsumeMQTT.QOS_ATTRIBUTE_KEY, "2");
        flowFile.assertAttributeEquals(ConsumeMQTT.IS_DUPLICATE_ATTRIBUTE_KEY, "false");
        flowFile.assertAttributeEquals(ConsumeMQTT.IS_RETAINED_ATTRIBUTE_KEY, "true");
    }
}

