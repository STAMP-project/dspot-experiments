/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.messaging.jms.context.notClosingInjectedContext;


import java.util.List;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.test.integration.messaging.jms.context.notClosingInjectedContext.auxiliary.Mdb;
import org.jboss.as.test.shared.ServerReload;
import org.jboss.as.test.shared.SnapshotRestoreSetupTask;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test for issue https://issues.jboss.org/browse/WFLY-10531 (based on reproducer created by Gunter Zeilinger <gunterze@gmail.com>.
 *
 * 500 messages should be send to mdb and each of them should be received in verify queue.
 * If error is still valid, there will be exceptions like: IJ000453: Unable to get managed connection for java:/JmsXA
 *
 * @author Jiri Ondrusek <jondruse@redhat.com>
 */
@RunWith(Arquillian.class)
@ServerSetup(NotClosingInjectedContextTestCase.NotClosingInjectedContextServerSetupTask.class)
public class NotClosingInjectedContextTestCase {
    private static final Logger LOGGER = Logger.getLogger(NotClosingInjectedContextTestCase.class);

    private static final PathAddress LOG_FILE_ADDRESS = PathAddress.pathAddress().append(SUBSYSTEM, "logging");

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory factory;

    @Resource(mappedName = Mdb.JNDI_NAME)
    private Queue queue;

    @Resource(mappedName = Mdb.JNDI_VERIFY_NAME)
    private Queue queueVerify;

    @ArquillianResource
    private ManagementClient managementClient;

    @Test
    public void testLeakingConnection() throws Exception {
        // length of log before execution
        String sizeBefore = getLogLineCount(managementClient.getControllerClient());
        try (JMSContext context = factory.createContext();JMSConsumer consumer = context.createConsumer(queueVerify)) {
            int j = 0;
            while (true) {
                String t = consumer.receiveBody(String.class, adjust(2000));
                if (t == null) {
                    NotClosingInjectedContextTestCase.LOGGER.info("Received null message");
                    break;
                } else {
                    NotClosingInjectedContextTestCase.LOGGER.info(("Received message:" + (++j)));
                }
            } 
            Assert.assertEquals("There should be 2 received messages.", 2, j);
            // size of log after execution
            String sizeAfter = getLogLineCount(managementClient.getControllerClient());
            long difference = (Long.parseLong(sizeAfter)) - (Long.parseLong(sizeBefore));
            // validate, that there is no "IJ000100: Closing a connection for you. Please close them yourself"
            List<ModelNode> lines = getLogs(managementClient.getControllerClient(), difference);
            for (ModelNode line : lines) {
                if (line.asString().contains("IJ000100:")) {
                    Assert.fail("JMS context is not closed");
                }
            }
        }
    }

    /**
     * Enable debug for ccm
     */
    static class NotClosingInjectedContextServerSetupTask extends SnapshotRestoreSetupTask {
        @Override
        protected void doSetup(ManagementClient client, String containerId) throws Exception {
            ModelNode address = new ModelNode();
            address.add("subsystem", "jca");
            address.add("cached-connection-manager", "cached-connection-manager");
            ModelNode operation = new ModelNode();
            operation = new ModelNode();
            operation.get(OP_ADDR).set(address);
            operation.get(OP).set("write-attribute");
            operation.get("name").set("debug");
            operation.get("value").set("true");
            client.getControllerClient().execute(operation);
            ServerReload.executeReloadAndWaitForCompletion(client.getControllerClient(), 50000);
        }
    }
}

