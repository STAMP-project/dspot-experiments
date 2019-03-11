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
package org.apache.activemq.transport.amqp.interop;


import org.apache.activemq.transport.amqp.client.AmqpClient;
import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpConnection;
import org.apache.activemq.transport.amqp.client.AmqpReceiver;
import org.apache.activemq.transport.amqp.client.AmqpSession;
import org.apache.activemq.transport.amqp.client.AmqpValidator;
import org.apache.qpid.proton.engine.Receiver;
import org.apache.qpid.proton.engine.Session;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test for creation and configuration of AMQP sessions.
 */
public class AmqpSessionTest extends AmqpClientTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(AmqpSessionTest.class);

    @Test
    public void testCreateSession() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertNotNull(session);
        Session protonSession = session.getSession();
        try {
            protonSession.close();
            Assert.fail("Should not be able to mutate.");
        } catch (UnsupportedOperationException ex) {
        }
        try {
            protonSession.free();
            Assert.fail("Should not be able to mutate.");
        } catch (UnsupportedOperationException ex) {
        }
        try {
            protonSession.getConnection().close();
            Assert.fail("Should not be able to mutate.");
        } catch (UnsupportedOperationException ex) {
        }
        try {
            protonSession.open();
            Assert.fail("Should not be able to mutate.");
        } catch (UnsupportedOperationException ex) {
        }
        Assert.assertNull(protonSession.getProperties());
        Assert.assertNull(protonSession.getOfferedCapabilities());
        Assert.assertNotNull(protonSession.getContext());
        try {
            protonSession.receiver("sender");
            Assert.fail("Should not be able to mutate.");
        } catch (UnsupportedOperationException ex) {
        }
        connection.close();
    }

    @Test(timeout = 60000)
    public void testSessionClosedDoesNotGetReceiverDetachFromRemote() throws Exception {
        AmqpClient client = createAmqpClient();
        Assert.assertNotNull(client);
        client.setValidator(new AmqpValidator() {
            @Override
            public void inspectClosedResource(Session session) {
                AmqpSessionTest.LOG.info("Session closed: {}", session.getContext());
            }

            @Override
            public void inspectDetachedResource(Receiver receiver) {
                markAsInvalid("Broker should not detach receiver linked to closed session.");
            }

            @Override
            public void inspectClosedResource(Receiver receiver) {
                markAsInvalid("Broker should not close receiver linked to closed session.");
            }
        });
        AmqpConnection connection = trackConnection(client.connect());
        Assert.assertNotNull(connection);
        AmqpSession session = connection.createSession();
        Assert.assertNotNull(session);
        AmqpReceiver receiver = session.createReceiver(("queue://" + (getTestName())));
        Assert.assertNotNull(receiver);
        session.close();
        connection.getStateInspector().assertValid();
        connection.close();
    }
}

