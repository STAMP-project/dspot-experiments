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


import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import org.apache.activemq.ActiveMQConnection;
import org.junit.Assert;
import org.junit.Test;


public class ManagedConnectionTest {
    private static final String DEFAULT_HOST = "vm://localhost?broker.persistent=false";

    private ConnectionManagerAdapter connectionManager = new ConnectionManagerAdapter();

    private ActiveMQManagedConnectionFactory managedConnectionFactory;

    private ConnectionFactory connectionFactory;

    private ManagedConnectionProxy connection;

    private ActiveMQManagedConnection managedConnection;

    @Test(timeout = 60000)
    public void testConnectionCloseEvent() throws JMSException, ResourceException {
        final boolean[] test = new boolean[]{ false };
        connectionManager.addConnectionEventListener(new ConnectionEventListenerAdapter() {
            @Override
            public void connectionClosed(ConnectionEvent arg0) {
                test[0] = true;
            }
        });
        connection.close();
        Assert.assertTrue(test[0]);
    }

    @Test(timeout = 60000)
    public void testLocalTransactionCommittedEvent() throws JMSException, ResourceException {
        final boolean[] test = new boolean[]{ false };
        connectionManager.addConnectionEventListener(new ConnectionEventListenerAdapter() {
            @Override
            public void localTransactionCommitted(ConnectionEvent arg0) {
                test[0] = true;
            }
        });
        managedConnection.getLocalTransaction().begin();
        Session session = connection.createSession(true, 0);
        doWork(session);
        session.commit();
        Assert.assertTrue(test[0]);
    }

    @Test(timeout = 60000)
    public void testLocalTransactionRollbackEvent() throws JMSException, ResourceException {
        final boolean[] test = new boolean[]{ false };
        connectionManager.addConnectionEventListener(new ConnectionEventListenerAdapter() {
            @Override
            public void localTransactionRolledback(ConnectionEvent arg0) {
                test[0] = true;
            }
        });
        managedConnection.getLocalTransaction().begin();
        Session session = connection.createSession(true, 0);
        doWork(session);
        session.rollback();
        Assert.assertTrue(test[0]);
    }

    @Test(timeout = 60000)
    public void testLocalTransactionStartedEvent() throws JMSException, ResourceException {
        final boolean[] test = new boolean[]{ false };
        connectionManager.addConnectionEventListener(new ConnectionEventListenerAdapter() {
            @Override
            public void localTransactionStarted(ConnectionEvent arg0) {
                test[0] = true;
            }
        });
        // Begin the transaction... that should kick off the event.
        managedConnection.getLocalTransaction().begin();
        Session session = connection.createSession(true, 0);
        doWork(session);
        Assert.assertTrue(test[0]);
    }

    /**
     * A managed connection that has been clean up should throw exceptions when
     * it used.
     */
    @Test(timeout = 60000)
    public void testCleanup() throws JMSException, ResourceException {
        // Do some work and close it...
        Session session = connection.createSession(true, 0);
        doWork(session);
        connection.close();
        try {
            // This should throw exception
            doWork(session);
            Assert.fail("Using a session after the connection is closed should throw exception.");
        } catch (JMSException e) {
        }
    }

    @Test(timeout = 60000)
    public void testSetClientIdAfterCleanup() throws Exception {
        connection.setClientID("test");
        try {
            connection.setClientID("test");
            Assert.fail("Should have received JMSException");
        } catch (JMSException e) {
        }
        ActiveMQConnection physicalConnection = ((ActiveMQConnection) (managedConnection.getPhysicalConnection()));
        try {
            physicalConnection.setClientID("testTwo");
            Assert.fail("Should have received JMSException");
        } catch (JMSException e) {
        }
        // close the proxy
        connection.close();
        // can set the id on the physical connection again after cleanup
        physicalConnection.setClientID("test3");
        try {
            physicalConnection.setClientID("test4");
            Assert.fail("Should have received JMSException");
        } catch (JMSException e) {
        }
    }

    @Test(timeout = 60000)
    public void testSessionCloseIndependance() throws JMSException, ResourceException {
        Session session1 = connection.createSession(true, 0);
        Session session2 = connection.createSession(true, 0);
        Assert.assertTrue((session1 != session2));
        doWork(session1);
        session1.close();
        try {
            // This should throw exception
            doWork(session1);
            Assert.fail("Using a session after the connection is closed should throw exception.");
        } catch (JMSException e) {
        }
        // Make sure that closing session 1 does not close session 2
        doWork(session2);
        session2.close();
        try {
            // This should throw exception
            doWork(session2);
            Assert.fail("Using a session after the connection is closed should throw exception.");
        } catch (JMSException e) {
        }
    }

    @Test(timeout = 60000)
    public void testImplementsQueueAndTopicConnection() throws Exception {
        QueueConnection qc = createQueueConnection();
        Assert.assertNotNull(qc);
        TopicConnection tc = createTopicConnection();
        Assert.assertNotNull(tc);
    }

    @Test(timeout = 60000)
    public void testSelfEquality() {
        assertEquality(managedConnection, managedConnection);
    }

    @Test(timeout = 60000)
    public void testSamePropertiesButNotEqual() throws Exception {
        ManagedConnectionProxy newConnection = ((ManagedConnectionProxy) (connectionFactory.createConnection()));
        assertNonEquality(managedConnection, newConnection.getManagedConnection());
        newConnection.close();
    }
}

