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
package org.apache.activemq.transport.amqp;


import QoS.AT_LEAST_ONCE;
import Session.AUTO_ACKNOWLEDGE;
import java.util.Arrays;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.fusesource.mqtt.client.BlockingConnection;
import org.junit.Assert;
import org.junit.Test;


public class AmqpAndMqttTest {
    protected BrokerService broker;

    private TransportConnector amqpConnector;

    private TransportConnector mqttConnector;

    @Test(timeout = 60000)
    public void testFromMqttToAmqp() throws Exception {
        Connection amqp = createAmqpConnection();
        Session session = amqp.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(session.createTopic("topic://FOO"));
        final BlockingConnection mqtt = createMQTTConnection().blockingConnection();
        mqtt.connect();
        byte[] payload = bytes("Hello World");
        mqtt.publish("FOO", payload, AT_LEAST_ONCE, false);
        mqtt.disconnect();
        Message msg = consumer.receive((1000 * 5));
        Assert.assertNotNull(msg);
        Assert.assertTrue((msg instanceof BytesMessage));
        BytesMessage bmsg = ((BytesMessage) (msg));
        byte[] actual = new byte[((int) (bmsg.getBodyLength()))];
        bmsg.readBytes(actual);
        Assert.assertTrue(Arrays.equals(actual, payload));
        amqp.close();
    }
}

