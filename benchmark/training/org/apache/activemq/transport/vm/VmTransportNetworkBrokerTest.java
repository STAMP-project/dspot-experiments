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
package org.apache.activemq.transport.vm;


import Session.AUTO_ACKNOWLEDGE;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.bugs.embedded.ThreadExplorer;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.network.DurableConduitBridge;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.activemq.util.Wait;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getLogger;


public class VmTransportNetworkBrokerTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(VmTransportNetworkBrokerTest.class);

    private static final String VM_BROKER_URI = "vm://localhost?create=false";

    public void testNoThreadLeak() throws Exception {
        // with VMConnection and simple discovery network connector
        Thread[] threads = filterDaemonThreads(ThreadExplorer.listThreads());
        final int originalThreadCount = threads.length;
        VmTransportNetworkBrokerTest.LOG.debug(ThreadExplorer.show("threads at beginning"));
        BrokerService broker = new BrokerService();
        broker.setDedicatedTaskRunner(true);
        broker.setPersistent(false);
        broker.addConnector("tcp://localhost:61616");
        NetworkConnector networkConnector = broker.addNetworkConnector("static:(tcp://wrongHostname1:61617,tcp://wrongHostname2:61618)?useExponentialBackOff=false");
        networkConnector.setDuplex(true);
        broker.start();
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(new URI(VmTransportNetworkBrokerTest.VM_BROKER_URI));
        Connection connection = cf.createConnection("system", "manager");
        connection.start();
        // let it settle
        TimeUnit.SECONDS.sleep(5);
        int threadCountAfterStart = Thread.activeCount();
        TimeUnit.SECONDS.sleep(20);
        int threadCountAfterSleep = Thread.activeCount();
        TestCase.assertTrue(((((("Threads are leaking: " + (ThreadExplorer.show("active sleep"))) + ", threadCount=") + threadCountAfterStart) + " threadCountAfterSleep=") + threadCountAfterSleep), (threadCountAfterSleep < (2 * threadCountAfterStart)));
        connection.close();
        broker.stop();
        broker.waitUntilStopped();
        // testNoDanglingThreadsAfterStop with tcp transport
        broker = new BrokerService();
        broker.setSchedulerSupport(true);
        broker.setDedicatedTaskRunner(true);
        broker.setPersistent(false);
        broker.addConnector("tcp://localhost:61616?wireFormat.maxInactivityDuration=1000&wireFormat.maxInactivityDurationInitalDelay=1000");
        broker.start();
        cf = new ActiveMQConnectionFactory("tcp://localhost:61616?wireFormat.maxInactivityDuration=1000&wireFormat.maxInactivityDurationInitalDelay=1000");
        connection = cf.createConnection("system", "manager");
        connection.start();
        connection.close();
        broker.stop();
        broker.waitUntilStopped();
        final AtomicInteger threadCountAfterStop = new AtomicInteger();
        boolean ok = Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                VmTransportNetworkBrokerTest.LOG.info(ThreadExplorer.show("active after stop"));
                // get final threads but filter out any daemon threads that the JVM may have created.
                Thread[] threads = filterDaemonThreads(ThreadExplorer.listThreads());
                threadCountAfterStop.set(threads.length);
                return (threadCountAfterStop.get()) <= originalThreadCount;
            }
        });
        VmTransportNetworkBrokerTest.LOG.info(((("originalThreadCount=" + originalThreadCount) + " threadCountAfterStop=") + threadCountAfterStop));
        TestCase.assertTrue(((((("Threads are leaking: " + (ThreadExplorer.show("active after stop"))) + ". originalThreadCount=") + originalThreadCount) + " threadCountAfterStop=") + (threadCountAfterStop.get())), ok);
    }

    public void testInvalidClientIdAndDurableSubs() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setUseJmx(false);
        broker.setDedicatedTaskRunner(true);
        broker.setPersistent(false);
        broker.addConnector("tcp://localhost:0");
        broker.start();
        // ensure remoteConnection fails with InvalidClientId
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(broker.getTransportConnectors().get(0).getPublishableConnectString());
        Connection connection = connectionFactory.createConnection("system", "manager");
        connection.setClientID("F1_forwarder_outbound");
        connection.start();
        BrokerService forwarder = new BrokerService();
        forwarder.setBrokerName("forwarder");
        forwarder.setPersistent(false);
        forwarder.setUseJmx(false);
        forwarder.start();
        // setup some durable subs to have some local work to do
        ActiveMQConnectionFactory vmFactory = new ActiveMQConnectionFactory("vm://forwarder");
        Connection vmConnection = vmFactory.createConnection("system", "manager");
        vmConnection.setClientID("vm_local");
        vmConnection.start();
        Session session = vmConnection.createSession(false, AUTO_ACKNOWLEDGE);
        for (int i = 0; i < 5000; i++) {
            session.createDurableSubscriber(new ActiveMQTopic(("T" + i)), ("" + i));
        }
        vmConnection.close();
        final AtomicInteger logCounts = new AtomicInteger(0);
        DefaultTestAppender appender = new DefaultTestAppender() {
            @Override
            public void doAppend(LoggingEvent event) {
                if ((event.getLevel()) == (Level.ERROR)) {
                    logCounts.incrementAndGet();
                }
            }
        };
        getLogger(DurableConduitBridge.class).addAppender(appender);
        try {
            NetworkConnector networkConnector = forwarder.addNetworkConnector((("static:(" + (broker.getTransportConnectors().get(0).getPublishableConnectString())) + ")"));
            networkConnector.setName("F1");
            forwarder.addNetworkConnector(networkConnector);
            forwarder.startAllConnectors();
            TimeUnit.SECONDS.sleep(1);
            connection.close();
            forwarder.stop();
            broker.stop();
            TestCase.assertEquals("no errors", 0, logCounts.get());
        } finally {
            getLogger(DurableConduitBridge.class).removeAppender(appender);
        }
    }
}

