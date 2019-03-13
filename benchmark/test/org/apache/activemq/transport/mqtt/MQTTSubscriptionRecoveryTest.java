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


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that all previous QoS 2 subscriptions are recovered on Broker restart.
 */
@RunWith(Parameterized.class)
public class MQTTSubscriptionRecoveryTest extends MQTTTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(MQTTSubscriptionRecoveryTest.class);

    protected boolean defaultStrategy = false;

    public MQTTSubscriptionRecoveryTest(String subscriptionStrategy, boolean defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

    @Test
    public void testDurableSubscriptionsAreRecovered() throws Exception {
        MqttClient connection = createClient(getTestName());
        final String[] topics = new String[]{ "TopicA/", "TopicB/", "TopicC/" };
        for (int i = 0; i < (topics.length); i++) {
            MQTTSubscriptionRecoveryTest.LOG.debug("Subscribing to Topic:{}", topics[i]);
            connection.subscribe(topics[i], MQTTTestSupport.EXACTLY_ONCE);
        }
        assertStatsForConnectedClient(topics.length);
        disconnect(connection);
        assertStatsForDisconnectedClient(topics.length);
        restartBroker();
        assertStatsForDisconnectedClient(topics.length);
        connection = createClient(getTestName());
        assertStatsForConnectedClient(topics.length);
    }
}

