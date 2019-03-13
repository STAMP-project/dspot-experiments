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
package org.apache.activemq.ra;


import Session.AUTO_ACKNOWLEDGE;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.resource.ResourceException;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.XATerminator;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.Wait;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MDBTest {
    private static final Logger LOG = LoggerFactory.getLogger(MDBTest.class);

    private long txGenerator = System.currentTimeMillis();

    private AtomicInteger id = new AtomicInteger(0);

    private static final class StubBootstrapContext implements BootstrapContext {
        @Override
        public WorkManager getWorkManager() {
            return new WorkManager() {
                @Override
                public void doWork(Work work) throws WorkException {
                    new Thread(work).start();
                }

                @Override
                public void doWork(Work work, long arg1, ExecutionContext arg2, WorkListener arg3) throws WorkException {
                    new Thread(work).start();
                }

                @Override
                public long startWork(Work work) throws WorkException {
                    new Thread(work).start();
                    return 0;
                }

                @Override
                public long startWork(Work work, long arg1, ExecutionContext arg2, WorkListener arg3) throws WorkException {
                    new Thread(work).start();
                    return 0;
                }

                @Override
                public void scheduleWork(Work work) throws WorkException {
                    new Thread(work).start();
                }

                @Override
                public void scheduleWork(Work work, long arg1, ExecutionContext arg2, WorkListener arg3) throws WorkException {
                    new Thread(work).start();
                }
            };
        }

        @Override
        public XATerminator getXATerminator() {
            return null;
        }

        @Override
        public Timer createTimer() throws UnavailableException {
            return null;
        }
    }

    public class StubMessageEndpoint implements MessageListener , MessageEndpoint {
        public int messageCount;

        public XAResource xaresource;

        public Xid xid;

        @Override
        public void beforeDelivery(Method method) throws NoSuchMethodException, ResourceException {
            try {
                if ((xid) == null) {
                    xid = createXid();
                }
                xaresource.start(xid, 0);
            } catch (Throwable e) {
                MDBTest.LOG.info((("beforeDelivery, messageCount: " + (messageCount)) + " ex"), e);
                throw new ResourceException(e);
            }
        }

        @Override
        public void afterDelivery() throws ResourceException {
            try {
                xaresource.end(xid, 0);
                xaresource.prepare(xid);
                xaresource.commit(xid, false);
                xid = null;
            } catch (Throwable e) {
                MDBTest.LOG.info((("afterDelivery, messageCount: " + (messageCount)) + " ex"), e);
                throw new ResourceException(e);
            }
        }

        @Override
        public void release() {
            MDBTest.LOG.info(((("In release, messageCount: " + (messageCount)) + ", xid:") + (xid)));
        }

        @Override
        public void onMessage(Message message) {
            (messageCount)++;
        }
    }

    @Test(timeout = 90000)
    public void testDestinationInJndi() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer advisory = session.createConsumer(AdvisorySupport.getConsumerAdvisoryTopic(new ActiveMQQueue("TEST")));
        ActiveMQResourceAdapter adapter = new ActiveMQResourceAdapter();
        adapter.setServerUrl("vm://localhost?broker.persistent=false");
        adapter.setQueuePrefetch(1);
        adapter.start(new MDBTest.StubBootstrapContext());
        final CountDownLatch messageDelivered = new CountDownLatch(1);
        final MDBTest.StubMessageEndpoint endpoint = new MDBTest.StubMessageEndpoint() {
            @Override
            public void onMessage(Message message) {
                super.onMessage(message);
                messageDelivered.countDown();
            }
        };
        ActiveMQActivationSpec activationSpec = new ActiveMQActivationSpec();
        activationSpec.setDestinationType(Queue.class.getName());
        activationSpec.setDestination("MyQueue");
        activationSpec.setUseJndi(true);
        activationSpec.setResourceAdapter(adapter);
        activationSpec.validate();
        MessageEndpointFactory messageEndpointFactory = new MessageEndpointFactory() {
            @Override
            public MessageEndpoint createEndpoint(XAResource resource) throws UnavailableException {
                endpoint.xaresource = resource;
                return endpoint;
            }

            @Override
            public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
                return true;
            }
        };
        // Activate an Endpoint
        adapter.endpointActivation(messageEndpointFactory, activationSpec);
        ActiveMQMessage msg = ((ActiveMQMessage) (advisory.receive(1000)));
        if (msg != null) {
            Assert.assertEquals("Prefetch size hasn't been set", 1, getPrefetchSize());
        } else {
            Assert.fail("Consumer hasn't been created");
        }
        // Send the broker a message to that endpoint
        MessageProducer producer = session.createProducer(new ActiveMQQueue("TEST"));
        producer.send(session.createTextMessage("Hello!"));
        connection.close();
        // Wait for the message to be delivered.
        Assert.assertTrue(messageDelivered.await(5000, TimeUnit.MILLISECONDS));
        // Shut the Endpoint down.
        adapter.endpointDeactivation(messageEndpointFactory, activationSpec);
        adapter.stop();
    }

    @Test(timeout = 90000)
    public void testMessageDelivery() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer advisory = session.createConsumer(AdvisorySupport.getConsumerAdvisoryTopic(new ActiveMQQueue("TEST")));
        ActiveMQResourceAdapter adapter = new ActiveMQResourceAdapter();
        adapter.setServerUrl("vm://localhost?broker.persistent=false");
        adapter.setQueuePrefetch(1);
        adapter.start(new MDBTest.StubBootstrapContext());
        final CountDownLatch messageDelivered = new CountDownLatch(1);
        final MDBTest.StubMessageEndpoint endpoint = new MDBTest.StubMessageEndpoint() {
            @Override
            public void onMessage(Message message) {
                super.onMessage(message);
                messageDelivered.countDown();
            }
        };
        ActiveMQActivationSpec activationSpec = new ActiveMQActivationSpec();
        activationSpec.setDestinationType(Queue.class.getName());
        activationSpec.setDestination("TEST");
        activationSpec.setResourceAdapter(adapter);
        activationSpec.validate();
        MessageEndpointFactory messageEndpointFactory = new MessageEndpointFactory() {
            @Override
            public MessageEndpoint createEndpoint(XAResource resource) throws UnavailableException {
                endpoint.xaresource = resource;
                return endpoint;
            }

            @Override
            public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
                return true;
            }
        };
        // Activate an Endpoint
        adapter.endpointActivation(messageEndpointFactory, activationSpec);
        ActiveMQMessage msg = ((ActiveMQMessage) (advisory.receive(1000)));
        if (msg != null) {
            Assert.assertEquals("Prefetch size hasn't been set", 1, getPrefetchSize());
        } else {
            Assert.fail("Consumer hasn't been created");
        }
        // Send the broker a message to that endpoint
        MessageProducer producer = session.createProducer(new ActiveMQQueue("TEST"));
        producer.send(session.createTextMessage("Hello!"));
        connection.close();
        // Wait for the message to be delivered.
        Assert.assertTrue(messageDelivered.await(5000, TimeUnit.MILLISECONDS));
        // Shut the Endpoint down.
        adapter.endpointDeactivation(messageEndpointFactory, activationSpec);
        adapter.stop();
    }

    @Test
    public void testParallelMessageDelivery() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        ActiveMQResourceAdapter adapter = new ActiveMQResourceAdapter();
        adapter.setServerUrl("vm://localhost?broker.persistent=false");
        adapter.start(new MDBTest.StubBootstrapContext());
        final CountDownLatch messageDelivered = new CountDownLatch(10);
        final MDBTest.StubMessageEndpoint endpoint = new MDBTest.StubMessageEndpoint() {
            @Override
            public void beforeDelivery(Method method) throws NoSuchMethodException, ResourceException {
            }

            @Override
            public void afterDelivery() throws ResourceException {
            }

            public void onMessage(Message message) {
                MDBTest.LOG.info(("Message:" + message));
                super.onMessage(message);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                messageDelivered.countDown();
            }
        };
        ActiveMQActivationSpec activationSpec = new ActiveMQActivationSpec();
        activationSpec.setDestinationType(Queue.class.getName());
        activationSpec.setDestination("TEST");
        activationSpec.setResourceAdapter(adapter);
        activationSpec.validate();
        MessageEndpointFactory messageEndpointFactory = new MessageEndpointFactory() {
            public MessageEndpoint createEndpoint(XAResource resource) throws UnavailableException {
                endpoint.xaresource = resource;
                return endpoint;
            }

            public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
                return false;
            }
        };
        // Activate an Endpoint
        adapter.endpointActivation(messageEndpointFactory, activationSpec);
        // Send the broker a message to that endpoint
        MessageProducer producer = session.createProducer(new ActiveMQQueue("TEST"));
        for (int i = 0; i < 10; i++) {
            producer.send(session.createTextMessage((i + "-Hello!")));
        }
        connection.close();
        // Wait for the message to be delivered.
        Assert.assertTrue(messageDelivered.await(5000, TimeUnit.MILLISECONDS));
        // Shut the Endpoint down.
        adapter.endpointDeactivation(messageEndpointFactory, activationSpec);
        adapter.stop();
    }

    // https://issues.apache.org/jira/browse/AMQ-5811
    @Test(timeout = 90000)
    public void testAsyncStop() throws Exception {
        for (int repeat = 0; repeat < 10; repeat++) {
            ActiveMQResourceAdapter adapter = new ActiveMQResourceAdapter();
            adapter.setServerUrl("vm://localhost?broker.persistent=false");
            adapter.setQueuePrefetch(1);
            adapter.start(new MDBTest.StubBootstrapContext());
            final int num = 20;
            MessageEndpointFactory[] endpointFactories = new MessageEndpointFactory[num];
            ActiveMQActivationSpec[] activationSpecs = new ActiveMQActivationSpec[num];
            for (int i = 0; i < num; i++) {
                final MDBTest.StubMessageEndpoint endpoint = new MDBTest.StubMessageEndpoint() {
                    @Override
                    public void onMessage(Message message) {
                        super.onMessage(message);
                    }
                };
                activationSpecs[i] = new ActiveMQActivationSpec();
                activationSpecs[i].setDestinationType(Queue.class.getName());
                activationSpecs[i].setDestination(("TEST" + i));
                activationSpecs[i].setResourceAdapter(adapter);
                activationSpecs[i].validate();
                endpointFactories[i] = new MessageEndpointFactory() {
                    @Override
                    public MessageEndpoint createEndpoint(XAResource resource) throws UnavailableException {
                        endpoint.xaresource = resource;
                        return endpoint;
                    }

                    @Override
                    public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
                        return true;
                    }
                };
                // Activate an Endpoint
                adapter.endpointActivation(endpointFactories[i], activationSpecs[i]);
            }
            // spawn num threads to deactivate
            Thread[] threads = asyncDeactivate(adapter, endpointFactories, activationSpecs);
            for (int i = 0; i < (threads.length); i++) {
                threads[i].start();
            }
            adapter.stop();
            for (int i = 0; i < (threads.length); i++) {
                threads[i].join();
            }
        }
    }

    @Test(timeout = 90000)
    public void testErrorOnNoMessageDeliveryBrokerZeroPrefetchConfig() throws Exception {
        final BrokerService brokerService = new BrokerService();
        final String brokerUrl = "vm://zeroPrefetch?create=false";
        brokerService.setBrokerName("zeroPrefetch");
        brokerService.setPersistent(false);
        PolicyMap policyMap = new PolicyMap();
        PolicyEntry zeroPrefetchPolicy = new PolicyEntry();
        zeroPrefetchPolicy.setQueuePrefetch(0);
        policyMap.setDefaultEntry(zeroPrefetchPolicy);
        brokerService.setDestinationPolicy(policyMap);
        brokerService.start();
        final AtomicReference<String> errorMessage = new AtomicReference<String>();
        final Appender testAppender = new Appender() {
            @Override
            public void addFilter(Filter filter) {
            }

            @Override
            public Filter getFilter() {
                return null;
            }

            @Override
            public void clearFilters() {
            }

            @Override
            public void close() {
            }

            @Override
            public void doAppend(LoggingEvent event) {
                if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
                    System.err.println(("Event :" + (event.getRenderedMessage())));
                    errorMessage.set(event.getRenderedMessage());
                }
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void setErrorHandler(ErrorHandler errorHandler) {
            }

            @Override
            public ErrorHandler getErrorHandler() {
                return null;
            }

            @Override
            public void setLayout(Layout layout) {
            }

            @Override
            public Layout getLayout() {
                return null;
            }

            @Override
            public void setName(String s) {
            }

            @Override
            public boolean requiresLayout() {
                return false;
            }
        };
        LogManager.getRootLogger().addAppender(testAppender);
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer advisory = session.createConsumer(AdvisorySupport.getConsumerAdvisoryTopic(new ActiveMQQueue("TEST")));
        ActiveMQResourceAdapter adapter = new ActiveMQResourceAdapter();
        adapter.setServerUrl(brokerUrl);
        adapter.start(new MDBTest.StubBootstrapContext());
        final CountDownLatch messageDelivered = new CountDownLatch(1);
        final MDBTest.StubMessageEndpoint endpoint = new MDBTest.StubMessageEndpoint() {
            @Override
            public void onMessage(Message message) {
                super.onMessage(message);
                messageDelivered.countDown();
            }
        };
        ActiveMQActivationSpec activationSpec = new ActiveMQActivationSpec();
        activationSpec.setDestinationType(Queue.class.getName());
        activationSpec.setDestination("TEST");
        activationSpec.setResourceAdapter(adapter);
        activationSpec.validate();
        MessageEndpointFactory messageEndpointFactory = new MessageEndpointFactory() {
            @Override
            public MessageEndpoint createEndpoint(XAResource resource) throws UnavailableException {
                endpoint.xaresource = resource;
                return endpoint;
            }

            @Override
            public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
                return true;
            }
        };
        // Activate an Endpoint
        adapter.endpointActivation(messageEndpointFactory, activationSpec);
        ActiveMQMessage msg = ((ActiveMQMessage) (advisory.receive(4000)));
        if (msg != null) {
            Assert.assertEquals("Prefetch size hasn't been set", 0, getPrefetchSize());
        } else {
            Assert.fail("Consumer hasn't been created");
        }
        // Send the broker a message to that endpoint
        MessageProducer producer = session.createProducer(new ActiveMQQueue("TEST"));
        producer.send(session.createTextMessage("Hello!"));
        connection.close();
        // Wait for the message to be delivered.
        Assert.assertFalse(messageDelivered.await(5000, TimeUnit.MILLISECONDS));
        // Shut the Endpoint down.
        adapter.endpointDeactivation(messageEndpointFactory, activationSpec);
        adapter.stop();
        Assert.assertNotNull("We got an error message", errorMessage.get());
        Assert.assertTrue(("correct message: " + (errorMessage.get())), errorMessage.get().contains("zero"));
        LogManager.getRootLogger().removeAppender(testAppender);
        brokerService.stop();
    }

    @Test
    public void testMessageExceptionReDelivery() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        ActiveMQResourceAdapter adapter = new ActiveMQResourceAdapter();
        adapter.setServerUrl("vm://localhost?broker.persistent=false");
        adapter.start(new MDBTest.StubBootstrapContext());
        final CountDownLatch messageDelivered = new CountDownLatch(5);
        final AtomicLong timeReceived = new AtomicLong();
        final AtomicBoolean failed = new AtomicBoolean(false);
        final MDBTest.StubMessageEndpoint endpoint = new MDBTest.StubMessageEndpoint() {
            @Override
            public void onMessage(Message message) {
                super.onMessage(message);
                try {
                    long now = System.currentTimeMillis();
                    if ((timeReceived.get()) == 0) {
                        timeReceived.set(now);
                    }
                    if ((now - (timeReceived.getAndSet(now))) >= 1000) {
                        failed.set(true);
                    }
                    messageDelivered.countDown();
                    if (!(messageDelivered.await(1, TimeUnit.MILLISECONDS))) {
                        throw new RuntimeException(("ex on delivery: " + (messageDelivered.getCount())));
                    } else {
                        try {
                            Assert.assertTrue(message.getJMSRedelivered());
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException ignored) {
                }
            }

            @Override
            public void afterDelivery() throws ResourceException {
                try {
                    if (!(messageDelivered.await(1, TimeUnit.MILLISECONDS))) {
                        xaresource.end(xid, XAResource.TMFAIL);
                        xaresource.rollback(xid);
                    } else {
                        xaresource.end(xid, XAResource.TMSUCCESS);
                        xaresource.prepare(xid);
                        xaresource.commit(xid, false);
                    }
                } catch (Throwable e) {
                    throw new ResourceException(e);
                }
            }
        };
        ActiveMQActivationSpec activationSpec = new ActiveMQActivationSpec();
        activationSpec.setDestinationType(Queue.class.getName());
        activationSpec.setDestination("TEST");
        activationSpec.setInitialRedeliveryDelay(100);
        activationSpec.setResourceAdapter(adapter);
        activationSpec.validate();
        MessageEndpointFactory messageEndpointFactory = new MessageEndpointFactory() {
            @Override
            public MessageEndpoint createEndpoint(XAResource resource) throws UnavailableException {
                endpoint.xaresource = resource;
                return endpoint;
            }

            @Override
            public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
                return true;
            }
        };
        // Activate an Endpoint
        adapter.endpointActivation(messageEndpointFactory, activationSpec);
        // Give endpoint a chance to setup and register its listeners
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        timeReceived.set(0);
        // Send the broker a message to that endpoint
        MessageProducer producer = session.createProducer(new ActiveMQQueue("TEST"));
        producer.send(session.createTextMessage("Hello!"));
        connection.close();
        // Wait for the message to be delivered.
        Assert.assertTrue(messageDelivered.await(10000, TimeUnit.MILLISECONDS));
        Assert.assertFalse("Delivery policy delay not working", failed.get());
        // Shut the Endpoint down.
        adapter.endpointDeactivation(messageEndpointFactory, activationSpec);
        adapter.stop();
    }

    @Test(timeout = 90000)
    public void testOrderOfMessageExceptionReDelivery() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        ActiveMQResourceAdapter adapter = new ActiveMQResourceAdapter();
        adapter.setServerUrl("vm://localhost?broker.persistent=false");
        adapter.start(new MDBTest.StubBootstrapContext());
        final String ORDER_PROP = "Order";
        final List<Integer> orderedReceipt = new ArrayList<Integer>();
        final MDBTest.StubMessageEndpoint endpoint = new MDBTest.StubMessageEndpoint() {
            @Override
            public void onMessage(Message message) {
                super.onMessage(message);
                if ((messageCount) == 2) {
                    throw new RuntimeException("Throwing on two");
                }
                try {
                    orderedReceipt.add(message.getIntProperty(ORDER_PROP));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterDelivery() throws ResourceException {
                try {
                    if ((messageCount) == 2) {
                        xaresource.end(xid, XAResource.TMFAIL);
                        xaresource.rollback(xid);
                    } else {
                        xaresource.end(xid, XAResource.TMSUCCESS);
                        xaresource.prepare(xid);
                        xaresource.commit(xid, false);
                    }
                } catch (Throwable e) {
                    MDBTest.LOG.info((("afterDelivery messageCount: " + (messageCount)) + " ex"), e);
                    throw new ResourceException(e);
                } finally {
                    xid = null;
                }
            }
        };
        ActiveMQActivationSpec activationSpec = new ActiveMQActivationSpec();
        activationSpec.setDestinationType(Queue.class.getName());
        activationSpec.setDestination("TEST");
        activationSpec.setInitialRedeliveryDelay(100);
        activationSpec.setMaxSessions("1");
        activationSpec.setResourceAdapter(adapter);
        activationSpec.validate();
        MessageEndpointFactory messageEndpointFactory = new MessageEndpointFactory() {
            @Override
            public MessageEndpoint createEndpoint(XAResource resource) throws UnavailableException {
                endpoint.xaresource = resource;
                return endpoint;
            }

            @Override
            public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
                return true;
            }
        };
        // Activate an Endpoint
        adapter.endpointActivation(messageEndpointFactory, activationSpec);
        // Give endpoint a chance to setup and register its listeners
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        // Send the broker a message to that endpoint
        MessageProducer producer = session.createProducer(new ActiveMQQueue("TEST"));
        for (int i = 0; i < 5; i++) {
            Message message = session.createTextMessage("Hello!");
            message.setIntProperty(ORDER_PROP, i);
            producer.send(message);
        }
        connection.close();
        Assert.assertTrue("Got 5", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                MDBTest.LOG.info(("Ordered size: " + (orderedReceipt.size())));
                return (orderedReceipt.size()) == 5;
            }
        }));
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals("in order", Integer.valueOf(i), orderedReceipt.remove(0));
        }
        // Shut the Endpoint down.
        adapter.endpointDeactivation(messageEndpointFactory, activationSpec);
        adapter.stop();
    }

    @Test(timeout = 90000)
    public void testXaTimeoutRedelivery() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        ActiveMQResourceAdapter adapter = new ActiveMQResourceAdapter();
        adapter.setServerUrl("vm://localhost?broker.persistent=false");
        adapter.start(new MDBTest.StubBootstrapContext());
        final CountDownLatch messageDelivered = new CountDownLatch(2);
        final MDBTest.StubMessageEndpoint endpoint = new MDBTest.StubMessageEndpoint() {
            @Override
            public void onMessage(Message message) {
                super.onMessage(message);
                try {
                    messageDelivered.countDown();
                    if (!(messageDelivered.await(1, TimeUnit.MILLISECONDS))) {
                        // simulate abort, timeout
                        try {
                            xaresource.end(xid, XAResource.TMFAIL);
                            xaresource.rollback(xid);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            Assert.assertTrue(message.getJMSRedelivered());
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException ignored) {
                }
            }

            @Override
            public void afterDelivery() throws ResourceException {
                try {
                    xaresource.end(xid, XAResource.TMSUCCESS);
                    xaresource.commit(xid, true);
                } catch (Throwable e) {
                    throw new ResourceException(e);
                }
            }
        };
        ActiveMQActivationSpec activationSpec = new ActiveMQActivationSpec();
        activationSpec.setDestinationType(Queue.class.getName());
        activationSpec.setDestination("TEST");
        activationSpec.setResourceAdapter(adapter);
        activationSpec.validate();
        MessageEndpointFactory messageEndpointFactory = new MessageEndpointFactory() {
            @Override
            public MessageEndpoint createEndpoint(XAResource resource) throws UnavailableException {
                endpoint.xaresource = resource;
                return endpoint;
            }

            @Override
            public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
                return true;
            }
        };
        // Activate an Endpoint
        adapter.endpointActivation(messageEndpointFactory, activationSpec);
        // Give endpoint a chance to setup and register its listeners
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        // Send the broker a message to that endpoint
        MessageProducer producer = session.createProducer(new ActiveMQQueue("TEST"));
        producer.send(session.createTextMessage("Hello!"));
        connection.close();
        // Wait for the message to be delivered twice.
        Assert.assertTrue(messageDelivered.await(10000, TimeUnit.MILLISECONDS));
        // Shut the Endpoint down.
        adapter.endpointDeactivation(messageEndpointFactory, activationSpec);
        adapter.stop();
    }

    @Test(timeout = 90000)
    public void testXaOnMessageExceptionRollback() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        ActiveMQResourceAdapter adapter = new ActiveMQResourceAdapter();
        adapter.setServerUrl("vm://localhost?broker.persistent=false");
        adapter.start(new MDBTest.StubBootstrapContext());
        final CountDownLatch messageDelivered = new CountDownLatch(1);
        final MDBTest.StubMessageEndpoint endpoint = new MDBTest.StubMessageEndpoint() {
            @Override
            public void onMessage(Message message) {
                super.onMessage(message);
                messageDelivered.countDown();
                throw new RuntimeException("Failure");
            }

            @Override
            public void afterDelivery() throws ResourceException {
                try {
                    xaresource.end(xid, XAResource.TMSUCCESS);
                    xaresource.commit(xid, true);
                } catch (Throwable e) {
                    throw new ResourceException(e);
                }
            }
        };
        ActiveMQActivationSpec activationSpec = new ActiveMQActivationSpec();
        activationSpec.setDestinationType(Queue.class.getName());
        activationSpec.setDestination("TEST");
        activationSpec.setResourceAdapter(adapter);
        activationSpec.validate();
        MessageEndpointFactory messageEndpointFactory = new MessageEndpointFactory() {
            @Override
            public MessageEndpoint createEndpoint(XAResource resource) throws UnavailableException {
                endpoint.xaresource = resource;
                return endpoint;
            }

            @Override
            public boolean isDeliveryTransacted(Method method) throws NoSuchMethodException {
                return true;
            }
        };
        // Activate an Endpoint
        adapter.endpointActivation(messageEndpointFactory, activationSpec);
        // Give endpoint a chance to setup and register its listeners
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        // Send the broker a message to that endpoint
        MessageProducer producer = session.createProducer(new ActiveMQQueue("TEST"));
        producer.send(session.createTextMessage("Hello!"));
        // Wait for the message to be delivered twice.
        Assert.assertTrue(messageDelivered.await(10000, TimeUnit.MILLISECONDS));
        // Shut the Endpoint down.
        adapter.endpointDeactivation(messageEndpointFactory, activationSpec);
        adapter.stop();
        // assert message still available
        MessageConsumer messageConsumer = session.createConsumer(new ActiveMQQueue("TEST"));
        Assert.assertNotNull("got the message", messageConsumer.receive(5000));
        connection.close();
    }
}

