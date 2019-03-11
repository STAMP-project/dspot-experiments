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
 * WITHOUT WARRANTIES OR ONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.usecases;


import Session.AUTO_ACKNOWLEDGE;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DurableSubProcessMultiRestartTest {
    private static final Logger LOG = LoggerFactory.getLogger(DurableSubProcessMultiRestartTest.class);

    public static final long RUNTIME = (1 * 60) * 1000;

    private BrokerService broker;

    private ActiveMQTopic topic;

    private final ReentrantReadWriteLock processLock = new ReentrantReadWriteLock(true);

    private int restartCount = 0;

    private final int SUBSCRIPTION_ID = 1;

    static final Vector<Throwable> exceptions = new Vector<Throwable>();

    /**
     * The test creates a durable subscriber and producer with a broker that is
     * continually restarted.
     *
     * Producer creates a message every .5 seconds -creates a new connection for
     * each message
     *
     * durable subscriber - comes online for 10 seconds, - then goes offline for
     * a "moment" - repeats the cycle
     *
     * approx every 10 seconds the broker restarts. Subscriber and Producer
     * connections will be closed BEFORE the restart.
     *
     * The Durable subscriber is "unsubscribed" before the the end of the test.
     *
     * checks for number of kahaDB files left on filesystem.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProcess() throws Exception {
        DurableSubProcessMultiRestartTest.DurableSubscriber durableSubscriber = new DurableSubProcessMultiRestartTest.DurableSubscriber(SUBSCRIPTION_ID);
        DurableSubProcessMultiRestartTest.MsgProducer msgProducer = new DurableSubProcessMultiRestartTest.MsgProducer();
        try {
            // register the subscription & start messages
            durableSubscriber.start();
            msgProducer.start();
            long endTime = (System.currentTimeMillis()) + (DurableSubProcessMultiRestartTest.RUNTIME);
            while (endTime > (System.currentTimeMillis())) {
                Thread.sleep(10000);
                restartBroker();
            } 
        } catch (Throwable e) {
            DurableSubProcessMultiRestartTest.exit("ProcessTest.testProcess failed.", e);
        }
        // wait for threads to finish
        try {
            msgProducer.join();
            durableSubscriber.join();
        } catch (InterruptedException e) {
            e.printStackTrace(System.out);
        }
        // restart broker one last time
        restartBroker();
        Assert.assertTrue(("no exceptions: " + (DurableSubProcessMultiRestartTest.exceptions)), DurableSubProcessMultiRestartTest.exceptions.isEmpty());
        final KahaDBPersistenceAdapter pa = ((KahaDBPersistenceAdapter) (broker.getPersistenceAdapter()));
        Assert.assertTrue(("only less than two journal files should be left: " + (pa.getStore().getJournal().getFileMap().size())), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (pa.getStore().getJournal().getFileMap().size()) <= 2;
            }
        }, TimeUnit.MINUTES.toMillis(3)));
        DurableSubProcessMultiRestartTest.LOG.info("DONE.");
    }

    /**
     * Producers messages
     */
    final class MsgProducer extends Thread {
        String url = "vm://" + (DurableSubProcessMultiRestartTest.getName());

        final ConnectionFactory cf = new ActiveMQConnectionFactory(url);

        private long msgCount;

        int messageRover = 0;

        public MsgProducer() {
            super("MsgProducer");
            setDaemon(true);
        }

        @Override
        public void run() {
            long endTime = (DurableSubProcessMultiRestartTest.RUNTIME) + (System.currentTimeMillis());
            try {
                while (endTime > (System.currentTimeMillis())) {
                    Thread.sleep(500);
                    processLock.readLock().lock();
                    try {
                        send();
                    } finally {
                        processLock.readLock().unlock();
                    }
                    DurableSubProcessMultiRestartTest.LOG.info(("MsgProducer msgCount=" + (msgCount)));
                } 
            } catch (Throwable e) {
                DurableSubProcessMultiRestartTest.exit("Server.run failed", e);
            }
        }

        public void send() throws JMSException {
            DurableSubProcessMultiRestartTest.LOG.info("Sending ... ");
            Connection con = cf.createConnection();
            Session sess = con.createSession(false, AUTO_ACKNOWLEDGE);
            MessageProducer prod = sess.createProducer(null);
            Message message = sess.createMessage();
            message.setIntProperty("ID", (++(messageRover)));
            message.setBooleanProperty("COMMIT", true);
            prod.send(topic, message);
            (msgCount)++;
            DurableSubProcessMultiRestartTest.LOG.info("Message Sent.");
            sess.close();
            con.close();
        }
    }

    /**
     * Consumes massages from a durable subscription. Goes online/offline
     * periodically.
     */
    private final class DurableSubscriber extends Thread {
        String url = "tcp://localhost:61656";

        final ConnectionFactory cf = new ActiveMQConnectionFactory(url);

        public static final String SUBSCRIPTION_NAME = "subscription";

        private final int id;

        private final String conClientId;

        private long msgCount;

        public DurableSubscriber(int id) throws JMSException {
            super(("DurableSubscriber" + id));
            setDaemon(true);
            this.id = id;
            conClientId = "cli" + id;
            subscribe();
        }

        @Override
        public void run() {
            long end = (System.currentTimeMillis()) + (DurableSubProcessMultiRestartTest.RUNTIME);
            try {
                // while (true) {
                while (end > (System.currentTimeMillis())) {
                    processLock.readLock().lock();
                    try {
                        process(5000);
                    } finally {
                        processLock.readLock().unlock();
                    }
                } 
                unsubscribe();
            } catch (JMSException maybe) {
                if ((maybe.getCause()) instanceof IOException) {
                    // ok on broker shutdown;
                } else {
                    DurableSubProcessMultiRestartTest.exit(((toString()) + " failed with JMSException"), maybe);
                }
            } catch (Throwable e) {
                DurableSubProcessMultiRestartTest.exit(((toString()) + " failed."), e);
            }
            DurableSubProcessMultiRestartTest.LOG.info((((toString()) + " DONE. MsgCout=") + (msgCount)));
        }

        private void process(long duration) throws JMSException {
            DurableSubProcessMultiRestartTest.LOG.info(((toString()) + " ONLINE."));
            Connection con = openConnection();
            Session sess = con.createSession(false, AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = sess.createDurableSubscriber(topic, DurableSubProcessMultiRestartTest.DurableSubscriber.SUBSCRIPTION_NAME);
            long end = (System.currentTimeMillis()) + duration;
            try {
                while (end > (System.currentTimeMillis())) {
                    Message message = consumer.receive(100);
                    if (message != null) {
                        DurableSubProcessMultiRestartTest.LOG.info(((toString()) + "received message..."));
                        (msgCount)++;
                    }
                } 
            } finally {
                sess.close();
                con.close();
                DurableSubProcessMultiRestartTest.LOG.info(((toString()) + " OFFLINE."));
            }
        }

        private Connection openConnection() throws JMSException {
            Connection con = cf.createConnection();
            con.setClientID(conClientId);
            con.start();
            return con;
        }

        private void subscribe() throws JMSException {
            Connection con = openConnection();
            Session session = con.createSession(false, AUTO_ACKNOWLEDGE);
            session.createDurableSubscriber(topic, DurableSubProcessMultiRestartTest.DurableSubscriber.SUBSCRIPTION_NAME);
            DurableSubProcessMultiRestartTest.LOG.info(((toString()) + " SUBSCRIBED"));
            session.close();
            con.close();
        }

        private void unsubscribe() throws JMSException {
            Connection con = openConnection();
            Session session = con.createSession(false, AUTO_ACKNOWLEDGE);
            session.unsubscribe(DurableSubProcessMultiRestartTest.DurableSubscriber.SUBSCRIPTION_NAME);
            DurableSubProcessMultiRestartTest.LOG.info(((toString()) + " UNSUBSCRIBED"));
            session.close();
            con.close();
        }

        @Override
        public String toString() {
            return ("DurableSubscriber[id=" + (id)) + "]";
        }
    }
}

