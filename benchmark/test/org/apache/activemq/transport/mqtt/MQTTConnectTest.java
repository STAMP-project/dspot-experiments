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
package org.apache.activemq.transport.mqtt;


import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.util.Wait;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that connection attempts that don't send a CONNECT frame will
 * get cleaned up by the inactivity monitor.
 */
@RunWith(Parameterized.class)
public class MQTTConnectTest extends MQTTTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(MQTTConnectTest.class);

    private Socket connection;

    public MQTTConnectTest(String connectorScheme, boolean useSSL) {
        super(connectorScheme, useSSL);
    }

    @Test(timeout = 90000)
    public void testParallelConnectPlain() throws Exception {
        final int THREAD_COUNT = 16;
        final int CONNECTION_COUNT = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        final AtomicInteger clientIdGemeratpr = new AtomicInteger();
        for (int i = 0; i < CONNECTION_COUNT; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        MQTT mqtt = createMQTTConnection();
                        mqtt.setClientId(("client-" + (clientIdGemeratpr.incrementAndGet())));
                        mqtt.setCleanSession(true);
                        BlockingConnection connection = mqtt.blockingConnection();
                        connection.connect();
                        connection.disconnect();
                    } catch (Exception e) {
                        MQTTConnectTest.LOG.error("unexpected exception on connect/disconnect", e);
                        exceptions.add(e);
                    }
                }
            });
        }
        executorService.shutdown();
        Assert.assertTrue("executor done on time", executorService.awaitTermination(60, TimeUnit.SECONDS));
    }

    @Test(timeout = 60 * 1000)
    public void testInactivityMonitor() throws Exception {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                try {
                    connection = createConnection();
                    connection.getOutputStream().write(0);
                    connection.getOutputStream().flush();
                } catch (Exception ex) {
                    MQTTConnectTest.LOG.error("unexpected exception on connect/disconnect", ex);
                    exceptions.add(ex);
                }
            }
        };
        t1.start();
        Assert.assertTrue("one connection", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 1 == (brokerService.getTransportConnectors().get(0).connectionCount());
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
        // and it should be closed due to inactivity
        Assert.assertTrue("no dangling connections", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 0 == (brokerService.getTransportConnectors().get(0).connectionCount());
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
        Assert.assertTrue("no exceptions", exceptions.isEmpty());
    }
}

