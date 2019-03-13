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
package org.apache.activemq.bugs;


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ4814Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ4814Test.class);

    private static final String CONNECTION_URL = "tcp://127.0.0.1:0";

    private static final String KAHADB_DIRECTORY = "./target/activemq-data/";

    private BrokerService broker;

    private String connectionURI;

    @Test(timeout = 60000)
    public void testDurableTopicResourcesAreRemoved() throws Exception {
        AMQ4814Test.LOG.info("Test starting.");
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(connectionURI);
        for (int i = 0; i < 2; ++i) {
            AMQ4814Test.LOG.info("Test main loop starting iteration: {}", (i + 1));
            Connection connection = factory.createConnection();
            connection.setClientID("client_id");
            connection.start();
            for (int j = 0; j < 8; j++) {
                AMQ4814Test.LOG.info("Test sub loop starting iteration: {}", (j + 1));
                Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                String topicName = "durabletopic_" + j;
                String subscriberName = "subscriber_" + j;
                Topic topic = session.createTopic(topicName);
                TopicSubscriber subscriber = session.createDurableSubscriber(topic, subscriberName);
                subscriber.close();
                session.unsubscribe(subscriberName);
                session.close();
            }
            connection.stop();
            connection.close();
            connection = null;
            Thread.sleep(10);
        }
        Assert.assertEquals(0, broker.getSystemUsage().getMemoryUsage().getNumUsageListeners());
        AMQ4814Test.LOG.info("Test completed.");
    }
}

