/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.bugs;


import javax.jms.MessageConsumer;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Ensure the tempPercentUsage JMX attribute decreases after temp store usage is decreased
 */
public class AMQ6459Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ6459Test.class);

    private static final String DESTINATION = "testQ1";

    private static final int MESSAGES_TO_SEND = 4000;

    private String TRANSPORT_URL = "tcp://0.0.0.0:0";

    BrokerService broker;

    @Test
    public void testTempPercentUsageDecreases() throws Exception {
        // create a topic subscriber, but do not consume messages
        MessageConsumer messageConsumer = createConsumer();
        // send X messages with with a sequence number number in the message property.
        sendMessages(AMQ6459Test.MESSAGES_TO_SEND);
        final BrokerViewMBean brokerView = getBrokerView(broker);
        AMQ6459Test.LOG.info(("tempPercentageUsage is " + (brokerView.getTempPercentUsage())));
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                AMQ6459Test.LOG.info(("tempPercentageUsage now " + (brokerView.getTempPercentUsage())));
                return (brokerView.getTempPercentUsage()) > 50;
            }
        });
        final int tempPercentUsageWithConsumer = brokerView.getTempPercentUsage();
        // ensure the tempPercentageUsage is at a high number
        Assert.assertTrue(" tempPercentageUsage ", (50 < tempPercentUsageWithConsumer));
        // close the consumer, releasing the temp storage
        messageConsumer.close();
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                AMQ6459Test.LOG.info(("tempPercentageUsage now (after consumer closed) " + (brokerView.getTempPercentUsage())));
                return tempPercentUsageWithConsumer > (brokerView.getTempPercentUsage());
            }
        });
        Assert.assertTrue("tempPercentageUsage should be less after consumer has closed", (tempPercentUsageWithConsumer > (brokerView.getTempPercentUsage())));
    }
}

