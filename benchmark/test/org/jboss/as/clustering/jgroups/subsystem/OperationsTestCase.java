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
package org.jboss.as.clustering.jgroups.subsystem;


import ExpressionResolver.TEST_RESOLVER;
import JGroupsSubsystemResourceDefinition.Attribute.DEFAULT_CHANNEL;
import SocketBindingProtocolResourceDefinition.Attribute.SOCKET_BINDING;
import TransportResourceDefinition.Attribute.RACK;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for testing individual management operations.
 *
 * @author Richard Achmatowicz (c) 2011 Red Hat Inc.
 */
public class OperationsTestCase extends OperationTestCaseBase {
    /**
     * Tests access to subsystem attributes
     */
    @Test
    public void testSubsystemReadWriteOperations() throws Exception {
        KernelServices services = this.buildKernelServices();
        // read the default stack
        ModelNode result = services.executeOperation(OperationTestCaseBase.getSubsystemReadOperation(DEFAULT_CHANNEL));
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("ee", result.get(RESULT).asString());
        // write the default stack
        result = services.executeOperation(OperationTestCaseBase.getSubsystemWriteOperation(DEFAULT_CHANNEL, "bridge"));
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        // re-read the default stack
        result = services.executeOperation(OperationTestCaseBase.getSubsystemReadOperation(DEFAULT_CHANNEL));
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("bridge", result.get(RESULT).asString());
    }

    /**
     * Tests access to transport attributes
     */
    @Test
    public void testTransportReadWriteOperation() throws Exception {
        KernelServices services = this.buildKernelServices();
        // read the transport rack attribute
        ModelNode result = services.executeOperation(OperationTestCaseBase.getTransportReadOperation("maximal", "TCP", RACK));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("rack1", TEST_RESOLVER.resolveExpressions(result.get(RESULT)).asString());
        // write the rack attribute
        result = services.executeOperation(OperationTestCaseBase.getTransportWriteOperation("maximal", "TCP", RACK, "new-rack"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        // re-read the rack attribute
        result = services.executeOperation(OperationTestCaseBase.getTransportReadOperation("maximal", "TCP", RACK));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("new-rack", result.get(RESULT).asString());
    }

    @Test
    public void testTransportReadWriteWithParameters() throws Exception {
        // Parse and install the XML into the controller
        KernelServices services = this.buildKernelServices();
        Assert.assertTrue("Could not create services", services.isSuccessfulBoot());
        // add a protocol stack specifying TRANSPORT and PROTOCOLS parameters
        ModelNode result = services.executeOperation(OperationTestCaseBase.getProtocolStackAddOperationWithParameters("maximal2"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        // write the rack attribute
        result = services.executeOperation(OperationTestCaseBase.getTransportWriteOperation("maximal", "TCP", RACK, "new-rack"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        // re-read the rack attribute
        result = services.executeOperation(OperationTestCaseBase.getTransportReadOperation("maximal", "TCP", RACK));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("new-rack", result.get(RESULT).asString());
    }

    @Test
    public void testTransportPropertyReadWriteOperation() throws Exception {
        KernelServices services = this.buildKernelServices();
        // read the enable_bundling transport property
        ModelNode result = services.executeOperation(OperationTestCaseBase.getTransportGetPropertyOperation("maximal", "TCP", "enable_bundling"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("true", TEST_RESOLVER.resolveExpressions(result.get(RESULT)).asString());
        // write the enable_bundling transport property
        result = services.executeOperation(OperationTestCaseBase.getTransportPutPropertyOperation("maximal", "TCP", "enable_bundling", "false"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        // re-read the enable_bundling transport property
        result = services.executeOperation(OperationTestCaseBase.getTransportGetPropertyOperation("maximal", "TCP", "enable_bundling"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("false", result.get(RESULT).asString());
        // remove the enable_bundling transport property
        result = services.executeOperation(OperationTestCaseBase.getTransportRemovePropertyOperation("maximal", "TCP", "enable_bundling"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        // re-read the enable_bundling transport property
        result = services.executeOperation(OperationTestCaseBase.getTransportGetPropertyOperation("maximal", "TCP", "enable_bundling"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        // Validate that add/read/write/remove via legacy property resource
        result = services.executeOperation(OperationTestCaseBase.getTransportPropertyAddOperation("maximal", "TCP", "shared", "false"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        result = services.executeOperation(OperationTestCaseBase.getTransportPropertyReadOperation("maximal", "TCP", "shared"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("false", result.get(RESULT).asString());
        result = services.executeOperation(OperationTestCaseBase.getTransportGetPropertyOperation("maximal", "TCP", "shared"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("false", result.get(RESULT).asString());
        result = services.executeOperation(OperationTestCaseBase.getTransportPropertyWriteOperation("maximal", "TCP", "shared", "true"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        result = services.executeOperation(OperationTestCaseBase.getTransportPropertyReadOperation("maximal", "TCP", "shared"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("true", result.get(RESULT).asString());
        result = services.executeOperation(OperationTestCaseBase.getTransportGetPropertyOperation("maximal", "TCP", "shared"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("true", result.get(RESULT).asString());
        result = services.executeOperation(OperationTestCaseBase.getTransportPropertyRemoveOperation("maximal", "TCP", "shared"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        result = services.executeOperation(OperationTestCaseBase.getTransportGetPropertyOperation("maximal", "TCP", "shared"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
    }

    @Test
    public void testProtocolReadWriteOperation() throws Exception {
        KernelServices services = this.buildKernelServices();
        // add a protocol stack specifying TRANSPORT and PROTOCOLS parameters
        ModelNode result = services.executeOperation(OperationTestCaseBase.getProtocolStackAddOperationWithParameters("maximal2"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        // read the socket binding attribute
        result = services.executeOperation(OperationTestCaseBase.getProtocolReadOperation("maximal", "MPING", SOCKET_BINDING));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("jgroups-mping", result.get(RESULT).asString());
        // write the attribute
        result = services.executeOperation(OperationTestCaseBase.getProtocolWriteOperation("maximal", "MPING", SOCKET_BINDING, "new-socket-binding"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        // re-read the attribute
        result = services.executeOperation(OperationTestCaseBase.getProtocolReadOperation("maximal", "MPING", SOCKET_BINDING));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("new-socket-binding", result.get(RESULT).asString());
    }

    @Test
    public void testProtocolPropertyReadWriteOperation() throws Exception {
        KernelServices services = this.buildKernelServices();
        // read the name protocol property
        ModelNode result = services.executeOperation(OperationTestCaseBase.getProtocolGetPropertyOperation("maximal", "MPING", "name"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("value", TEST_RESOLVER.resolveExpressions(result.get(RESULT)).asString());
        // write the property
        result = services.executeOperation(OperationTestCaseBase.getProtocolPutPropertyOperation("maximal", "MPING", "name", "new-value"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        // re-read the property
        result = services.executeOperation(OperationTestCaseBase.getProtocolGetPropertyOperation("maximal", "MPING", "name"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("new-value", result.get(RESULT).asString());
        // remove the property
        result = services.executeOperation(OperationTestCaseBase.getProtocolRemovePropertyOperation("maximal", "MPING", "name"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        // re-read the property
        result = services.executeOperation(OperationTestCaseBase.getProtocolGetPropertyOperation("maximal", "MPING", "name"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
        // Validate property add/read/write/remove via legacy property resource
        result = services.executeOperation(OperationTestCaseBase.getProtocolPropertyAddOperation("maximal", "MPING", "async_discovery", "false"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        result = services.executeOperation(OperationTestCaseBase.getProtocolPropertyReadOperation("maximal", "MPING", "async_discovery"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("false", result.get(RESULT).asString());
        result = services.executeOperation(OperationTestCaseBase.getProtocolGetPropertyOperation("maximal", "MPING", "async_discovery"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("false", result.get(RESULT).asString());
        result = services.executeOperation(OperationTestCaseBase.getProtocolPropertyWriteOperation("maximal", "MPING", "async_discovery", "true"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        result = services.executeOperation(OperationTestCaseBase.getProtocolPropertyReadOperation("maximal", "MPING", "async_discovery"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("true", result.get(RESULT).asString());
        result = services.executeOperation(OperationTestCaseBase.getProtocolGetPropertyOperation("maximal", "MPING", "async_discovery"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals("true", result.get(RESULT).asString());
        result = services.executeOperation(OperationTestCaseBase.getProtocolPropertyRemoveOperation("maximal", "MPING", "async_discovery"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        result = services.executeOperation(OperationTestCaseBase.getProtocolGetPropertyOperation("maximal", "MPING", "async_discovery"));
        Assert.assertEquals(result.toString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertFalse(result.get(RESULT).isDefined());
    }

    @Test
    public void testLegacyProtocolAddRemoveOperation() throws Exception {
        KernelServices services = this.buildKernelServices();
        OperationsTestCase.testProtocolAddRemoveOperation(services, "MERGE2");
        OperationsTestCase.testProtocolAddRemoveOperation(services, "pbcast.NAKACK");
        OperationsTestCase.testProtocolAddRemoveOperation(services, "UNICAST2");
    }
}

