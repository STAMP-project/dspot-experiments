/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.manualmode.messaging.ha;


import javax.naming.InitialContext;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.jboss.logging.Logger;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2015 Red Hat inc.
 */
public abstract class FailoverTestCase extends AbstractMessagingHATestCase {
    private final Logger log = Logger.getLogger(FailoverTestCase.class);

    protected final String jmsQueueName = "FailoverTestCase-Queue";

    protected final String jmsQueueLookup = "jms/" + (jmsQueueName);

    @Test
    public void testBackupActivation() throws Exception {
        ModelControllerClient client2 = AbstractMessagingHATestCase.createClient2();
        JMSOperations jmsOperations2 = JMSOperationsProvider.getInstance(client2);
        AbstractMessagingHATestCase.checkJMSQueue(jmsOperations2, jmsQueueName, false);
        InitialContext context1 = AbstractMessagingHATestCase.createJNDIContextFromServer1();
        AbstractMessagingHATestCase.sendAndReceiveMessage(context1, jmsQueueLookup);
        // send a message to server1 before it is stopped
        String text = "sent to server1, received from server2 (after failover)";
        AbstractMessagingHATestCase.sendMessage(context1, jmsQueueLookup, text);
        context1.close();
        log.trace("===================");
        log.trace("STOP SERVER1...");
        log.trace("===================");
        AbstractMessagingHATestCase.container.stop(AbstractMessagingHATestCase.SERVER1);
        // let some time for the backup to detect the failure
        AbstractMessagingHATestCase.waitForHornetQServerActivation(jmsOperations2, true);
        AbstractMessagingHATestCase.checkJMSQueue(jmsOperations2, jmsQueueName, true);
        InitialContext context2 = AbstractMessagingHATestCase.createJNDIContextFromServer2();
        // receive the message that was sent to server1 before failover occurs
        AbstractMessagingHATestCase.receiveMessage(context2, jmsQueueLookup, text);
        AbstractMessagingHATestCase.sendAndReceiveMessage(context2, jmsQueueLookup);
        String text2 = "sent to server2, received from server 1 (after failback)";
        AbstractMessagingHATestCase.sendMessage(context2, jmsQueueLookup, text2);
        context2.close();
        log.trace("====================");
        log.trace("START SERVER1...");
        log.trace("====================");
        // restart the live server
        AbstractMessagingHATestCase.container.start(AbstractMessagingHATestCase.SERVER1);
        // let some time for the backup to detect the live node and failback
        ModelControllerClient client1 = AbstractMessagingHATestCase.createClient1();
        JMSOperations jmsOperations1 = JMSOperationsProvider.getInstance(client1);
        AbstractMessagingHATestCase.waitForHornetQServerActivation(jmsOperations1, true);
        AbstractMessagingHATestCase.checkHornetQServerStartedAndActiveAttributes(jmsOperations1, true, true);
        // let some time for the backup to detect the failure
        AbstractMessagingHATestCase.waitForHornetQServerActivation(jmsOperations2, false);
        // backup server has been restarted in passive mode
        AbstractMessagingHATestCase.checkHornetQServerStartedAndActiveAttributes(jmsOperations2, true, false);
        AbstractMessagingHATestCase.checkJMSQueue(jmsOperations2, jmsQueueName, false);
        context1 = AbstractMessagingHATestCase.createJNDIContextFromServer1();
        // receive the message that was sent to server2 before failback
        AbstractMessagingHATestCase.receiveMessage(context1, jmsQueueLookup, text2);
        // send & receive a message from server1
        AbstractMessagingHATestCase.sendAndReceiveMessage(context1, jmsQueueLookup);
        context1.close();
        log.trace("=============================");
        log.trace("RETURN TO NORMAL OPERATION...");
        log.trace("=============================");
    }

    @Test
    public void testBackupFailoverAfterFailback() throws Exception {
        ModelControllerClient client2 = AbstractMessagingHATestCase.createClient2();
        JMSOperations backupJMSOperations = JMSOperationsProvider.getInstance(client2);
        AbstractMessagingHATestCase.checkJMSQueue(backupJMSOperations, jmsQueueName, false);
        InitialContext context1 = AbstractMessagingHATestCase.createJNDIContextFromServer1();
        String text = "sent to server1, received from server2 (after failover)";
        AbstractMessagingHATestCase.sendMessage(context1, jmsQueueLookup, text);
        context1.close();
        log.trace("############## 1 #############");
        // listSharedStoreDir();
        log.trace("===================");
        log.trace("STOP SERVER1...");
        log.trace("===================");
        AbstractMessagingHATestCase.container.stop(AbstractMessagingHATestCase.SERVER1);
        log.trace("############## 2 #############");
        // listSharedStoreDir();
        // let some time for the backup to detect the failure
        AbstractMessagingHATestCase.waitForHornetQServerActivation(backupJMSOperations, true);
        AbstractMessagingHATestCase.checkJMSQueue(backupJMSOperations, jmsQueueName, true);
        InitialContext context2 = AbstractMessagingHATestCase.createJNDIContextFromServer2();
        // receive the message that was sent to server1 before failover occurs
        AbstractMessagingHATestCase.receiveMessage(context2, jmsQueueLookup, text);
        // send a message to server2 before server1 fails back
        String text2 = "sent to server2, received from server 1 (after failback)";
        AbstractMessagingHATestCase.sendMessage(context2, jmsQueueLookup, text2);
        context2.close();
        log.trace("====================");
        log.trace("START SERVER1...");
        log.trace("====================");
        // restart the live server
        AbstractMessagingHATestCase.container.start(AbstractMessagingHATestCase.SERVER1);
        // let some time for the backup to detect the live node and failback
        ModelControllerClient client1 = AbstractMessagingHATestCase.createClient1();
        JMSOperations liveJMSOperations = JMSOperationsProvider.getInstance(client1);
        AbstractMessagingHATestCase.waitForHornetQServerActivation(liveJMSOperations, true);
        AbstractMessagingHATestCase.checkHornetQServerStartedAndActiveAttributes(liveJMSOperations, true, true);
        // let some time for the backup to detect the failure
        AbstractMessagingHATestCase.waitForHornetQServerActivation(backupJMSOperations, false);
        // backup server has been restarted in passive mode
        AbstractMessagingHATestCase.checkHornetQServerStartedAndActiveAttributes(backupJMSOperations, true, false);
        AbstractMessagingHATestCase.checkJMSQueue(backupJMSOperations, jmsQueueName, false);
        context1 = AbstractMessagingHATestCase.createJNDIContextFromServer1();
        // receive the message that was sent to server2 before failback
        AbstractMessagingHATestCase.receiveMessage(context1, jmsQueueLookup, text2);
        String text3 = "sent to server1, received from server2 (after 2nd failover)";
        // send a message to server1 before it is stopped a 2nd time
        AbstractMessagingHATestCase.sendMessage(context1, jmsQueueLookup, text3);
        context1.close();
        log.trace("==============================");
        log.trace("STOP SERVER1 A 2ND TIME...");
        log.trace("==============================");
        // shutdown server1 a 2nd time
        AbstractMessagingHATestCase.container.stop(AbstractMessagingHATestCase.SERVER1);
        // let some time for the backup to detect the failure
        AbstractMessagingHATestCase.waitForHornetQServerActivation(backupJMSOperations, true);
        AbstractMessagingHATestCase.checkHornetQServerStartedAndActiveAttributes(backupJMSOperations, true, true);
        AbstractMessagingHATestCase.checkJMSQueue(backupJMSOperations, jmsQueueName, true);
        context2 = AbstractMessagingHATestCase.createJNDIContextFromServer2();
        // receive the message that was sent to server1 before failover occurs a 2nd time
        AbstractMessagingHATestCase.receiveMessage(context2, jmsQueueLookup, text3);
        context2.close();
        // Assert.fail("wth");
    }
}

