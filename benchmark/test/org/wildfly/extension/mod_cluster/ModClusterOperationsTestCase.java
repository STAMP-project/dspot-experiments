/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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
package org.wildfly.extension.mod_cluster;


import LoadMetricResourceDefinition.SharedAttribute.WEIGHT;
import ModClusterExtension.SUBSYSTEM_NAME;
import ProxyConfigurationResourceDefinition.Attribute.LISTENER;
import ProxyConfigurationResourceDefinition.Attribute.PING;
import ProxyConfigurationResourceDefinition.DeprecatedAttribute.CONNECTOR;
import ProxyConfigurationResourceDefinition.DeprecatedAttribute.SIMPLE_LOAD_PROVIDER;
import SimpleLoadProviderResourceDefinition.Attribute.FACTOR;
import org.jboss.as.clustering.controller.Operations;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for testing individual management operations, especially useless and convoluted legacy operations.
 *
 * @author Radoslav Husar
 */
@SuppressWarnings("SameParameterValue")
public class ModClusterOperationsTestCase extends AbstractSubsystemTest {
    private final String PROXY_NAME = "default";

    public ModClusterOperationsTestCase() {
        super(SUBSYSTEM_NAME, new ModClusterExtension());
    }

    @Test
    public void testLegacyPathOperations() throws Exception {
        KernelServices services = this.buildKernelServices();
        ModelNode op = ModClusterOperationsTestCase.createLegacyModClusterConfigWriteAttributeOperation(PING, new ModelNode(10));
        ModelNode result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        op = ModClusterOperationsTestCase.createLegacyModClusterConfigLoadMetricWriteAttributeOperation("mem", WEIGHT, new ModelNode(10));
        result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
    }

    @Test
    public void testProxyOperations() throws Exception {
        KernelServices services = this.buildKernelServices();
        ModelNode op = Util.createOperation(READ_OPERATION_NAMES_OPERATION, ModClusterOperationsTestCase.getProxyAddress("default"));
        ModelNode result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        for (ProxyOperation proxyOperation : ProxyOperation.values()) {
            String operationName = proxyOperation.getDefinition().getName();
            Assert.assertTrue(String.format("'%s' operation is not registered at the proxy address", operationName), result.get(RESULT).asList().contains(new ModelNode(operationName)));
        }
    }

    /**
     * Tests that legacy proxy operations are registered at the subsystem level.
     */
    @Test
    public void testLegacyProxyOperations() throws Exception {
        KernelServices services = this.buildKernelServices();
        ModelNode op = Util.createOperation(READ_OPERATION_NAMES_OPERATION, ModClusterOperationsTestCase.getSubsystemAddress());
        ModelNode result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        for (ProxyOperation proxyOperation : ProxyOperation.values()) {
            String operationName = proxyOperation.getDefinition().getName();
            Assert.assertTrue(String.format("'%s' legacy operation is not registered", operationName), result.get(RESULT).asList().contains(new ModelNode(operationName)));
            ModelNode rodOp = Util.createOperation(READ_OPERATION_DESCRIPTION_OPERATION, ModClusterOperationsTestCase.getSubsystemAddress());
            rodOp.get(NAME).set(operationName);
            ModelNode rodResult = services.executeOperation(rodOp);
            Assert.assertEquals(rodResult.get(FAILURE_DESCRIPTION).asString(), SUCCESS, rodResult.get(OUTCOME).asString());
            Assert.assertTrue(rodResult.get(RESULT).hasDefined(DEPRECATED));
        }
    }

    @Test
    public void testLegacyMetricOperations() throws Exception {
        KernelServices services = this.buildKernelServices();
        ModelNode op = ModClusterOperationsTestCase.createLegacyRemoveMetricOperation("mem");
        ModelNode result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        op = ModClusterOperationsTestCase.createLegacyAddMetricOperation("mem");
        op.get("weight").set("5");
        result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        op = ModClusterOperationsTestCase.createLegacyAddMetricOperation("invalid-metric");
        result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), FAILED, result.get(OUTCOME).asString());
    }

    @Test
    public void testLegacyConnectorOperations() throws Exception {
        KernelServices services = this.buildKernelServices();
        String testListenerName = "default";
        ModelNode op = Operations.createWriteAttributeOperation(ModClusterOperationsTestCase.getLegacyModClusterConfigAddress(), CONNECTOR, new ModelNode(testListenerName));
        ModelNode result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        op = Operations.createReadAttributeOperation(ModClusterOperationsTestCase.getProxyAddress("default"), LISTENER);
        result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals(testListenerName, result.get(RESULT).asString());
    }

    @Test
    public void testLegacyLoadProviderOperations() throws Exception {
        KernelServices services = this.buildKernelServices();
        ModelNode op = Util.createRemoveOperation(ModClusterOperationsTestCase.getLegacyModClusterConfigDynamicLoadProviderAddress());
        ModelNode result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        op = Util.createAddOperation(ModClusterOperationsTestCase.getSimpleLoadProviderAddress(PROXY_NAME));
        result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        // Write on legacy path
        int testFactor = 66;
        op = Util.createOperation(WRITE_ATTRIBUTE_OPERATION, ModClusterOperationsTestCase.getProxyAddress(PROXY_NAME));
        op.get(NAME).set(SIMPLE_LOAD_PROVIDER.getName());
        op.get(VALUE).set(testFactor);
        result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        // Check written value on current model path
        op = Operations.createReadAttributeOperation(ModClusterOperationsTestCase.getSimpleLoadProviderAddress(PROXY_NAME), FACTOR);
        result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals(testFactor, result.get(RESULT).asInt());
        // Check written value on legacy path
        op = Operations.createReadAttributeOperation(ModClusterOperationsTestCase.getProxyAddress(PROXY_NAME), SIMPLE_LOAD_PROVIDER);
        result = services.executeOperation(op);
        Assert.assertEquals(result.get(FAILURE_DESCRIPTION).asString(), SUCCESS, result.get(OUTCOME).asString());
        Assert.assertEquals(testFactor, result.get(RESULT).asInt());
    }
}

