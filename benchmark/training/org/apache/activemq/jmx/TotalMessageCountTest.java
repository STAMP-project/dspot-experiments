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
package org.apache.activemq.jmx;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author <a href="http://tmielke.blogspot.com">Torsten Mielke</a>
 */
public class TotalMessageCountTest {
    private static final Logger LOG = LoggerFactory.getLogger(TotalMessageCountTest.class);

    private BrokerService brokerService;

    private final String TESTQUEUE = "testQueue";

    private ActiveMQConnectionFactory connectionFactory;

    private final String BROKER_ADDRESS = "tcp://localhost:0";

    private final ActiveMQQueue queue = new ActiveMQQueue(TESTQUEUE);

    private String connectionUri;

    @Test
    public void testNegativeTotalMessageCount() throws Exception {
        TotalMessageCountTest.LOG.info("Running test testNegativeTotalMessageCount()");
        // send one msg first
        sendMessage();
        // restart the broker
        restartBroker();
        // receive one msg
        receiveMessage();
        // assert TotalMessageCount JMX property > 0
        long totalMessageCount = getTotalMessageCount();
        if (totalMessageCount < 0) {
            TotalMessageCountTest.LOG.error(("Unexpected negative TotalMessageCount: " + totalMessageCount));
        } else {
            TotalMessageCountTest.LOG.info(("TotalMessageCount: " + totalMessageCount));
        }
        Assert.assertTrue(("Non negative TotalMessageCount " + totalMessageCount), (totalMessageCount > (-1)));
        TotalMessageCountTest.LOG.info("Test testNegativeTotalMessageCount() completed.");
    }
}

