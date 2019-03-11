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
package org.apache.activemq.usecases;


import ActiveMQDestination.QUEUE_TYPE;
import ActiveMQDestination.TOPIC_TYPE;
import Session.AUTO_ACKNOWLEDGE;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;


public class RequestReplyToTopicViaThreeNetworkHopsTest {
    protected static final int CONCURRENT_CLIENT_COUNT = 5;

    protected static final int CONCURRENT_SERVER_COUNT = 5;

    protected static final int TOTAL_CLIENT_ITER = 10;

    protected static int Next_broker_num = 0;

    protected RequestReplyToTopicViaThreeNetworkHopsTest.EmbeddedTcpBroker edge1;

    protected RequestReplyToTopicViaThreeNetworkHopsTest.EmbeddedTcpBroker edge2;

    protected RequestReplyToTopicViaThreeNetworkHopsTest.EmbeddedTcpBroker core1;

    protected RequestReplyToTopicViaThreeNetworkHopsTest.EmbeddedTcpBroker core2;

    protected boolean testError = false;

    protected boolean fatalTestError = false;

    protected int echoResponseFill = 0;// Number of "filler" response messages per request


    protected static Log LOG;

    public boolean duplex = true;

    static {
        RequestReplyToTopicViaThreeNetworkHopsTest.LOG = LogFactory.getLog(RequestReplyToTopicViaThreeNetworkHopsTest.class);
    }

    public RequestReplyToTopicViaThreeNetworkHopsTest() throws Exception {
        edge1 = new RequestReplyToTopicViaThreeNetworkHopsTest.EmbeddedTcpBroker("edge", 1);
        edge2 = new RequestReplyToTopicViaThreeNetworkHopsTest.EmbeddedTcpBroker("edge", 2);
        core1 = new RequestReplyToTopicViaThreeNetworkHopsTest.EmbeddedTcpBroker("core", 1);
        core2 = new RequestReplyToTopicViaThreeNetworkHopsTest.EmbeddedTcpBroker("core", 2);
        // duplex is necessary to serialise sends with consumer/destination creation
        edge1.coreConnectTo(core1, duplex);
        edge2.coreConnectTo(core2, duplex);
        core1.coreConnectTo(core2, duplex);
    }

    @Test
    public void runWithTempTopicReplyTo() throws Exception {
        RequestReplyToTopicViaThreeNetworkHopsTest.EchoService echo_svc;
        RequestReplyToTopicViaThreeNetworkHopsTest.TopicTrafficGenerator traffic_gen;
        Thread start1;
        Thread start2;
        Thread start3;
        Thread start4;
        ThreadPoolExecutor clientExecPool;
        final CountDownLatch clientCompletionLatch;
        int iter;
        fatalTestError = false;
        testError = false;
        // 
        // Execute up to 20 clients at a time to simulate that load.
        // 
        clientExecPool = new ThreadPoolExecutor(RequestReplyToTopicViaThreeNetworkHopsTest.CONCURRENT_CLIENT_COUNT, RequestReplyToTopicViaThreeNetworkHopsTest.CONCURRENT_CLIENT_COUNT, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000));
        clientCompletionLatch = new CountDownLatch(RequestReplyToTopicViaThreeNetworkHopsTest.TOTAL_CLIENT_ITER);
        // Use threads to avoid startup deadlock since the first broker started waits until
        // it knows the name of the remote broker before finishing its startup, which means
        // the remote must already be running.
        start1 = new Thread() {
            @Override
            public void run() {
                try {
                    edge1.start();
                } catch (Exception ex) {
                    RequestReplyToTopicViaThreeNetworkHopsTest.LOG.error(null, ex);
                }
            }
        };
        start2 = new Thread() {
            @Override
            public void run() {
                try {
                    edge2.start();
                } catch (Exception ex) {
                    RequestReplyToTopicViaThreeNetworkHopsTest.LOG.error(null, ex);
                }
            }
        };
        start3 = new Thread() {
            @Override
            public void run() {
                try {
                    core1.start();
                } catch (Exception ex) {
                    RequestReplyToTopicViaThreeNetworkHopsTest.LOG.error(null, ex);
                }
            }
        };
        start4 = new Thread() {
            @Override
            public void run() {
                try {
                    core2.start();
                } catch (Exception ex) {
                    RequestReplyToTopicViaThreeNetworkHopsTest.LOG.error(null, ex);
                }
            }
        };
        start1.start();
        start2.start();
        start3.start();
        start4.start();
        start1.join();
        start2.join();
        start3.join();
        start4.join();
        traffic_gen = new RequestReplyToTopicViaThreeNetworkHopsTest.TopicTrafficGenerator(edge1.getConnectionUrl(), edge2.getConnectionUrl());
        traffic_gen.start();
        // 
        // Now start the echo service with that queue.
        // 
        echo_svc = new RequestReplyToTopicViaThreeNetworkHopsTest.EchoService("echo", edge1.getConnectionUrl());
        echo_svc.start();
        // 
        // Run the tests on Temp Topics.
        // 
        RequestReplyToTopicViaThreeNetworkHopsTest.LOG.info("** STARTING TEMP TOPIC TESTS");
        iter = 0;
        while ((iter < (RequestReplyToTopicViaThreeNetworkHopsTest.TOTAL_CLIENT_ITER)) && (!(fatalTestError))) {
            clientExecPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        RequestReplyToTopicViaThreeNetworkHopsTest.this.testTempTopic(edge1.getConnectionUrl(), edge2.getConnectionUrl());
                    } catch (Exception exc) {
                        RequestReplyToTopicViaThreeNetworkHopsTest.LOG.error("test exception", exc);
                        fatalTestError = true;
                        testError = true;
                    }
                    clientCompletionLatch.countDown();
                }
            });
            iter++;
        } 
        boolean allDoneOnTime = clientCompletionLatch.await(20, TimeUnit.MINUTES);
        RequestReplyToTopicViaThreeNetworkHopsTest.LOG.info(((((((("** FINISHED TEMP TOPIC TESTS AFTER " + iter) + " ITERATIONS, testError:") + (testError)) + ", fatal: ") + (fatalTestError)) + ", onTime:") + allDoneOnTime));
        Thread.sleep(100);
        echo_svc.shutdown();
        traffic_gen.shutdown();
        shutdown();
        Assert.assertTrue("test completed in time", allDoneOnTime);
        Assert.assertTrue("no errors", (!(testError)));
    }

    protected class EmbeddedTcpBroker {
        protected BrokerService brokerSvc;

        protected int brokerNum;

        protected String brokerName;

        protected String brokerId;

        protected int port;

        protected String tcpUrl;

        protected String fullUrl;

        public EmbeddedTcpBroker(String name, int number) throws Exception {
            brokerSvc = new BrokerService();
            synchronized(this.getClass()) {
                brokerNum = RequestReplyToTopicViaThreeNetworkHopsTest.Next_broker_num;
                (RequestReplyToTopicViaThreeNetworkHopsTest.Next_broker_num)++;
            }
            brokerName = name + number;
            brokerId = brokerName;
            brokerSvc.setBrokerName(brokerName);
            brokerSvc.setBrokerId(brokerId);
            brokerSvc.setPersistent(false);
            brokerSvc.setUseJmx(false);
            port = 60000 + ((brokerNum) * 10);
            tcpUrl = "tcp://127.0.0.1:" + (Integer.toString(port));
            fullUrl = (tcpUrl) + "?jms.watchTopicAdvisories=false";
            brokerSvc.addConnector(tcpUrl);
        }

        public Connection createConnection() throws URISyntaxException, JMSException {
            Connection result;
            result = org.apache.activemq.ActiveMQConnection.makeConnection(this.fullUrl);
            return result;
        }

        public String getConnectionUrl() {
            return this.fullUrl;
        }

        public void coreConnectTo(RequestReplyToTopicViaThreeNetworkHopsTest.EmbeddedTcpBroker other, boolean duplex_f) throws Exception {
            this.makeConnectionTo(other, duplex_f, true);
            this.makeConnectionTo(other, duplex_f, false);
            if (!duplex_f) {
                other.makeConnectionTo(this, duplex_f, true);
                other.makeConnectionTo(this, duplex_f, false);
            }
        }

        public void start() throws Exception {
            brokerSvc.start();
            brokerSvc.waitUntilStarted();
        }

        public void stop() throws Exception {
            brokerSvc.stop();
        }

        protected void makeConnectionTo(RequestReplyToTopicViaThreeNetworkHopsTest.EmbeddedTcpBroker other, boolean duplex_f, boolean queue_f) throws Exception {
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

            nw_conn.setNetworkTTL(3);
            nw_conn.setSuppressDuplicateQueueSubscriptions(true);
            nw_conn.setDecreaseNetworkConsumerPriority(true);
            nw_conn.setBridgeTempDestinations(queue_f);
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

        @Override
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
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.error("message client error", exc);
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
                    RequestReplyToTopicViaThreeNetworkHopsTest.LOG.info("echo client shutdown: client does not appear to be active");

            } catch (InterruptedException int_exc) {
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.warn("wait for message client shutdown interrupted", int_exc);
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
            // 
            // Stop at shutdown time or after any test error is detected.
            // 
            while ((!(shutdownInd)) && (!(fatalTestError))) {
                in_msg = msgCons.receive(100);
                if (in_msg != null) {
                    (msgCount)++;
                    checkMessage(in_msg);
                }
            } 
            msgCons.close();
        }

        protected void checkMessage(Message in_msg) throws Exception {
            int seq;
            RequestReplyToTopicViaThreeNetworkHopsTest.LOG.debug(((("received message " + (RequestReplyToTopicViaThreeNetworkHopsTest.fmtMsgInfo(in_msg))) + " from ") + (in_msg.getJMSDestination())));
            // 
            // Only check messages with a sequence number.
            // 
            if (in_msg.propertyExists("SEQ")) {
                seq = in_msg.getIntProperty("SEQ");
                if ((haveFirstSeq) && (seq != ((lastSeq) + 1))) {
                    RequestReplyToTopicViaThreeNetworkHopsTest.LOG.error(((("***ERROR*** incorrect sequence number; expected " + (Integer.toString(((lastSeq) + 1)))) + " but have ") + (Integer.toString(seq))));
                    testError = true;
                }
                lastSeq = seq;
                if ((msgCount) > (expectedCount)) {
                    RequestReplyToTopicViaThreeNetworkHopsTest.LOG.error(((("*** have more messages than expected; have " + (msgCount)) + "; expect ") + (expectedCount)));
                    testError = true;
                }
            }
            if (in_msg.propertyExists("end-of-response")) {
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.trace("received end-of-response message");
            }
        }
    }

    /**
     *
     */
    protected class EchoService extends Thread {
        protected String destName;

        protected Connection jmsConn;

        protected Session sess;

        protected MessageConsumer msg_cons;

        protected boolean Shutdown_ind;

        protected Destination req_dest;

        protected CountDownLatch waitShutdown;

        protected ThreadPoolExecutor processorPool;

        public EchoService(String dest, Connection broker_conn) throws Exception {
            destName = dest;
            jmsConn = broker_conn;
            Shutdown_ind = false;
            sess = jmsConn.createSession(false, AUTO_ACKNOWLEDGE);
            req_dest = sess.createQueue(destName);
            msg_cons = sess.createConsumer(req_dest);
            jmsConn.start();
            waitShutdown = new CountDownLatch(1);
            processorPool = new ThreadPoolExecutor(RequestReplyToTopicViaThreeNetworkHopsTest.CONCURRENT_SERVER_COUNT, RequestReplyToTopicViaThreeNetworkHopsTest.CONCURRENT_SERVER_COUNT, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000));
        }

        public EchoService(String dest, String broker_url) throws Exception {
            this(dest, ActiveMQConnection.makeConnection(broker_url));
        }

        @Override
        public void run() {
            Message req;
            try {
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.info("STARTING ECHO SERVICE");
                while (!(Shutdown_ind)) {
                    req = msg_cons.receive(100);
                    if (req != null) {
                        processorPool.execute(new RequestReplyToTopicViaThreeNetworkHopsTest.EchoRequestProcessor(sess, req));
                    }
                } 
            } catch (Exception ex) {
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.error("error processing echo service requests", ex);
            } finally {
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.info("shutting down test echo service");
                try {
                    jmsConn.stop();
                } catch (javax.jms jms_exc) {
                    RequestReplyToTopicViaThreeNetworkHopsTest.LOG.warn("error on shutting down JMS connection", jms_exc);
                }
                synchronized(this) {
                    waitShutdown.countDown();
                }
            }
        }

        /**
         * Shut down the service, waiting up to 3 seconds for the service to terminate.
         */
        public void shutdown() {
            CountDownLatch wait_l;
            synchronized(this) {
                wait_l = waitShutdown;
            }
            Shutdown_ind = true;
            try {
                if (wait_l != null) {
                    if (wait_l.await(3000, TimeUnit.MILLISECONDS))
                        RequestReplyToTopicViaThreeNetworkHopsTest.LOG.info("echo service shutdown complete");
                    else
                        RequestReplyToTopicViaThreeNetworkHopsTest.LOG.warn("timeout waiting for echo service shutdown");

                } else {
                    RequestReplyToTopicViaThreeNetworkHopsTest.LOG.info("echo service shutdown: service does not appear to be active");
                }
            } catch (InterruptedException int_exc) {
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.warn("interrupted while waiting for echo service shutdown");
            }
        }
    }

    /**
     *
     */
    protected class EchoRequestProcessor implements Runnable {
        protected Session session;

        protected Destination resp_dest;

        protected MessageProducer msg_prod;

        protected Message request;

        public EchoRequestProcessor(Session sess, Message req) throws Exception {
            this.session = sess;
            this.request = req;
            this.resp_dest = req.getJMSReplyTo();
            if ((resp_dest) == null) {
                throw new Exception("invalid request: no reply-to destination given");
            }
            this.msg_prod = session.createProducer(this.resp_dest);
        }

        @Override
        public void run() {
            try {
                this.processRequest(this.request);
            } catch (Exception ex) {
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.error("Failed to process request", ex);
            }
        }

        /**
         * Process one request for the Echo Service.
         */
        protected void processRequest(Message req) throws Exception {
            if (RequestReplyToTopicViaThreeNetworkHopsTest.LOG.isDebugEnabled())
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.debug(("ECHO request message " + (req.toString())));

            resp_dest = req.getJMSReplyTo();
            if ((resp_dest) != null) {
                msg_prod = session.createProducer(resp_dest);
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.debug(("SENDING ECHO RESPONSE to:" + (resp_dest)));
                msg_prod.send(req);
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.debug((((getConnection().getBrokerName()) + " SENT ECHO RESPONSE to ") + (resp_dest)));
                msg_prod.close();
                msg_prod = null;
            } else {
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.warn("invalid request: no reply-to destination given");
            }
        }
    }

    protected class TopicTrafficGenerator extends Thread {
        protected Connection conn1;

        protected Connection conn2;

        protected Session sess1;

        protected Session sess2;

        protected Destination dest;

        protected MessageProducer prod;

        protected MessageConsumer cons;

        protected boolean Shutdown_ind;

        protected int send_count;

        public TopicTrafficGenerator(String url1, String url2) throws Exception {
            conn1 = createConnection(url1);
            conn2 = createConnection(url2);
            sess1 = conn1.createSession(false, AUTO_ACKNOWLEDGE);
            sess2 = conn2.createSession(false, AUTO_ACKNOWLEDGE);
            conn1.start();
            conn2.start();
            dest = sess1.createTopic("traffic");
            prod = sess1.createProducer(dest);
            dest = sess2.createTopic("traffic");
            cons = sess2.createConsumer(dest);
        }

        public void shutdown() {
            Shutdown_ind = true;
        }

        @Override
        public void run() {
            Message msg;
            try {
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.info("Starting Topic Traffic Generator");
                while (!(Shutdown_ind)) {
                    msg = sess1.createTextMessage("TRAFFIC");
                    prod.send(msg);
                    (send_count)++;
                    // 
                    // Time out the receipt; early messages may not make it.
                    // 
                    msg = cons.receive(250);
                } 
            } catch (JMSException jms_exc) {
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.warn("traffic generator failed on jms exception", jms_exc);
            } finally {
                RequestReplyToTopicViaThreeNetworkHopsTest.LOG.info(("Shutdown of Topic Traffic Generator; send count = " + (send_count)));
                if ((conn1) != null) {
                    try {
                        conn1.stop();
                    } catch (JMSException jms_exc) {
                        RequestReplyToTopicViaThreeNetworkHopsTest.LOG.warn("failed to shutdown connection", jms_exc);
                    }
                }
                if ((conn2) != null) {
                    try {
                        conn2.stop();
                    } catch (JMSException jms_exc) {
                        RequestReplyToTopicViaThreeNetworkHopsTest.LOG.warn("failed to shutdown connection", jms_exc);
                    }
                }
            }
        }
    }
}

