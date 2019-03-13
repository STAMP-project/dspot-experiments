/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.ejb.mdb;


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.jms.auxiliary.CreateQueueSetupTask;
import org.jboss.as.test.shared.TimeoutUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
@RunWith(Arquillian.class)
@ServerSetup(CreateQueueSetupTask.class)
public class JMSMessageDrivenBeanTestCase {
    @Test
    public void testSendMessage() throws JMSException, NamingException {
        final InitialContext ctx = new InitialContext();
        final QueueConnectionFactory factory = ((QueueConnectionFactory) (ctx.lookup("java:/JmsXA")));
        final QueueConnection connection = factory.createQueueConnection();
        connection.start();
        try {
            final QueueSession session = connection.createQueueSession(false, AUTO_ACKNOWLEDGE);
            final Queue replyDestination = session.createTemporaryQueue();
            final QueueReceiver receiver = session.createReceiver(replyDestination);
            final Message message = session.createTextMessage("Test");
            message.setJMSReplyTo(replyDestination);
            final Destination destination = ((Destination) (ctx.lookup("queue/myAwesomeQueue")));
            final MessageProducer producer = session.createProducer(destination);
            producer.send(message);
            producer.close();
            final Message reply = receiver.receive(TimeoutUtil.adjust(5000));
            Assert.assertNotNull(reply);
            final String result = getText();
            Assert.assertEquals("replying Test", result);
        } finally {
            connection.close();
        }
    }
}

