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
import org.apache.hadoop.hbase.NamespaceNotFoundException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.constraint.ConstraintException;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestDeleteNamespaceProcedure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestDeleteNamespaceProcedure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestDeleteNamespaceProcedure.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testDeleteNamespace() throws Exception {
        final String namespaceName = "testDeleteNamespace";
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        createNamespaceForTesting(namespaceName);
        long procId = procExec.submitProcedure(new DeleteNamespaceProcedure(procExec.getEnvironment(), namespaceName));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        TestDeleteNamespaceProcedure.validateNamespaceNotExist(namespaceName);
    }

    @Test
    public void testDeleteNonExistNamespace() throws Exception {
        final String namespaceName = "testDeleteNonExistNamespace";
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        TestDeleteNamespaceProcedure.validateNamespaceNotExist(namespaceName);
        long procId = procExec.submitProcedure(new DeleteNamespaceProcedure(procExec.getEnvironment(), namespaceName));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        // Expect fail with NamespaceNotFoundException
        Procedure<?> result = procExec.getResult(procId);
        Assert.assertTrue(result.isFailed());
        TestDeleteNamespaceProcedure.LOG.debug(("Delete namespace failed with exception: " + (result.getException())));
        Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof NamespaceNotFoundException));
    }

    @Test
    public void testDeleteSystemNamespace() throws Exception {
        final String namespaceName = SYSTEM_NAMESPACE.getName();
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        long procId = procExec.submitProcedure(new DeleteNamespaceProcedure(procExec.getEnvironment(), namespaceName));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        Procedure<?> result = procExec.getResult(procId);
        Assert.assertTrue(result.isFailed());
        TestDeleteNamespaceProcedure.LOG.debug(("Delete namespace failed with exception: " + (result.getException())));
        Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof ConstraintException));
    }

    @Test
    public void testDeleteNonEmptyNamespace() throws Exception {
        final String namespaceName = "testDeleteNonExistNamespace";
        final TableName tableName = TableName.valueOf(("testDeleteNonExistNamespace:" + (name.getMethodName())));
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        // create namespace
        createNamespaceForTesting(namespaceName);
        // create the table under the new namespace
        MasterProcedureTestingUtility.createTable(procExec, tableName, null, "f1");
        long procId = procExec.submitProcedure(new DeleteNamespaceProcedure(procExec.getEnvironment(), namespaceName));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        Procedure<?> result = procExec.getResult(procId);
        Assert.assertTrue(result.isFailed());
        TestDeleteNamespaceProcedure.LOG.debug(("Delete namespace failed with exception: " + (result.getException())));
        Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof ConstraintException));
    }

    @Test
    public void testRecoveryAndDoubleExecution() throws Exception {
        final String namespaceName = "testRecoveryAndDoubleExecution";
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        createNamespaceForTesting(namespaceName);
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Start the DeleteNamespace procedure && kill the executor
        long procId = procExec.submitProcedure(new DeleteNamespaceProcedure(procExec.getEnvironment(), namespaceName));
        // Restart the executor and execute the step twice
        MasterProcedureTestingUtility.testRecoveryAndDoubleExecution(procExec, procId);
        // Validate the deletion of namespace
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        TestDeleteNamespaceProcedure.validateNamespaceNotExist(namespaceName);
    }

    @Test
    public void testRollbackAndDoubleExecution() throws Exception {
        final String namespaceName = "testRollbackAndDoubleExecution";
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        createNamespaceForTesting(namespaceName);
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Start the DeleteNamespace procedure && kill the executor
        long procId = procExec.submitProcedure(new DeleteNamespaceProcedure(procExec.getEnvironment(), namespaceName));
        int lastStep = 2;// failing before DELETE_NAMESPACE_DELETE_FROM_NS_TABLE

        MasterProcedureTestingUtility.testRollbackAndDoubleExecution(procExec, procId, lastStep);
        // Validate the namespace still exists
        NamespaceDescriptor createdNsDescriptor = TestDeleteNamespaceProcedure.UTIL.getAdmin().getNamespaceDescriptor(namespaceName);
        Assert.assertNotNull(createdNsDescriptor);
    }
}

