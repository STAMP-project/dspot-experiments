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
package org.apache.activemq.bugs;


import javax.jms.Connection;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* Try and replicate:
Caused by: java.io.IOException: Could not locate data file data--188
 at org.apache.activemq.kaha.impl.async.AsyncDataManager.getDataFile(AsyncDataManager.java:302)
 at org.apache.activemq.kaha.impl.async.AsyncDataManager.read(AsyncDataManager.java:614)
 at org.apache.activemq.store.amq.AMQPersistenceAdapter.readCommand(AMQPersistenceAdapter.java:523)
 */
public class MissingDataFileTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(MissingDataFileTest.class);

    private static int counter = 500;

    private static int hectorToHaloCtr;

    private static int xenaToHaloCtr;

    private static int troyToHaloCtr;

    private static int haloToHectorCtr;

    private static int haloToXenaCtr;

    private static int haloToTroyCtr;

    private final String hectorToHalo = "hectorToHalo";

    private final String xenaToHalo = "xenaToHalo";

    private final String troyToHalo = "troyToHalo";

    private final String haloToHector = "haloToHector";

    private final String haloToXena = "haloToXena";

    private final String haloToTroy = "haloToTroy";

    private BrokerService broker;

    private Connection hectorConnection;

    private Connection xenaConnection;

    private Connection troyConnection;

    private Connection haloConnection;

    private final Object lock = new Object();

    final boolean useTopic = false;

    final boolean useSleep = true;

    protected static final String payload = new String(new byte[500]);

    public void testForNoDataFoundError() throws Exception {
        startBroker();
        hectorConnection = createConnection();
        Thread hectorThread = buildProducer(hectorConnection, hectorToHalo, false, useTopic);
        Receiver hHectorReceiver = new Receiver() {
            @Override
            public void receive(String s) throws Exception {
                (MissingDataFileTest.haloToHectorCtr)++;
                if ((MissingDataFileTest.haloToHectorCtr) >= (MissingDataFileTest.counter)) {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
                possiblySleep(MissingDataFileTest.haloToHectorCtr);
            }
        };
        buildReceiver(hectorConnection, haloToHector, false, hHectorReceiver, useTopic);
        troyConnection = createConnection();
        Thread troyThread = buildProducer(troyConnection, troyToHalo);
        Receiver hTroyReceiver = new Receiver() {
            @Override
            public void receive(String s) throws Exception {
                (MissingDataFileTest.haloToTroyCtr)++;
                if ((MissingDataFileTest.haloToTroyCtr) >= (MissingDataFileTest.counter)) {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
                possiblySleep(MissingDataFileTest.haloToTroyCtr);
            }
        };
        buildReceiver(hectorConnection, haloToTroy, false, hTroyReceiver, false);
        xenaConnection = createConnection();
        Thread xenaThread = buildProducer(xenaConnection, xenaToHalo);
        Receiver hXenaReceiver = new Receiver() {
            @Override
            public void receive(String s) throws Exception {
                (MissingDataFileTest.haloToXenaCtr)++;
                if ((MissingDataFileTest.haloToXenaCtr) >= (MissingDataFileTest.counter)) {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
                possiblySleep(MissingDataFileTest.haloToXenaCtr);
            }
        };
        buildReceiver(xenaConnection, haloToXena, false, hXenaReceiver, false);
        haloConnection = createConnection();
        final MessageSender hectorSender = buildTransactionalProducer(haloToHector, haloConnection, false);
        final MessageSender troySender = buildTransactionalProducer(haloToTroy, haloConnection, false);
        final MessageSender xenaSender = buildTransactionalProducer(haloToXena, haloConnection, false);
        Receiver hectorReceiver = new Receiver() {
            @Override
            public void receive(String s) throws Exception {
                (MissingDataFileTest.hectorToHaloCtr)++;
                troySender.send(MissingDataFileTest.payload);
                if ((MissingDataFileTest.hectorToHaloCtr) >= (MissingDataFileTest.counter)) {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                    possiblySleep(MissingDataFileTest.hectorToHaloCtr);
                }
            }
        };
        Receiver xenaReceiver = new Receiver() {
            @Override
            public void receive(String s) throws Exception {
                (MissingDataFileTest.xenaToHaloCtr)++;
                hectorSender.send(MissingDataFileTest.payload);
                if ((MissingDataFileTest.xenaToHaloCtr) >= (MissingDataFileTest.counter)) {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
                possiblySleep(MissingDataFileTest.xenaToHaloCtr);
            }
        };
        Receiver troyReceiver = new Receiver() {
            @Override
            public void receive(String s) throws Exception {
                (MissingDataFileTest.troyToHaloCtr)++;
                xenaSender.send(MissingDataFileTest.payload);
                if ((MissingDataFileTest.troyToHaloCtr) >= (MissingDataFileTest.counter)) {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        };
        buildReceiver(haloConnection, hectorToHalo, true, hectorReceiver, false);
        buildReceiver(haloConnection, xenaToHalo, true, xenaReceiver, false);
        buildReceiver(haloConnection, troyToHalo, true, troyReceiver, false);
        haloConnection.start();
        troyConnection.start();
        troyThread.start();
        xenaConnection.start();
        xenaThread.start();
        hectorConnection.start();
        hectorThread.start();
        waitForMessagesToBeDelivered();
        // number of messages received should match messages sent
        TestCase.assertEquals(MissingDataFileTest.hectorToHaloCtr, MissingDataFileTest.counter);
        MissingDataFileTest.LOG.info((("hectorToHalo received " + (MissingDataFileTest.hectorToHaloCtr)) + " messages"));
        TestCase.assertEquals(MissingDataFileTest.xenaToHaloCtr, MissingDataFileTest.counter);
        MissingDataFileTest.LOG.info((("xenaToHalo received " + (MissingDataFileTest.xenaToHaloCtr)) + " messages"));
        TestCase.assertEquals(MissingDataFileTest.troyToHaloCtr, MissingDataFileTest.counter);
        MissingDataFileTest.LOG.info((("troyToHalo received " + (MissingDataFileTest.troyToHaloCtr)) + " messages"));
        TestCase.assertEquals(MissingDataFileTest.haloToHectorCtr, MissingDataFileTest.counter);
        MissingDataFileTest.LOG.info((("haloToHector received " + (MissingDataFileTest.haloToHectorCtr)) + " messages"));
        TestCase.assertEquals(MissingDataFileTest.haloToXenaCtr, MissingDataFileTest.counter);
        MissingDataFileTest.LOG.info((("haloToXena received " + (MissingDataFileTest.haloToXenaCtr)) + " messages"));
        TestCase.assertEquals(MissingDataFileTest.haloToTroyCtr, MissingDataFileTest.counter);
        MissingDataFileTest.LOG.info((("haloToTroy received " + (MissingDataFileTest.haloToTroyCtr)) + " messages"));
    }
}

