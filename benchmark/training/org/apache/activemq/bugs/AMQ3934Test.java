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


import javax.management.openmbean.CompositeData;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ3934Test {
    private static final transient Logger LOG = LoggerFactory.getLogger(AMQ3934Test.class);

    private static BrokerService brokerService;

    private static String TEST_QUEUE = "testQueue";

    private static ActiveMQQueue queue = new ActiveMQQueue(AMQ3934Test.TEST_QUEUE);

    private static String BROKER_ADDRESS = "tcp://localhost:0";

    private ActiveMQConnectionFactory connectionFactory;

    private String connectionUri;

    private String messageID;

    @Test
    public void getMessage() throws Exception {
        final QueueViewMBean queueView = getProxyToQueueViewMBean();
        final CompositeData[] messages = queueView.browse();
        messageID = ((String) (messages[0].get("JMSMessageID")));
        Assert.assertNotNull(messageID);
        Assert.assertNotNull(queueView.getMessage(messageID));
        AMQ3934Test.LOG.debug(("Attempting to remove message ID: " + (messageID)));
        queueView.removeMessage(messageID);
        Assert.assertNull(queueView.getMessage(messageID));
    }
}

