/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.bugs;


import java.util.ArrayList;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// https://issues.apache.org/jira/browse/AMQ-6128
public class JMSQueueBrowserPriorityTest extends TestCase {
    static final Logger LOG = LoggerFactory.getLogger(JMSQueueBrowserPriorityTest.class);

    private static final String TEST_AMQ_BROKER_URI = "tcp://localhost:0";

    private BrokerService broker;

    public static final byte[] PAYLOAD = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    /**
     * Send MEDIUM priority
     * Send HIGH priority
     * Send HIGH priority
     * <p/>
     * browse the list of messages
     * <p/>
     * consume the messages from the queue
     * <p/>
     * Compare browse and consumed messages - they should be the same
     *
     * @throws Exception
     * 		
     */
    public void testBrowsePriorityMessages() throws Exception {
        for (int i = 0; i < 5; i++) {
            // MED
            produceMessages(3, 4, "TestQ");
            Thread.sleep(1000);
            // HI
            produceMessages(3, 9, "TestQ");
            // browse messages, will page in
            ArrayList<Integer> browseList = browseQueue("TestQ");
            // HI
            produceMessages(3, 9, "TestQ");
            // browse again to be sure new messages are picked up
            browseList = browseQueue("TestQ");
            // consume messages to verify order
            ArrayList<Integer> consumeList = consumeMessages("TestQ");
            if (!(browseList.equals(consumeList))) {
                JMSQueueBrowserPriorityTest.LOG.info(("browseList size " + (browseList.size())));
                JMSQueueBrowserPriorityTest.LOG.info(("consumeList size " + (consumeList.size())));
                JMSQueueBrowserPriorityTest.LOG.info(("browseList is:" + browseList));
                JMSQueueBrowserPriorityTest.LOG.info(("consumeList is:" + consumeList));
            }
            // compare lists
            TestCase.assertTrue(("browseList and consumeList should be equal, iteration " + i), browseList.equals(consumeList));
        }
    }
}

