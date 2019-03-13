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
package org.apache.activemq.transport.failover;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javax.jms.Connection;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for AMQ-3719
 */
public class ConnectionHangOnStartupTest {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionHangOnStartupTest.class);

    // short maxInactivityDurationInitalDelay to trigger the bug, short
    // maxReconnectDelay so that the test runs faster (because it will retry
    // connection sooner)
    protected String uriString = "failover://(tcp://localhost:62001?wireFormat.maxInactivityDurationInitalDelay=1,tcp://localhost:62002?wireFormat.maxInactivityDurationInitalDelay=1)?randomize=false&maxReconnectDelay=200";

    protected BrokerService master = null;

    protected AtomicReference<BrokerService> slave = new AtomicReference<BrokerService>();

    @Test(timeout = 60000)
    public void testInitialWireFormatNegotiationTimeout() throws Exception {
        final AtomicReference<Connection> conn = new AtomicReference<Connection>();
        final CountDownLatch connStarted = new CountDownLatch(1);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    conn.set(createConnectionFactory().createConnection());
                    conn.get().start();
                } catch (Exception ex) {
                    ConnectionHangOnStartupTest.LOG.error("could not create or start connection", ex);
                }
                connStarted.countDown();
            }
        };
        t.start();
        createMaster();
        // slave will never start unless the master dies!
        // createSlave();
        conn.get().stop();
    }
}

