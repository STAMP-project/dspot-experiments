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


import ActiveMQDestination.QUEUE_TYPE;
import ActiveMQDestination.TOPIC_TYPE;
import Session.AUTO_ACKNOWLEDGE;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.network.DiscoveryNetworkConnector;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.org.apache.activemq.ActiveMQConnection;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ3274Test {
    private static final transient Logger LOG = LoggerFactory.getLogger(AMQ3274Test.class);

    protected static int Next_broker_num = 0;

    protected AMQ3274Test.EmbeddedTcpBroker broker1;

    protected AMQ3274Test.EmbeddedTcpBroker broker2;

    protected int nextEchoId = 0;

    protected boolean testError = false;

    protected int echoResponseFill = 0;// Number of "filler" response messages per request


    public AMQ3274Test() throws Exception {
        broker1 = new AMQ3274Test.EmbeddedTcpBroker();
        broker2 = new AMQ3274Test.EmbeddedTcpBroker();
        broker1.coreConnectTo(broker2, true);
        broker2.coreConnectTo(broker1, true);
    }

    @Test
    public void run() throws Exception {
        Thread start1;
        Thread start2;
        testError = false;
        // Use threads to avoid startup deadlock since the first broker started waits until
        // it knows the name of the remote broker before finishing its startup, which means
        // the remote must already be running.
        start1 = new Thread() {
            public void run() {
                try {
                    broker1.start();
                } catch (Exception ex) {
                    AMQ3274Test.LOG.error(null, ex);
                }
            }
        };
        start2 = new Thread() {
            public void run() {
                try {
                    broker2.start();
                } catch (Exception ex) {
                    AMQ3274Test.LOG.error(null, ex);
                }
            }
        };
        start1.start();
        start2.start();
        start1.join();
        start2.join();
        if (!(testError)) {
            this.testTempTopic(broker1.getConnectionUrl(), broker2.getConnectionUrl());
        }
        if (!(testError)) {
            this.testTempQueue(broker1.getConnectionUrl(), broker2.getConnectionUrl());
        }
        if (!(testError)) {
            this.testTopic(broker1.getConnectionUrl(), broker2.getConnectionUrl());
        }
        if (!(testError)) {
            this.testQueue(broker1.getConnectionUrl(), broker2.getConnectionUrl());
        }
        Thread.sleep(100);
        shutdown();
        Assert.assertTrue((!(testError)));
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////// INTERNAL CLASSES
    // /////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected class EmbeddedTcpBroker {
        protected BrokerService brokerSvc;

        protected int brokerNum;

        protected String brokerName;

        protected String brokerId;

        protected int port;

        protected String tcpUrl;

        public EmbeddedTcpBroker() throws Exception {
            brokerSvc = new BrokerService();
            synchronized(this.getClass()) {
                brokerNum = AMQ3274Test.Next_broker_num;
                (AMQ3274Test.Next_broker_num)++;
            }
            brokerName = "broker" + (brokerNum);
            brokerId = "b" + (brokerNum);
            brokerSvc.setBrokerName(brokerName);
            brokerSvc.setBrokerId(brokerId);
            brokerSvc.setPersistent(false);
            brokerSvc.setUseJmx(false);
            tcpUrl = brokerSvc.addConnector("tcp://localhost:0").getPublishableConnectString();
        }

        public Connection createConnection() throws URISyntaxException, JMSException {
            Connection result;
            result = org.apache.activemq.ActiveMQConnection.makeConnection(this.tcpUrl);
            return result;
        }

        public String getConnectionUrl() {
            return this.tcpUrl;
        }

        /**
         * Create network connections to the given broker using the
         * network-connector configuration of CORE brokers (e.g.
         * core1.bus.dev1.coresys.tmcs)
         *
         * @param other
         * 		
         * @param duplex_f
         * 		
         */
        public void coreConnectTo(AMQ3274Test.EmbeddedTcpBroker other, boolean duplex_f) throws Exception {
            this.makeConnectionTo(other, duplex_f, true);
            this.makeConnectionTo(other, duplex_f, false);
        }

        public void start() throws Exception {
            brokerSvc.start();
        }

        public void stop() throws Exception {
            brokerSvc.stop();
        }

        /**
         * Make one connection to the other embedded broker, of the specified
         * type (queue or topic) using the standard CORE broker networking.
         *
         * @param other
         * 		
         * @param duplex_f
         * 		
         * @param queue_f
         * 		
         * @throws Exception
         * 		
         */
        protected void makeConnectionTo(AMQ3274Test.EmbeddedTcpBroker other, boolean duplex_f, boolean queue_f) throws Exception {
            NetworkConnector nw_conn;
            String prefix;
            ActiveMQDestination excl_dest;
            ArrayList<ActiveMQDestination> excludes;
            nw_conn = new DiscoveryNetworkConnector(new URI((("static:(" + (other.tcpUrl)) + ")")));
            nw_conn.setDuplex(duplex_f);
            if (queue_f)
                nw_conn.setConduitSubscriptions(false);
            else
                nw_conn.setConduitSubscriptions(true);

            nw_conn.setNetworkTTL(5);
            nw_conn.setSuppressDuplicateQueueSubscriptions(true);
            nw_conn.setDecreaseNetworkConsumerPriority(true);
            nw_conn.setBridgeTempDestinations(true);
            if (queue_f) {
                prefix = "queue";
                excl_dest = ActiveMQDestination.createDestination(">", TOPIC_TYPE);
            } else {
                prefix = "topic";
                excl_dest = ActiveMQDestination.createDestination(">", QUEUE_TYPE);
            }
            excludes = new ArrayList<ActiveMQDestination>();
            excludes.add(excl_dest);
            nw_conn.setExcludedDestinations(excludes);
            if (duplex_f)
                nw_conn.setName((((((this.brokerId) + "<-") + prefix) + "->") + (other.brokerId)));
            else
                nw_conn.setName((((((this.brokerId) + "-") + prefix) + "->") + (other.brokerId)));

            brokerSvc.addNetworkConnector(nw_conn);
        }
    }

    protected class MessageClient extends Thread {
        protected MessageConsumer msgCons;

        protected boolean shutdownInd;

        protected int expectedCount;

        protected int lastSeq = 0;

        protected int msgCount = 0;

        protected boolean haveFirstSeq;

        protected CountDownLatch shutdownLatch;

        public MessageClient(MessageConsumer cons, int num_to_expect) {
            msgCons = cons;
            expectedCount = num_to_expect * ((echoResponseFill) + 1);
            shutdownLatch = new CountDownLatch(1);
        }

        public void run() {
            CountDownLatch latch;
            try {
                synchronized(this) {
                    latch = shutdownLatch;
                }
                shutdownInd = false;
                processMessages();
                latch.countDown();
            } catch (Exception exc) {
                AMQ3274Test.LOG.error("message client error", exc);
            }
        }

        public void waitShutdown(long timeout) {
            CountDownLatch latch;
            try {
                synchronized(this) {
                    latch = shutdownLatch;
                }
                if (latch != null)
                    latch.await(timeout, TimeUnit.MILLISECONDS);
                else
                    AMQ3274Test.LOG.info("echo client shutdown: client does not appear to be active");

            } catch (InterruptedException int_exc) {
                AMQ3274Test.LOG.warn("wait for message client shutdown interrupted", int_exc);
            }
        }

        public boolean shutdown() {
            boolean down_ind;
            if (!(shutdownInd)) {
                shutdownInd = true;
            }
            waitShutdown(200);
            synchronized(this) {
                if (((shutdownLatch) == null) || ((shutdownLatch.getCount()) == 0))
                    down_ind = true;
                else
                    down_ind = false;

            }
            return down_ind;
        }

        public int getNumMsgReceived() {
            return msgCount;
        }

        protected void processMessages() throws Exception {
            Message in_msg;
            haveFirstSeq = false;
            while ((!(shutdownInd)) && (!(testError))) {
                in_msg = msgCons.receive(100);
                if (in_msg != null) {
                    (msgCount)++;
                    checkMessage(in_msg);
                }
            } 
        }

        protected void checkMessage(Message in_msg) throws Exception {
            int seq;
            AMQ3274Test.LOG.debug(("received message " + (AMQ3274Test.fmtMsgInfo(in_msg))));
            if (in_msg.propertyExists("SEQ")) {
                seq = in_msg.getIntProperty("SEQ");
                if ((haveFirstSeq) && (seq != ((lastSeq) + 1))) {
                    AMQ3274Test.LOG.error(((("***ERROR*** incorrect sequence number; expected " + (Integer.toString(((lastSeq) + 1)))) + " but have ") + (Integer.toString(seq))));
                    testError = true;
                }
                lastSeq = seq;
                if ((msgCount) > (expectedCount)) {
                    AMQ3274Test.LOG.warn(((("*** have more messages than expected; have " + (msgCount)) + "; expect ") + (expectedCount)));
                    testError = true;
                }
            }
            if (in_msg.propertyExists("end-of-response")) {
                AMQ3274Test.LOG.trace("received end-of-response message");
                shutdownInd = true;
            }
        }
    }

    protected class EchoService extends Thread {
        protected String destName;

        protected Connection jmsConn;

        protected Session sess;

        protected MessageConsumer msg_cons;

        protected boolean Shutdown_ind;

        protected Destination req_dest;

        protected Destination resp_dest;

        protected MessageProducer msg_prod;

        protected CountDownLatch waitShutdown;

        public EchoService(String dest, Connection broker_conn) throws Exception {
            destName = dest;
            jmsConn = broker_conn;
            Shutdown_ind = false;
            sess = jmsConn.createSession(false, AUTO_ACKNOWLEDGE);
            req_dest = sess.createQueue(destName);
            msg_cons = sess.createConsumer(req_dest);
            jmsConn.start();
            waitShutdown = new CountDownLatch(1);
        }

        public EchoService(String dest, String broker_url) throws Exception {
            this(dest, ActiveMQConnection.makeConnection(broker_url));
        }

        public void run() {
            Message req;
            try {
                AMQ3274Test.LOG.info("STARTING ECHO SERVICE");
                while (!(Shutdown_ind)) {
                    req = msg_cons.receive(100);
                    if (req != null) {
                        if (AMQ3274Test.LOG.isDebugEnabled())
                            AMQ3274Test.LOG.debug(("ECHO request message " + (req.toString())));

                        resp_dest = req.getJMSReplyTo();
                        if ((resp_dest) != null) {
                            msg_prod = sess.createProducer(resp_dest);
                            msg_prod.send(req);
                            msg_prod.close();
                            msg_prod = null;
                        } else {
                            AMQ3274Test.LOG.warn("invalid request: no reply-to destination given");
                        }
                    }
                } 
            } catch (Exception ex) {
                AMQ3274Test.LOG.error(null, ex);
            } finally {
                AMQ3274Test.LOG.info("shutting down test echo service");
                try {
                    jmsConn.stop();
                } catch (javax.jms jms_exc) {
                    AMQ3274Test.LOG.warn("error on shutting down JMS connection", jms_exc);
                }
                synchronized(this) {
                    waitShutdown.countDown();
                }
            }
        }

        /**
         * Shut down the service, waiting up to 3 seconds for the service to
         * terminate.
         */
        public void shutdown() {
            CountDownLatch wait_l;
            synchronized(this) {
                wait_l = waitShutdown;
            }
            Shutdown_ind = true;
            try {
                if (wait_l != null) {
                    if (wait_l.await(3000, TimeUnit.MILLISECONDS)) {
                        AMQ3274Test.LOG.info("echo service shutdown complete");
                    } else {
                        AMQ3274Test.LOG.warn("timeout waiting for echo service shutdown");
                    }
                } else {
                    AMQ3274Test.LOG.info("echo service shutdown: service does not appear to be active");
                }
            } catch (InterruptedException int_exc) {
                AMQ3274Test.LOG.warn("interrupted while waiting for echo service shutdown");
            }
        }
    }
}

