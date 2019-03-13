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
package org.apache.activemq.security;


import Session.AUTO_ACKNOWLEDGE;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP") })
@ApplyLdifFiles("org/apache/activemq/security/activemq-apacheds-legacy.ldif")
public class CachedLDAPSecurityLegacyTest extends AbstractLdapTestUnit {
    public BrokerService broker;

    public static LdapServer ldapServer;

    @Test
    public void testSendReceive() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        Connection conn = factory.createQueueConnection("jdoe", "sunflower");
        Session sess = conn.createSession(false, AUTO_ACKNOWLEDGE);
        conn.start();
        Queue queue = sess.createQueue("TEST.FOO");
        MessageProducer producer = sess.createProducer(queue);
        MessageConsumer consumer = sess.createConsumer(queue);
        producer.send(sess.createTextMessage("test"));
        Message msg = consumer.receive(1000);
        Assert.assertNotNull(msg);
    }

    @Test
    public void testSendDenied() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        Connection conn = factory.createQueueConnection("jdoe", "sunflower");
        Session sess = conn.createSession(false, AUTO_ACKNOWLEDGE);
        conn.start();
        Queue queue = sess.createQueue("ADMIN.FOO");
        try {
            sess.createProducer(queue);
            Assert.fail("expect auth exception");
        } catch (JMSException expected) {
        }
    }

    @Test
    public void testCompositeSendDenied() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        Connection conn = factory.createQueueConnection("jdoe", "sunflower");
        Session sess = conn.createSession(false, AUTO_ACKNOWLEDGE);
        conn.start();
        Queue queue = sess.createQueue("TEST.FOO,ADMIN.FOO");
        try {
            sess.createProducer(queue);
            Assert.fail("expect auth exception");
        } catch (JMSException expected) {
        }
    }

    @Test
    public void testTempDestinations() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        Connection conn = factory.createQueueConnection("jdoe", "sunflower");
        Session sess = conn.createSession(false, AUTO_ACKNOWLEDGE);
        conn.start();
        Queue queue = sess.createTemporaryQueue();
        MessageProducer producer = sess.createProducer(queue);
        MessageConsumer consumer = sess.createConsumer(queue);
        producer.send(sess.createTextMessage("test"));
        Message msg = consumer.receive(1000);
        Assert.assertNotNull(msg);
    }
}

