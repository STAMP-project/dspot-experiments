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
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test for AMQ-6000 issue with pause / resume feature.
 */
public class AMQ6000Test {
    private static Logger LOG = LoggerFactory.getLogger(AMQ6000Test.class);

    private ActiveMQConnection connection;

    private BrokerService broker;

    private String connectionUri;

    @Rule
    public TestName name = new TestName();

    @Test(timeout = 30000)
    public void testResumeNotDispatching() throws Exception {
        sendMessage();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(name.getMethodName());
        QueueViewMBean queueView = getProxyToQueue(name.getMethodName());
        AMQ6000Test.LOG.info("Pausing Queue");
        queueView.pause();
        MessageConsumer consumer = session.createConsumer(destination);
        Assert.assertNull(consumer.receive(100));
        AMQ6000Test.LOG.info("Resuming Queue");
        queueView.resume();
        Assert.assertNotNull(consumer.receive(2000));
    }
}

