/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.txn.subsystem;


import CommonAttributes.AVERAGE_COMMIT_TIME;
import CommonAttributes.NUMBER_OF_SYSTEM_ROLLBACKS;
import ModelDescriptionConstants.RESULT;
import ModelTestControllerVersion.EAP_6_4_0;
import ModelTestControllerVersion.EAP_7_0_0;
import ModelTestControllerVersion.EAP_7_1_0;
import TransactionExtension.SUBSYSTEM_NAME;
import TransactionExtension.SUBSYSTEM_PATH;
import com.arjuna.ats.arjuna.coordinator.TxStats;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.model.test.FailedOperationTransformationConfig;
import org.jboss.as.model.test.ModelFixer;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class TransactionSubsystemTestCase extends AbstractSubsystemBaseTest {
    public TransactionSubsystemTestCase() {
        super(SUBSYSTEM_NAME, new TransactionExtension());
    }

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }

    @Test
    public void testExpressions() throws Exception {
        standardSubsystemTest("full-expressions.xml");
    }

    @Test
    public void testMinimalConfig() throws Exception {
        standardSubsystemTest("minimal.xml");
    }

    @Test
    public void testJdbcStore() throws Exception {
        standardSubsystemTest("jdbc-store.xml");
    }

    @Test
    public void testJdbcStoreMinimal() throws Exception {
        standardSubsystemTest("jdbc-store-minimal.xml");
    }

    @Test
    public void testJdbcStoreExpressions() throws Exception {
        standardSubsystemTest("jdbc-store-expressions.xml");
    }

    @Test
    public void testParser_EAP_6_4() throws Exception {
        standardSubsystemTest("full-1.5.0.xml");
    }

    @Test
    public void testParser_EAP_7_0() throws Exception {
        standardSubsystemTest("full-3.0.0.xml");
    }

    @Test
    public void testParser_EAP_7_1() throws Exception {
        standardSubsystemTest("full-4.0.0.xml");
    }

    @Test
    public void testTxStats() throws Exception {
        // Parse the subsystem xml and install into the first controller
        final String subsystemXml = getSubsystemXml();
        final KernelServices kernelServices = super.createKernelServicesBuilder(createAdditionalInitialization()).setSubsystemXml(subsystemXml).build();
        Assert.assertTrue("Subsystem boot failed!", kernelServices.isSuccessfulBoot());
        // Reads stats
        ModelNode operation = createReadAttributeOperation(NUMBER_OF_SYSTEM_ROLLBACKS);
        ModelNode result = kernelServices.executeOperation(operation);
        Assert.assertEquals("success", result.get("outcome").asString());
        Assert.assertEquals(TxStats.getInstance().getNumberOfSystemRollbacks(), result.get(RESULT).asLong());
        operation = createReadAttributeOperation(AVERAGE_COMMIT_TIME);
        result = kernelServices.executeOperation(operation);
        Assert.assertEquals("success", result.get("outcome").asString());
        Assert.assertEquals(TxStats.getInstance().getAverageCommitTime(), result.get(RESULT).asLong());
    }

    @Test
    public void testAsyncIOExpressions() throws Exception {
        standardSubsystemTest("async-io-expressions.xml");
    }

    @Test
    public void testTransformersFullEAP640() throws Exception {
        testTransformersFull(EAP_6_4_0, TransactionTransformers.MODEL_VERSION_EAP64);
    }

    @Test
    public void testTransformersFullEAP700() throws Exception {
        testTransformersFull(EAP_7_0_0, TransactionTransformers.MODEL_VERSION_EAP70);
    }

    @Test
    public void testTransformersFullEAP710() throws Exception {
        testTransformersFull(EAP_7_1_0, TransactionTransformers.MODEL_VERSION_EAP71);
    }

    @Test
    public void testRejectTransformersEAP640() throws Exception {
        testRejectTransformers(EAP_6_4_0, TransactionTransformers.MODEL_VERSION_EAP64, new FailedOperationTransformationConfig().addFailedAttribute(PathAddress.pathAddress(SUBSYSTEM_PATH), new FailedOperationTransformationConfig.NewAttributesConfig("maximum-timeout")));
    }

    @Test
    public void testRejectTransformersEAP700() throws Exception {
        testRejectTransformers7(EAP_7_0_0, TransactionTransformers.MODEL_VERSION_EAP70, new FailedOperationTransformationConfig().addFailedAttribute(PathAddress.pathAddress(SUBSYSTEM_PATH), new FailedOperationTransformationConfig.NewAttributesConfig("maximum-timeout")));
    }

    @Test
    public void testRejectTransformersEAP710() throws Exception {
        testRejectTransformers7(EAP_7_1_0, TransactionTransformers.MODEL_VERSION_EAP71, new FailedOperationTransformationConfig().addFailedAttribute(PathAddress.pathAddress(SUBSYSTEM_PATH), new FailedOperationTransformationConfig.NewAttributesConfig("maximum-timeout")));
    }

    private static ModelFixer ADD_REMOVED_HORNETQ_STORE_ENABLE_ASYNC_IO = ( modelNode) -> {
        modelNode.get(TransactionSubsystemRootResourceDefinition.HORNETQ_STORE_ENABLE_ASYNC_IO.getName()).set(true);
        modelNode.get(TransactionSubsystemRootResourceDefinition.JOURNAL_STORE_ENABLE_ASYNC_IO.getName()).set(true);
        modelNode.get(TransactionSubsystemRootResourceDefinition.USE_HORNETQ_STORE.getName()).set(true);
        modelNode.get(TransactionSubsystemRootResourceDefinition.USE_JOURNAL_STORE.getName()).set(true);
        return modelNode;
    };
}

