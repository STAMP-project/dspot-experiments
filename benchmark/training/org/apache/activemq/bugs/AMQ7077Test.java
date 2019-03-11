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
import javax.management.ObjectName;
import javax.management.openmbean.TabularData;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.AbortSlowConsumerStrategyViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ7077Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ7077Test.class);

    private BrokerService brokerService;

    private String connectionUri;

    @Test
    public void testAdvisoryOnSlowAckDetection() throws Exception {
        Connection connection = createConnectionFactory().createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("DD");
        MessageConsumer advisoryConsumer = session.createConsumer(AdvisorySupport.getSlowConsumerAdvisoryTopic(destination));
        MessageConsumer consumer = session.createConsumer(destination);
        // will be idle and can get removed but will be marked slow and now produce an advisory
        Message message = advisoryConsumer.receive(10000);
        if (message == null) {
            message = advisoryConsumer.receive(2000);
        }
        Assert.assertNotNull("Got advisory", message);
        connection.close();
        QueueViewMBean queue = getProxyToQueue(getQueueName());
        ObjectName slowConsumerPolicyMBeanName = queue.getSlowConsumerStrategy();
        Assert.assertNotNull(slowConsumerPolicyMBeanName);
        AbortSlowConsumerStrategyViewMBean abortPolicy = ((AbortSlowConsumerStrategyViewMBean) (brokerService.getManagementContext().newProxyInstance(slowConsumerPolicyMBeanName, AbortSlowConsumerStrategyViewMBean.class, true)));
        Assert.assertTrue("slow list is gone on remove", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                TabularData slowOnes = abortPolicy.getSlowConsumers();
                AMQ7077Test.LOG.info(("slow ones:" + slowOnes));
                return (slowOnes.size()) == 0;
            }
        }));
    }
}

