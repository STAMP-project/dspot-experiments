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
package org.apache.activemq.jms.pool;


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Pooled connections ability to handle security exceptions
 */
public class PooledConnectionSecurityExceptionTest {
    protected static final Logger LOG = LoggerFactory.getLogger(PooledConnectionSecurityExceptionTest.class);

    @Rule
    public TestName name = new TestName();

    private BrokerService brokerService;

    private String connectionURI;

    protected PooledConnectionFactory pooledConnFact;

    @Test
    public void testFailedConnectThenSucceeds() throws JMSException {
        Connection connection = pooledConnFact.createConnection("invalid", "credentials");
        try {
            connection.start();
            Assert.fail("Should fail to connect");
        } catch (JMSSecurityException ex) {
            PooledConnectionSecurityExceptionTest.LOG.info("Caught expected security error");
        }
        connection = pooledConnFact.createConnection("system", "manager");
        connection.start();
        PooledConnectionSecurityExceptionTest.LOG.info("Successfully create new connection.");
        connection.close();
    }

    @Test
    public void testFailedConnectThenSucceedsWithListener() throws JMSException {
        Connection connection = pooledConnFact.createConnection("invalid", "credentials");
        connection.setExceptionListener(new ExceptionListener() {
            @Override
            public void onException(JMSException exception) {
                PooledConnectionSecurityExceptionTest.LOG.warn("Connection get error: {}", exception.getMessage());
            }
        });
        try {
            connection.start();
            Assert.fail("Should fail to connect");
        } catch (JMSSecurityException ex) {
            PooledConnectionSecurityExceptionTest.LOG.info("Caught expected security error");
        }
        connection = pooledConnFact.createConnection("system", "manager");
        connection.start();
        PooledConnectionSecurityExceptionTest.LOG.info("Successfully create new connection.");
        connection.close();
    }

    @Test
    public void testFailureGetsNewConnectionOnRetryLooped() throws Exception {
        for (int i = 0; i < 10; ++i) {
            testFailureGetsNewConnectionOnRetry();
        }
    }

    @Test
    public void testFailureGetsNewConnectionOnRetry() throws Exception {
        pooledConnFact.setMaxConnections(1);
        final PooledConnection connection1 = ((PooledConnection) (pooledConnFact.createConnection("invalid", "credentials")));
        try {
            connection1.start();
            Assert.fail("Should fail to connect");
        } catch (JMSSecurityException ex) {
            PooledConnectionSecurityExceptionTest.LOG.info("Caught expected security error");
        }
        // The pool should process the async error
        Assert.assertTrue("Should get new connection", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (connection1.getConnection()) != (getConnection());
            }
        }));
        final PooledConnection connection2 = ((PooledConnection) (pooledConnFact.createConnection("invalid", "credentials")));
        Assert.assertNotSame(connection1.getConnection(), connection2.getConnection());
        try {
            connection2.start();
            Assert.fail("Should fail to connect");
        } catch (JMSSecurityException ex) {
            PooledConnectionSecurityExceptionTest.LOG.info("Caught expected security error");
        } finally {
            connection2.close();
        }
        connection1.close();
    }

    @Test
    public void testFailureGetsNewConnectionOnRetryBigPool() throws JMSException {
        pooledConnFact.setMaxConnections(10);
        Connection connection1 = pooledConnFact.createConnection("invalid", "credentials");
        try {
            connection1.start();
            Assert.fail("Should fail to connect");
        } catch (JMSSecurityException ex) {
            PooledConnectionSecurityExceptionTest.LOG.info("Caught expected security error");
        }
        Connection connection2 = pooledConnFact.createConnection("invalid", "credentials");
        try {
            connection2.start();
            Assert.fail("Should fail to connect");
        } catch (JMSSecurityException ex) {
            PooledConnectionSecurityExceptionTest.LOG.info("Caught expected security error");
        }
        Assert.assertNotSame(connection1, connection2);
        connection1.close();
        connection2.close();
    }

    @Test
    public void testFailoverWithInvalidCredentialsCanConnect() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory((("failover:(" + (connectionURI)) + ")"));
        pooledConnFact = new PooledConnectionFactory();
        pooledConnFact.setConnectionFactory(cf);
        pooledConnFact.setMaxConnections(1);
        Connection connection = pooledConnFact.createConnection("invalid", "credentials");
        try {
            connection.start();
            Assert.fail("Should fail to connect");
        } catch (JMSSecurityException ex) {
            PooledConnectionSecurityExceptionTest.LOG.info("Caught expected security error");
        }
        connection = pooledConnFact.createConnection("system", "manager");
        connection.start();
        PooledConnectionSecurityExceptionTest.LOG.info("Successfully create new connection.");
        connection.close();
    }

    @Test
    public void testFailoverWithInvalidCredentials() throws Exception {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory((("failover:(" + (connectionURI)) + "?trace=true)"));
        pooledConnFact = new PooledConnectionFactory();
        pooledConnFact.setConnectionFactory(cf);
        pooledConnFact.setMaxConnections(1);
        final PooledConnection connection1 = ((PooledConnection) (pooledConnFact.createConnection("invalid", "credentials")));
        try {
            connection1.start();
            Assert.fail("Should fail to connect");
        } catch (JMSSecurityException ex) {
            PooledConnectionSecurityExceptionTest.LOG.info("Caught expected security error");
            // Intentionally don't close here to see that async pool reconnect takes place.
        }
        // The pool should process the async error
        Assert.assertTrue("Should get new connection", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (connection1.getConnection()) != (getConnection());
            }
        }));
        final PooledConnection connection2 = ((PooledConnection) (pooledConnFact.createConnection("invalid", "credentials")));
        Assert.assertNotSame(connection1.getConnection(), connection2.getConnection());
        try {
            connection2.start();
            Assert.fail("Should fail to connect");
        } catch (JMSSecurityException ex) {
            PooledConnectionSecurityExceptionTest.LOG.info("Caught expected security error");
            connection2.close();
        } finally {
            connection2.close();
        }
        connection1.close();
    }

    @Test
    public void testFailedCreateConsumerConnectionStillWorks() throws JMSException {
        Connection connection = pooledConnFact.createConnection("guest", "password");
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(name.getMethodName());
        try {
            session.createConsumer(queue);
            Assert.fail("Should fail to create consumer");
        } catch (JMSSecurityException ex) {
            PooledConnectionSecurityExceptionTest.LOG.info("Caught expected security error");
        }
        queue = session.createQueue(("GUESTS." + (name.getMethodName())));
        MessageProducer producer = session.createProducer(queue);
        producer.close();
        connection.close();
    }
}

