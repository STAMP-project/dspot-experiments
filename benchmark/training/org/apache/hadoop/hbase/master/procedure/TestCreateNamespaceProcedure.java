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


import NamespaceDescriptor.SYSTEM_NAMESPACE;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.NamespaceExistException;
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
public class TestCreateNamespaceProcedure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCreateNamespaceProcedure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCreateNamespaceProcedure.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Test
    public void testCreateNamespace() throws Exception {
        final NamespaceDescriptor nsd = NamespaceDescriptor.create("testCreateNamespace").build();
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        long procId = procExec.submitProcedure(new CreateNamespaceProcedure(procExec.getEnvironment(), nsd));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        validateNamespaceCreated(nsd);
    }

    @Test
    public void testCreateSameNamespaceTwice() throws Exception {
        final NamespaceDescriptor nsd = NamespaceDescriptor.create("testCreateSameNamespaceTwice").build();
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        long procId1 = procExec.submitProcedure(new CreateNamespaceProcedure(procExec.getEnvironment(), nsd));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId1);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId1);
        // Create the namespace that exists
        long procId2 = procExec.submitProcedure(new CreateNamespaceProcedure(procExec.getEnvironment(), nsd));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId2);
        // Second create should fail with NamespaceExistException
        Procedure<?> result = procExec.getResult(procId2);
        Assert.assertTrue(result.isFailed());
        TestCreateNamespaceProcedure.LOG.debug(("Create namespace failed with exception: " + (result.getException())));
        Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof NamespaceExistException));
    }

    @Test
    public void testCreateSystemNamespace() throws Exception {
        final NamespaceDescriptor nsd = TestCreateNamespaceProcedure.UTIL.getAdmin().getNamespaceDescriptor(SYSTEM_NAMESPACE.getName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        long procId = procExec.submitProcedure(new CreateNamespaceProcedure(procExec.getEnvironment(), nsd));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        Procedure<?> result = procExec.getResult(procId);
        Assert.assertTrue(result.isFailed());
        TestCreateNamespaceProcedure.LOG.debug(("Create namespace failed with exception: " + (result.getException())));
        Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof NamespaceExistException));
    }

    @Test
    public void testCreateNamespaceWithInvalidRegionCount() throws Exception {
        final NamespaceDescriptor nsd = NamespaceDescriptor.create("testCreateNamespaceWithInvalidRegionCount").build();
        final String nsKey = "hbase.namespace.quota.maxregions";
        final String nsValue = "-1";
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        nsd.setConfiguration(nsKey, nsValue);
        long procId = procExec.submitProcedure(new CreateNamespaceProcedure(procExec.getEnvironment(), nsd));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        Procedure<?> result = procExec.getResult(procId);
        Assert.assertTrue(result.isFailed());
        TestCreateNamespaceProcedure.LOG.debug(("Create namespace failed with exception: " + (result.getException())));
        Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof ConstraintException));
    }

    @Test
    public void testCreateNamespaceWithInvalidTableCount() throws Exception {
        final NamespaceDescriptor nsd = NamespaceDescriptor.create("testCreateNamespaceWithInvalidTableCount").build();
        final String nsKey = "hbase.namespace.quota.maxtables";
        final String nsValue = "-1";
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        nsd.setConfiguration(nsKey, nsValue);
        long procId = procExec.submitProcedure(new CreateNamespaceProcedure(procExec.getEnvironment(), nsd));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        Procedure<?> result = procExec.getResult(procId);
        Assert.assertTrue(result.isFailed());
        TestCreateNamespaceProcedure.LOG.debug(("Create namespace failed with exception: " + (result.getException())));
        Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof ConstraintException));
    }

    @Test
    public void testRecoveryAndDoubleExecution() throws Exception {
        final NamespaceDescriptor nsd = NamespaceDescriptor.create("testRecoveryAndDoubleExecution").build();
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Start the CreateNamespace procedure && kill the executor
        long procId = procExec.submitProcedure(new CreateNamespaceProcedure(procExec.getEnvironment(), nsd));
        // Restart the executor and execute the step twice
        MasterProcedureTestingUtility.testRecoveryAndDoubleExecution(procExec, procId);
        // Validate the creation of namespace
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        validateNamespaceCreated(nsd);
    }

    @Test
    public void testRollbackAndDoubleExecution() throws Exception {
        final NamespaceDescriptor nsd = NamespaceDescriptor.create("testRollbackAndDoubleExecution").build();
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Start the CreateNamespace procedure && kill the executor
        long procId = procExec.submitProcedure(new CreateNamespaceProcedure(procExec.getEnvironment(), nsd));
        int lastStep = 2;// failing before CREATE_NAMESPACE_CREATE_DIRECTORY

        MasterProcedureTestingUtility.testRollbackAndDoubleExecution(procExec, procId, lastStep);
        // Validate the non-existence of namespace
        try {
            NamespaceDescriptor nsDescriptor = TestCreateNamespaceProcedure.UTIL.getAdmin().getNamespaceDescriptor(nsd.getName());
            Assert.assertNull(nsDescriptor);
        } catch (NamespaceNotFoundException nsnfe) {
            // Expected
            TestCreateNamespaceProcedure.LOG.info((("The namespace " + (nsd.getName())) + " is not created."));
        }
    }
}

