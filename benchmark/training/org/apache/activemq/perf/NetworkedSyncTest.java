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
package org.apache.activemq.perf;


import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.network.NetworkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NetworkedSyncTest extends TestCase {
    // constants
    public static final int MESSAGE_COUNT = 10000;// 100000;


    public static final String config = "org/apache/activemq/perf/networkSync.xml";

    public static final String broker1URL = "tcp://localhost:61616";

    public static final String broker2URL = "tcp://localhost:62616";

    private final String networkConnectorURL = ("static://(" + (NetworkedSyncTest.broker2URL)) + ")";

    private static final Logger LOG = LoggerFactory.getLogger(NetworkedSyncTest.class);

    BrokerService broker1 = null;

    BrokerService broker2 = null;

    NetworkConnector connector = null;

    /**
     *
     *
     * @param name
     * 		
     */
    public NetworkedSyncTest(String name) {
        super(name);
        NetworkedSyncTest.LOG.info("Testcase started.");
    }

    public void testMessageExchange() throws Exception {
        NetworkedSyncTest.LOG.info("testMessageExchange() called.");
        long start = System.currentTimeMillis();
        // create producer and consumer threads
        Thread producer = new Thread(new Producer());
        Thread consumer = new Thread(new Consumer());
        // start threads
        consumer.start();
        Thread.sleep(2000);
        producer.start();
        // wait for threads to finish
        producer.join();
        consumer.join();
        long end = System.currentTimeMillis();
        System.out.println(("Duration: " + (end - start)));
    }
}

