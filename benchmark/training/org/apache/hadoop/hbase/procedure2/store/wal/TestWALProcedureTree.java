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
package org.apache.hadoop.hbase.procedure2.store.wal;


import Procedure.NO_PROC_ID;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureStateSerializer;
import org.apache.hadoop.hbase.procedure2.ProcedureSuspendedException;
import org.apache.hadoop.hbase.procedure2.ProcedureYieldException;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, SmallTests.class })
public class TestWALProcedureTree {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALProcedureTree.class);

    public static final class TestProcedure extends Procedure<Void> {
        @Override
        public void setProcId(long procId) {
            super.setProcId(procId);
        }

        @Override
        public void setParentProcId(long parentProcId) {
            super.setParentProcId(parentProcId);
        }

        @Override
        public synchronized void addStackIndex(int index) {
            super.addStackIndex(index);
        }

        @Override
        protected Procedure<Void>[] execute(Void env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            return null;
        }

        @Override
        protected void rollback(Void env) throws IOException, InterruptedException {
        }

        @Override
        protected boolean abort(Void env) {
            return false;
        }

        @Override
        protected void serializeStateData(ProcedureStateSerializer serializer) throws IOException {
        }

        @Override
        protected void deserializeStateData(ProcedureStateSerializer serializer) throws IOException {
        }
    }

    @Test
    public void testMissingStackId() throws IOException {
        TestWALProcedureTree.TestProcedure proc0 = createProc(1, NO_PROC_ID);
        proc0.addStackIndex(0);
        TestWALProcedureTree.TestProcedure proc1 = createProc(2, 1);
        proc1.addStackIndex(1);
        TestWALProcedureTree.TestProcedure proc2 = createProc(3, 2);
        proc2.addStackIndex(3);
        WALProcedureTree tree = WALProcedureTree.build(toProtos(proc0, proc1, proc2));
        List<TestWALProcedureTree.TestProcedure> validProcs = getProcs(tree.getValidProcs());
        Assert.assertEquals(0, validProcs.size());
        List<TestWALProcedureTree.TestProcedure> corruptedProcs = getProcs(tree.getCorruptedProcs());
        Assert.assertEquals(3, corruptedProcs.size());
        Assert.assertEquals(1, getProcId());
        Assert.assertEquals(2, getProcId());
        Assert.assertEquals(3, getProcId());
    }

    @Test
    public void testDuplicatedStackId() throws IOException {
        TestWALProcedureTree.TestProcedure proc0 = createProc(1, NO_PROC_ID);
        proc0.addStackIndex(0);
        TestWALProcedureTree.TestProcedure proc1 = createProc(2, 1);
        proc1.addStackIndex(1);
        TestWALProcedureTree.TestProcedure proc2 = createProc(3, 2);
        proc2.addStackIndex(1);
        WALProcedureTree tree = WALProcedureTree.build(toProtos(proc0, proc1, proc2));
        List<TestWALProcedureTree.TestProcedure> validProcs = getProcs(tree.getValidProcs());
        Assert.assertEquals(0, validProcs.size());
        List<TestWALProcedureTree.TestProcedure> corruptedProcs = getProcs(tree.getCorruptedProcs());
        Assert.assertEquals(3, corruptedProcs.size());
        Assert.assertEquals(1, getProcId());
        Assert.assertEquals(2, getProcId());
        Assert.assertEquals(3, getProcId());
    }

    @Test
    public void testOrphan() throws IOException {
        TestWALProcedureTree.TestProcedure proc0 = createProc(1, NO_PROC_ID);
        proc0.addStackIndex(0);
        TestWALProcedureTree.TestProcedure proc1 = createProc(2, 1);
        proc1.addStackIndex(1);
        TestWALProcedureTree.TestProcedure proc2 = createProc(3, NO_PROC_ID);
        proc2.addStackIndex(0);
        TestWALProcedureTree.TestProcedure proc3 = createProc(5, 4);
        proc3.addStackIndex(1);
        WALProcedureTree tree = WALProcedureTree.build(toProtos(proc0, proc1, proc2, proc3));
        List<TestWALProcedureTree.TestProcedure> validProcs = getProcs(tree.getValidProcs());
        Assert.assertEquals(3, validProcs.size());
        List<TestWALProcedureTree.TestProcedure> corruptedProcs = getProcs(tree.getCorruptedProcs());
        Assert.assertEquals(1, corruptedProcs.size());
        Assert.assertEquals(5, getProcId());
        Assert.assertEquals(4, getParentProcId());
    }
}

