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
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.junit.Test;


public class AMQ5035Test {
    private static final String CLIENT_ID = "amq-test-client-id";

    private static final String DURABLE_SUB_NAME = "testDurable";

    private final String xbean = "xbean:";

    private final String confBase = "src/test/resources/org/apache/activemq/bugs/amq5035";

    private static BrokerService brokerService;

    private String connectionUri;

    @Test
    public void testFoo() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(connectionUri);
        Connection connection = factory.createConnection();
        connection.setClientID(AMQ5035Test.CLIENT_ID);
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("Test.Topic");
        MessageConsumer consumer = session.createDurableSubscriber(topic, AMQ5035Test.DURABLE_SUB_NAME);
        consumer.close();
        BrokerViewMBean brokerView = getBrokerView(AMQ5035Test.DURABLE_SUB_NAME);
        brokerView.destroyDurableSubscriber(AMQ5035Test.CLIENT_ID, AMQ5035Test.DURABLE_SUB_NAME);
    }
}

