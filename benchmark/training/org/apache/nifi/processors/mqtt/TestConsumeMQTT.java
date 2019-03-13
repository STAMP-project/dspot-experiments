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
package org.apache.nifi.processors.mqtt;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.BlockingQueue;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processors.mqtt.common.MQTTQueueMessage;
import org.apache.nifi.processors.mqtt.common.MqttTestClient;
import org.apache.nifi.processors.mqtt.common.TestConsumeMqttCommon;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.apache.nifi.processors.mqtt.common.MqttTestClient.ConnectType.Subscriber;


public class TestConsumeMQTT extends TestConsumeMqttCommon {
    public MqttTestClient mqttTestClient;

    public class UnitTestableConsumeMqtt extends ConsumeMQTT {
        public UnitTestableConsumeMqtt() {
            super();
        }

        @Override
        public IMqttClient createMqttClient(String broker, String clientID, MemoryPersistence persistence) throws MqttException {
            mqttTestClient = new MqttTestClient(broker, clientID, Subscriber);
            return mqttTestClient;
        }
    }

    /**
     * If the session.commit() fails, we should not remove the unprocessed message
     */
    @Test
    public void testMessageNotConsumedOnCommitFail() throws IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        testRunner.run(1, false);
        ConsumeMQTT processor = ((ConsumeMQTT) (testRunner.getProcessor()));
        MQTTQueueMessage mock = Mockito.mock(MQTTQueueMessage.class);
        Mockito.when(mock.getPayload()).thenReturn(new byte[0]);
        Mockito.when(mock.getTopic()).thenReturn("testTopic");
        BlockingQueue<MQTTQueueMessage> mqttQueue = TestConsumeMqttCommon.getMqttQueue(processor);
        mqttQueue.add(mock);
        try {
            ProcessSession session = testRunner.getProcessSessionFactory().createSession();
            TestConsumeMqttCommon.transferQueue(processor, ((ProcessSession) (Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ ProcessSession.class }, ( proxy, method, args) -> {
                if (method.getName().equals("commit")) {
                    throw new RuntimeException();
                } else {
                    return method.invoke(session, args);
                }
            }))));
            Assert.fail("Expected runtime exception");
        } catch (InvocationTargetException e) {
            Assert.assertTrue(("Expected generic runtime exception, not " + e), ((e.getCause()) instanceof RuntimeException));
        }
        Assert.assertTrue("Expected mqttQueue to contain uncommitted message.", mqttQueue.contains(mock));
    }
}

