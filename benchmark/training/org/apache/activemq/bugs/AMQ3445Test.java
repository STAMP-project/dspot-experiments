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


import javax.jms.ConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.junit.Assert;
import org.junit.Test;


public class AMQ3445Test {
    private ConnectionFactory connectionFactory;

    private BrokerService broker;

    private String connectionUri;

    private final String queueName = "Consumer.MyApp.VirtualTopic.FOO";

    private final String topicName = "VirtualTopic.FOO";

    @Test
    public void testJDBCRetiansDestinationAfterRestart() throws Exception {
        broker.getAdminView().addQueue(queueName);
        broker.getAdminView().addTopic(topicName);
        Assert.assertTrue(findDestination(queueName, false));
        Assert.assertTrue(findDestination(topicName, true));
        QueueViewMBean queue = getProxyToQueueViewMBean();
        Assert.assertEquals(0, queue.getQueueSize());
        restartBroker();
        Assert.assertTrue(findDestination(queueName, false));
        queue = getProxyToQueueViewMBean();
        Assert.assertEquals(0, queue.getQueueSize());
        sendMessage();
        restartBroker();
        Assert.assertTrue(findDestination(queueName, false));
        queue = getProxyToQueueViewMBean();
        Assert.assertEquals(1, queue.getQueueSize());
        sendMessage();
        Assert.assertEquals(2, queue.getQueueSize());
        restartBroker();
        Assert.assertTrue(findDestination(queueName, false));
        queue = getProxyToQueueViewMBean();
        Assert.assertEquals(2, queue.getQueueSize());
    }
}

