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
package org.apache.activemq.transport.amqp;


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JMSMessageGroupsTest extends JMSClientTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(JMSMessageGroupsTest.class);

    private static final int ITERATIONS = 10;

    private static final int MESSAGE_COUNT = 10;

    private static final int MESSAGE_SIZE = 200 * 1024;

    private static final int RECEIVE_TIMEOUT = 3000;

    private static final String JMSX_GROUP_ID = "JmsGroupsTest";

    @Test(timeout = 60000)
    public void testGroupSeqIsNeverLost() throws Exception {
        AtomicInteger sequenceCounter = new AtomicInteger();
        for (int i = 0; i < (JMSMessageGroupsTest.ITERATIONS); ++i) {
            connection = createConnection();
            {
                sendMessagesToBroker(JMSMessageGroupsTest.MESSAGE_COUNT, sequenceCounter);
                readMessagesOnBroker(JMSMessageGroupsTest.MESSAGE_COUNT);
            }
            connection.close();
        }
    }
}

