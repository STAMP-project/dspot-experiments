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


import java.util.concurrent.TimeUnit;
import javax.jms.Queue;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Once the wire format is completed we can test against real persistence storage.
 */
public class AMQ6059Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ6059Test.class);

    private BrokerService broker;

    @Test
    public void testDLQRecovery() throws Exception {
        sendMessage(new ActiveMQQueue("QName"));
        TimeUnit.SECONDS.sleep(3);
        AMQ6059Test.LOG.info("### Check for expired message moving to DLQ.");
        Queue dlqQueue = ((Queue) (createDlqDestination()));
        verifyIsDlq(dlqQueue);
        final QueueViewMBean queueViewMBean = getProxyToQueue(dlqQueue.getQueueName());
        Assert.assertTrue("The message expired", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                AMQ6059Test.LOG.info("DLQ stats: Enqueues {}, Dispatches {}, Expired {}, Inflight {}", new Object[]{ queueViewMBean.getEnqueueCount(), queueViewMBean.getDispatchCount(), queueViewMBean.getExpiredCount(), queueViewMBean.getInFlightCount() });
                return (queueViewMBean.getEnqueueCount()) == 1;
            }
        }));
        verifyMessageIsRecovered(dlqQueue);
        restartBroker();
        verifyIsDlq(dlqQueue);
        verifyMessageIsRecovered(dlqQueue);
    }

    @Test
    public void testSetDlqFlag() throws Exception {
        final ActiveMQQueue toFlp = new ActiveMQQueue("QNameToFlip");
        sendMessage(toFlp);
        final QueueViewMBean queueViewMBean = getProxyToQueue(toFlp.getQueueName());
        Assert.assertFalse(queueViewMBean.isDLQ());
        queueViewMBean.setDLQ(true);
        Assert.assertTrue(queueViewMBean.isDLQ());
    }
}

