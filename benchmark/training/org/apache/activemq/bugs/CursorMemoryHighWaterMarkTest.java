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


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertTrue;


/**
 * ensure a message will be pages in from the store when another dest has stopped caching
 */
public class CursorMemoryHighWaterMarkTest {
    private static final Logger LOG = LoggerFactory.getLogger(CursorMemoryHighWaterMarkTest.class);

    public static final String MY_QUEUE_2 = "myQueue_2";

    public static final String MY_QUEUE = "myQueue";

    public static final String BROKER_NAME = "myBroker";

    private BrokerService broker1;

    private ActiveMQConnectionFactory connectionFactory;

    @Test
    public void testCursorHighWaterMark() throws Exception {
        // check the memory usage on broker1 (source broker ) has returned to zero
        int systemUsage = broker1.getSystemUsage().getMemoryUsage().getPercentUsage();
        Assert.assertEquals("System Usage on broker1 before test", 0, systemUsage);
        // produce message
        produceMesssages(CursorMemoryHighWaterMarkTest.MY_QUEUE, 3000);
        // verify usage is greater than 60%
        systemUsage = broker1.getSystemUsage().getMemoryUsage().getPercentUsage();
        assertTrue("System Usage on broker1 before test", (60 < systemUsage));
        CursorMemoryHighWaterMarkTest.LOG.info(("Broker System Mem Usage: " + (broker1.getSystemUsage().getMemoryUsage())));
        // send a mesage to myqueue.2
        produceMesssages(CursorMemoryHighWaterMarkTest.MY_QUEUE_2, 1);
        // try to consume that message
        consume(CursorMemoryHighWaterMarkTest.MY_QUEUE_2, 1);
    }
}

