/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.mdb.ejb2x;


import javax.jms.Message;
import javax.jms.Queue;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.jboss.as.test.shared.TimeoutUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests EJB2.0 MDBs listening on a queue.
 *
 * @author <a href="mailto:istudens@redhat.com">Ivo Studensky</a>
 */
@RunWith(Arquillian.class)
@ServerSetup({ MDB20QueueTestCase.JmsQueueSetup.class })
public class MDB20QueueTestCase extends AbstractMDB2xTestCase {
    private Queue queue;

    private Queue replyQueue;

    static class JmsQueueSetup implements ServerSetupTask {
        private JMSOperations jmsAdminOperations;

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            jmsAdminOperations = JMSOperationsProvider.getInstance(managementClient);
            jmsAdminOperations.createJmsQueue("ejb2x/queue", "java:jboss/ejb2x/queue");
            jmsAdminOperations.createJmsQueue("ejb2x/replyQueue", "java:jboss/ejb2x/replyQueue");
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            if ((jmsAdminOperations) != null) {
                jmsAdminOperations.removeJmsQueue("ejb2x/queue");
                jmsAdminOperations.removeJmsQueue("ejb2x/replyQueue");
                jmsAdminOperations.close();
            }
        }
    }

    /**
     * Tests a simple EJB2.0 MDB.
     */
    @Test
    public void testEjb20MDB() {
        sendTextMessage(("Say hello to " + (EJB2xMDB.class.getName())), queue, replyQueue);
        final Message reply = receiveMessage(replyQueue, TimeoutUtil.adjust(1000));
        Assert.assertNotNull(("Reply message was null on reply queue: " + (replyQueue)), reply);
    }
}

