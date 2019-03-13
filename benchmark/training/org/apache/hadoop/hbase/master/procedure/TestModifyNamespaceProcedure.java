/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.master.procedure;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.NamespaceNotFoundException;
import org.apache.hadoop.hbase.constraint.ConstraintException;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestModifyNamespaceProcedure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestModifyNamespaceProcedure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestModifyNamespaceProcedure.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Test
    public void testModifyNamespace() throws Exception {
        final NamespaceDescriptor nsd = NamespaceDescriptor.create("testModifyNamespace").build();
        final String nsKey1 = "hbase.namespace.quota.maxregions";
        final String nsValue1before = "1111";
        final String nsValue1after = "9999";
        final String nsKey2 = "hbase.namespace.quota.maxtables";
        final String nsValue2 = "10";
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        nsd.setConfiguration(nsKey1, nsValue1before);
        createNamespaceForTesting(nsd);
        // Before modify
        NamespaceDescriptor currentNsDescriptor = TestModifyNamespaceProcedure.UTIL.getAdmin().getNamespaceDescriptor(nsd.getName());
        Assert.assertEquals(nsValue1before, currentNsDescriptor.getConfigurationValue(nsKey1));
        Assert.assertNull(currentNsDescriptor.getConfigurationValue(nsKey2));
        // Update
        nsd.setConfiguration(nsKey1, nsValue1after);
        nsd.setConfiguration(nsKey2, nsValue2);
        long procId1 = procExec.submitProcedure(new ModifyNamespaceProcedure(procExec.getEnvironment(), nsd));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId1);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId1);
        // Verify the namespace is updated.
        currentNsDescriptor = TestModifyNamespaceProcedure.UTIL.getAdmin().getNamespaceDescriptor(nsd.getName());
        Assert.assertEquals(nsValue1after, nsd.getConfigurationValue(nsKey1));
        Assert.assertEquals(nsValue2, currentNsDescriptor.getConfigurationValue(nsKey2));
    }

    @Test
    public void testModifyNonExistNamespace() throws Exception {
        final String namespaceName = "testModifyNonExistNamespace";
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        try {
            NamespaceDescriptor nsDescriptor = TestModifyNamespaceProcedure.UTIL.getAdmin().getNamespaceDescriptor(namespaceName);
            Assert.assertNull(nsDescriptor);
        } catch (NamespaceNotFoundException nsnfe) {
            // Expected
            TestModifyNamespaceProcedure.LOG.debug((("The namespace " + namespaceName) + " does not exist.  This is expected."));
        }
        final NamespaceDescriptor nsd = NamespaceDescriptor.create(namespaceName).build();
        long procId = procExec.submitProcedure(new ModifyNamespaceProcedure(procExec.getEnvironment(), nsd));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        // Expect fail with NamespaceNotFoundException
        Procedure<?> result = procExec.getResult(procId);
        Assert.assertTrue(result.isFailed());
        TestModifyNamespaceProcedure.LOG.debug(("modify namespace failed with exception: " + (result.getException())));
        Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof NamespaceNotFoundException));
    }

    @Test
    public void testModifyNamespaceWithInvalidRegionCount() throws Exception {
        final NamespaceDescriptor nsd = NamespaceDescriptor.create("testModifyNamespaceWithInvalidRegionCount").build();
        final String nsKey = "hbase.namespace.quota.maxregions";
        final String nsValue = "-1";
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        createNamespaceForTesting(nsd);
        // Modify
        nsd.setConfiguration(nsKey, nsValue);
        long procId = procExec.submitProcedure(new ModifyNamespaceProcedure(procExec.getEnvironment(), nsd));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        Procedure<?> result = procExec.getResult(procId);
        Assert.assertTrue(result.isFailed());
        TestModifyNamespaceProcedure.LOG.debug(("Modify namespace failed with exception: " + (result.getException())));
        Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof ConstraintException));
    }

    @Test
    public void testModifyNamespaceWithInvalidTableCount() throws Exception {
        final NamespaceDescriptor nsd = NamespaceDescriptor.create("testModifyNamespaceWithInvalidTableCount").build();
        final String nsKey = "hbase.namespace.quota.maxtables";
        final String nsValue = "-1";
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        createNamespaceForTesting(nsd);
        // Modify
        nsd.setConfiguration(nsKey, nsValue);
        long procId = procExec.submitProcedure(new ModifyNamespaceProcedure(procExec.getEnvironment(), nsd));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        Procedure<?> result = procExec.getResult(procId);
        Assert.assertTrue(result.isFailed());
        TestModifyNamespaceProcedure.LOG.debug(("Modify namespace failed with exception: " + (result.getException())));
        Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof ConstraintException));
    }

    @Test
    public void testRecoveryAndDoubleExecution() throws Exception {
        final NamespaceDescriptor nsd = NamespaceDescriptor.create("testRecoveryAndDoubleExecution").build();
        final String nsKey = "foo";
        final String nsValue = "bar";
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        createNamespaceForTesting(nsd);
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Modify
        nsd.setConfiguration(nsKey, nsValue);
        // Start the Modify procedure && kill the executor
        long procId = procExec.submitProcedure(new ModifyNamespaceProcedure(procExec.getEnvironment(), nsd));
        // Restart the executor and execute the step twice
        MasterProcedureTestingUtility.testRecoveryAndDoubleExecution(procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        // Validate
        NamespaceDescriptor currentNsDescriptor = TestModifyNamespaceProcedure.UTIL.getAdmin().getNamespaceDescriptor(nsd.getName());
        Assert.assertEquals(nsValue, currentNsDescriptor.getConfigurationValue(nsKey));
    }

    @Test
    public void testRollbackAndDoubleExecution() throws Exception {
        final NamespaceDescriptor nsd = NamespaceDescriptor.create("testRollbackAndDoubleExecution").build();
        final String nsKey = "foo";
        final String nsValue = "bar";
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        createNamespaceForTesting(nsd);
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Modify
        // Start the Modify procedure && kill the executor
        long procId = procExec.submitProcedure(new ModifyNamespaceProcedure(procExec.getEnvironment(), NamespaceDescriptor.create(nsd).addConfiguration(nsKey, nsValue).build()));
        int lastStep = 2;// failing before MODIFY_NAMESPACE_UPDATE_NS_TABLE

        MasterProcedureTestingUtility.testRollbackAndDoubleExecution(procExec, procId, lastStep);
        // Validate
        NamespaceDescriptor currentNsDescriptor = TestModifyNamespaceProcedure.UTIL.getAdmin().getNamespaceDescriptor(nsd.getName());
        Assert.assertNull(currentNsDescriptor.getConfigurationValue(nsKey));
    }
}

