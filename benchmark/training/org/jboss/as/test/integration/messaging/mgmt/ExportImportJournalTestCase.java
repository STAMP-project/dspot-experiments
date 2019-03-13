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
package org.jboss.as.test.integration.messaging.mgmt;


import java.io.File;
import java.util.UUID;
import javax.naming.Context;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2015 Red Hat inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ExportImportJournalTestCase {
    protected final String jmsQueueName = "ExportImportJournalTestCase-Queue";

    protected final String jmsQueueLookup = "jms/" + (jmsQueueName);

    @ContainerResource
    private Context remoteContext;

    @ContainerResource
    private ManagementClient managementClient;

    @Test
    public void testExportImportJournal() throws Exception {
        // send a persistent message
        String text = UUID.randomUUID().toString();
        ExportImportJournalTestCase.sendMessage(remoteContext, jmsQueueLookup, text);
        // reload in admin-only mode
        executeReloadAndWaitForCompletion(managementClient.getControllerClient(), true);
        // export the journal (must be performed in admin-only mode)
        String dumpFilePath = exportJournal();
        // reload in normal mode
        executeReloadAndWaitForCompletion(managementClient.getControllerClient(), false);
        // remove all messages
        removeAllMessagesFromQueue(jmsQueueName);
        // no message to receive
        ExportImportJournalTestCase.receiveMessage(remoteContext, jmsQueueLookup, false, null);
        // import the journal (must be performed in normal mode)
        importJournal(dumpFilePath);
        // check the message is received
        ExportImportJournalTestCase.receiveMessage(remoteContext, jmsQueueLookup, true, text);
        // remove the dump file
        File f = new File(dumpFilePath);
        f.delete();
    }
}

