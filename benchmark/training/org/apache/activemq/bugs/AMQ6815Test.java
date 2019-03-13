/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.bugs;


import javax.jms.Connection;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ6815Test {
    static final Logger LOG = LoggerFactory.getLogger(AMQ6815Test.class);

    private static final int MEM_LIMIT = (5 * 1024) * 1024;

    private static final byte[] payload = new byte[5 * 1024];

    protected BrokerService brokerService;

    protected Connection connection;

    protected Session session;

    protected Queue amqDestination;

    @Test(timeout = 120000)
    public void testHeapUsage() throws Exception {
        Runtime.getRuntime().gc();
        final long initUsedMemory = (Runtime.getRuntime().totalMemory()) - (Runtime.getRuntime().freeMemory());
        sendMessages(10000);
        Runtime.getRuntime().gc();
        long usedMem = ((Runtime.getRuntime().totalMemory()) - (Runtime.getRuntime().freeMemory())) - initUsedMemory;
        AMQ6815Test.LOG.info((("Mem in use: " + (usedMem / 1024)) + "K"));
        Assert.assertTrue(("Used Mem reasonable " + usedMem), (usedMem < (5 * (AMQ6815Test.MEM_LIMIT))));
    }
}

