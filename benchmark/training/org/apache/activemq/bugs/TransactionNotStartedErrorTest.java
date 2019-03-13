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


/* simulate message flow which cause the following exception in the broker
(exception logged by client) <p/> 2007-07-24 13:51:23,624
com.easynet.halo.Halo ERROR (LoggingErrorHandler.java: 23) JMS failure
javax.jms.JMSException: Transaction 'TX:ID:dmt-53625-1185281414694-1:0:344'
has not been started. at
org.apache.activemq.broker.TransactionBroker.getTransaction(TransactionBroker.java:230)
This appears to be consistent in a MacBook. Haven't been able to replicate it
on Windows though
 */
public class TransactionNotStartedErrorTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionNotStartedErrorTest.class);

    private static final int counter = 500;

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

    public void testTransactionNotStartedError() throws Exception {
        startBroker();
        hectorConnection = createConnection();
        Thread hectorThread = buildProducer(hectorConnection, hectorToHalo);
        Receiver hHectorReceiver = new Receiver() {
            public void receive(String s) throws Exception {
                (TransactionNotStartedErrorTest.haloToHectorCtr)++;
                if ((TransactionNotStartedErrorTest.haloToHectorCtr) >= (TransactionNotStartedErrorTest.counter)) {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        };
        buildReceiver(hectorConnection, haloToHector, false, hHectorReceiver);
        troyConnection = createConnection();
        Thread troyThread = buildProducer(troyConnection, troyToHalo);
        Receiver hTroyReceiver = new Receiver() {
            public void receive(String s) throws Exception {
                (TransactionNotStartedErrorTest.haloToTroyCtr)++;
                if ((TransactionNotStartedErrorTest.haloToTroyCtr) >= (TransactionNotStartedErrorTest.counter)) {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        };
        buildReceiver(hectorConnection, haloToTroy, false, hTroyReceiver);
        xenaConnection = createConnection();
        Thread xenaThread = buildProducer(xenaConnection, xenaToHalo);
        Receiver hXenaReceiver = new Receiver() {
            public void receive(String s) throws Exception {
                (TransactionNotStartedErrorTest.haloToXenaCtr)++;
                if ((TransactionNotStartedErrorTest.haloToXenaCtr) >= (TransactionNotStartedErrorTest.counter)) {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        };
        buildReceiver(xenaConnection, haloToXena, false, hXenaReceiver);
        haloConnection = createConnection();
        final MessageSender hectorSender = buildTransactionalProducer(haloToHector, haloConnection);
        final MessageSender troySender = buildTransactionalProducer(haloToTroy, haloConnection);
        final MessageSender xenaSender = buildTransactionalProducer(haloToXena, haloConnection);
        Receiver hectorReceiver = new Receiver() {
            public void receive(String s) throws Exception {
                (TransactionNotStartedErrorTest.hectorToHaloCtr)++;
                troySender.send("halo to troy because of hector");
                if ((TransactionNotStartedErrorTest.hectorToHaloCtr) >= (TransactionNotStartedErrorTest.counter)) {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        };
        Receiver xenaReceiver = new Receiver() {
            public void receive(String s) throws Exception {
                (TransactionNotStartedErrorTest.xenaToHaloCtr)++;
                hectorSender.send("halo to hector because of xena");
                if ((TransactionNotStartedErrorTest.xenaToHaloCtr) >= (TransactionNotStartedErrorTest.counter)) {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        };
        Receiver troyReceiver = new Receiver() {
            public void receive(String s) throws Exception {
                (TransactionNotStartedErrorTest.troyToHaloCtr)++;
                xenaSender.send("halo to xena because of troy");
                if ((TransactionNotStartedErrorTest.troyToHaloCtr) >= (TransactionNotStartedErrorTest.counter)) {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        };
        buildReceiver(haloConnection, hectorToHalo, true, hectorReceiver);
        buildReceiver(haloConnection, xenaToHalo, true, xenaReceiver);
        buildReceiver(haloConnection, troyToHalo, true, troyReceiver);
        haloConnection.start();
        troyConnection.start();
        troyThread.start();
        xenaConnection.start();
        xenaThread.start();
        hectorConnection.start();
        hectorThread.start();
        waitForMessagesToBeDelivered();
        // number of messages received should match messages sent
        TestCase.assertEquals(TransactionNotStartedErrorTest.hectorToHaloCtr, TransactionNotStartedErrorTest.counter);
        TransactionNotStartedErrorTest.LOG.info((("hectorToHalo received " + (TransactionNotStartedErrorTest.hectorToHaloCtr)) + " messages"));
        TestCase.assertEquals(TransactionNotStartedErrorTest.xenaToHaloCtr, TransactionNotStartedErrorTest.counter);
        TransactionNotStartedErrorTest.LOG.info((("xenaToHalo received " + (TransactionNotStartedErrorTest.xenaToHaloCtr)) + " messages"));
        TestCase.assertEquals(TransactionNotStartedErrorTest.troyToHaloCtr, TransactionNotStartedErrorTest.counter);
        TransactionNotStartedErrorTest.LOG.info((("troyToHalo received " + (TransactionNotStartedErrorTest.troyToHaloCtr)) + " messages"));
        TestCase.assertEquals(TransactionNotStartedErrorTest.haloToHectorCtr, TransactionNotStartedErrorTest.counter);
        TransactionNotStartedErrorTest.LOG.info((("haloToHector received " + (TransactionNotStartedErrorTest.haloToHectorCtr)) + " messages"));
        TestCase.assertEquals(TransactionNotStartedErrorTest.haloToXenaCtr, TransactionNotStartedErrorTest.counter);
        TransactionNotStartedErrorTest.LOG.info((("haloToXena received " + (TransactionNotStartedErrorTest.haloToXenaCtr)) + " messages"));
        TestCase.assertEquals(TransactionNotStartedErrorTest.haloToTroyCtr, TransactionNotStartedErrorTest.counter);
        TransactionNotStartedErrorTest.LOG.info((("haloToTroy received " + (TransactionNotStartedErrorTest.haloToTroyCtr)) + " messages"));
    }
}

