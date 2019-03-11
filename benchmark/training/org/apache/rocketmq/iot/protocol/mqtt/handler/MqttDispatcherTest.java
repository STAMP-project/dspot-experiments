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
package org.apache.rocketmq.iot.protocol.mqtt.handler;


import Message.Type;
import Message.Type.MQTT_CONNECT;
import Message.Type.MQTT_DISCONNECT;
import java.util.Map;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.rocketmq.iot.common.data.Message;
import org.apache.rocketmq.iot.connection.client.ClientManager;
import org.junit.Assert;
import org.junit.Test;


public class MqttDispatcherTest {
    private MessageDispatcher messageDispatcher;

    private ClientManager clientManager;

    private MessageHandler mockedConnectMessageHandler;

    private MessageHandler mockedDisconnectMessageHandler;

    @Test
    public void testRegisterHandler() throws IllegalAccessException {
        messageDispatcher.registerHandler(MQTT_CONNECT, mockedConnectMessageHandler);
        messageDispatcher.registerHandler(MQTT_DISCONNECT, mockedDisconnectMessageHandler);
        Map<Message.Type, MessageHandler> type2handler = ((Map<Message.Type, MessageHandler>) (FieldUtils.getField(MessageDispatcher.class, "type2handler", true).get(messageDispatcher)));
        Assert.assertEquals(mockedConnectMessageHandler, type2handler.get(MQTT_CONNECT));
        Assert.assertEquals(mockedDisconnectMessageHandler, type2handler.get(MQTT_DISCONNECT));
    }
}

