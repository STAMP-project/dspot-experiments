/**
 * Copyright 2017 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.mqtt;


import CharsetUtil.UTF_8;
import MqttMessageBuilders.ConnectBuilder;
import org.junit.Assert;
import org.junit.Test;


public class MqttConnectPayloadTest {
    @Test
    public void testNullWillMessage() throws Exception {
        String clientIdentifier = "clientIdentifier";
        String willTopic = "willTopic";
        byte[] willMessage = null;
        String userName = "userName";
        byte[] password = "password".getBytes(UTF_8);
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(clientIdentifier, willTopic, willMessage, userName, password);
        Assert.assertNull(mqttConnectPayload.willMessageInBytes());
        Assert.assertNull(mqttConnectPayload.willMessage());
    }

    @Test
    public void testNullPassword() throws Exception {
        String clientIdentifier = "clientIdentifier";
        String willTopic = "willTopic";
        byte[] willMessage = "willMessage".getBytes(UTF_8);
        String userName = "userName";
        byte[] password = null;
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(clientIdentifier, willTopic, willMessage, userName, password);
        Assert.assertNull(mqttConnectPayload.passwordInBytes());
        Assert.assertNull(mqttConnectPayload.password());
    }

    @Test
    public void testBuilderNullPassword() throws Exception {
        MqttMessageBuilders.ConnectBuilder builder = new MqttMessageBuilders.ConnectBuilder();
        builder.password(((String) (null)));
        MqttConnectPayload mqttConnectPayload = builder.build().payload();
        Assert.assertNull(mqttConnectPayload.passwordInBytes());
        Assert.assertNull(mqttConnectPayload.password());
        builder = new MqttMessageBuilders.ConnectBuilder();
        builder.password(((byte[]) (null)));
        mqttConnectPayload = builder.build().payload();
        Assert.assertNull(mqttConnectPayload.passwordInBytes());
        Assert.assertNull(mqttConnectPayload.password());
    }

    @Test
    public void testBuilderNullWillMessage() throws Exception {
        MqttMessageBuilders.ConnectBuilder builder = new MqttMessageBuilders.ConnectBuilder();
        builder.willMessage(((String) (null)));
        MqttConnectPayload mqttConnectPayload = builder.build().payload();
        Assert.assertNull(mqttConnectPayload.willMessageInBytes());
        Assert.assertNull(mqttConnectPayload.willMessage());
        builder = new MqttMessageBuilders.ConnectBuilder();
        builder.willMessage(((byte[]) (null)));
        mqttConnectPayload = builder.build().payload();
        Assert.assertNull(mqttConnectPayload.willMessageInBytes());
        Assert.assertNull(mqttConnectPayload.willMessage());
    }
}

