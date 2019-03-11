/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.smoke.jms;


import javax.ejb.EJB;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.jms.auxiliary.CreateTopicSetupTask;
import org.jboss.as.test.smoke.jms.auxiliary.TransactedTopicMessageSender;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests sending JMS messages to a topic within a transaction
 *
 * @author <a href="jmartisk@redhat.com">Jan Martiska</a>
 */
@RunWith(Arquillian.class)
@ServerSetup(CreateTopicSetupTask.class)
public class SendToTopicFromWithinTransactionTest {
    private static final Logger logger = Logger.getLogger(SendToTopicFromWithinTransactionTest.class);

    @EJB
    private TransactedTopicMessageSender sender;

    @EJB
    private TransactedTopicMessageSender sender2;

    private static volatile boolean messageReceived;

    @Test
    public void sendSuccessfully() throws Exception {
        try {
            sender.sendToTopicSuccessfully();
            Thread.sleep(2000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Assert.assertTrue(SendToTopicFromWithinTransactionTest.messageReceived);
    }

    @Test
    public void sendAndRollback() {
        try {
            sender2.sendToTopicAndRollback();
            Thread.sleep(2000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Assert.assertFalse(SendToTopicFromWithinTransactionTest.messageReceived);
    }
}

