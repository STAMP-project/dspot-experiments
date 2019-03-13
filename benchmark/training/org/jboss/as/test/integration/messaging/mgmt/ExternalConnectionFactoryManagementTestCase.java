/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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


import java.util.UUID;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.as.test.integration.management.util.ServerReload;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c)2012 Red Hat, inc

https://issues.jboss.org/browse/AS7-5107
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ExternalConnectionFactoryManagementTestCase extends ContainerResourceMgmtTestBase {
    private static final String CF_NAME = UUID.randomUUID().toString();

    private static final String CONNECTOR_NAME = "client-http-connector";

    @ContainerResource
    private ManagementClient managementClient;

    @Test
    public void testWriteDiscoveryGroupAttributeWhenConnectorIsAlreadyDefined() throws Exception {
        JMSOperations jmsOperations = JMSOperationsProvider.getInstance(managementClient.getControllerClient());
        try {
            jmsOperations.addExternalHttpConnector(ExternalConnectionFactoryManagementTestCase.CONNECTOR_NAME, "http", "http-acceptor");
            ModelNode attributes = new ModelNode();
            attributes.get("connectors").add(ExternalConnectionFactoryManagementTestCase.CONNECTOR_NAME);
            jmsOperations.addJmsExternalConnectionFactory(ExternalConnectionFactoryManagementTestCase.CF_NAME, ("java:/jms/" + (ExternalConnectionFactoryManagementTestCase.CF_NAME)), attributes);
            final ModelNode writeAttribute = new ModelNode();
            writeAttribute.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
            writeAttribute.get(OP_ADDR).set(jmsOperations.getSubsystemAddress().add("connection-factory", ExternalConnectionFactoryManagementTestCase.CF_NAME));
            writeAttribute.get(NAME).set("discovery-group");
            writeAttribute.get(VALUE).set(UUID.randomUUID().toString());
            try {
                executeOperation(writeAttribute);
                Assert.fail("it is not possible to define a discovery group when the connector attribute is already defined");
            } catch (MgmtOperationException e) {
                Assert.assertEquals(FAILED, e.getResult().get(OUTCOME).asString());
                Assert.assertEquals(true, e.getResult().get(ROLLED_BACK).asBoolean());
                Assert.assertTrue(e.getResult().get(FAILURE_DESCRIPTION).asString().contains("WFLYCTL0105"));
            }
        } finally {
            jmsOperations.removeJmsExternalConnectionFactory(ExternalConnectionFactoryManagementTestCase.CF_NAME);
            jmsOperations.removeExternalHttpConnector(ExternalConnectionFactoryManagementTestCase.CONNECTOR_NAME);
        }
        ServerReload.executeReloadAndWaitForCompletion(managementClient.getControllerClient());
    }

    @Test
    public void testRemovePooledConnectionFactory() throws Exception {
        // /subsystem=messaging-activemq/in-vm-connector=invm1:add(server-id=123)
        ModelNode invmConnectorAddress = new ModelNode();
        invmConnectorAddress.add("subsystem", "messaging-activemq");
        invmConnectorAddress.add("in-vm-connector", "invm1");
        ModelNode op = Operations.createAddOperation(invmConnectorAddress);
        op.get("server-id").set("123");
        ExternalConnectionFactoryManagementTestCase.execute(managementClient.getControllerClient(), op);
        // /subsystem=messaging-activemq/pooled-connection-factory=pool3:add(connectors=[invm1],entries=[foo])
        ModelNode pcfAddress = new ModelNode();
        pcfAddress.add("subsystem", "messaging-activemq");
        pcfAddress.add("pooled-connection-factory", "pool3");
        op = Operations.createAddOperation(pcfAddress);
        op.get("connectors").setEmptyList().add("invm1");
        op.get("entries").setEmptyList().add("foo");
        ExternalConnectionFactoryManagementTestCase.execute(managementClient.getControllerClient(), op);
        // /subsystem=messaging-activemq/pooled-connection-factory=pool3:remove()
        op = Operations.createRemoveOperation(pcfAddress);
        ExternalConnectionFactoryManagementTestCase.execute(managementClient.getControllerClient(), op);
        op = Operations.createRemoveOperation(invmConnectorAddress);
        ExternalConnectionFactoryManagementTestCase.execute(managementClient.getControllerClient(), op);
        ServerReload.executeReloadAndWaitForCompletion(managementClient.getControllerClient());
    }
}

