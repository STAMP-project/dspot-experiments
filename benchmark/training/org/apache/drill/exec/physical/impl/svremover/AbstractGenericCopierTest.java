/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.physical.impl.svremover;


import BatchSchema.SelectionVectorMode.NONE;
import org.apache.drill.exec.memory.BufferAllocator;
import org.apache.drill.exec.physical.impl.MockRecordBatch;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.VectorContainer;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.OperatorFixture;
import org.apache.drill.test.rowSet.DirectRowSet;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.junit.Rule;
import org.junit.Test;


public abstract class AbstractGenericCopierTest {
    @Rule
    public final BaseDirTestWatcher baseDirTestWatcher = new BaseDirTestWatcher();

    @Test
    public void testCopyRecords() throws Exception {
        try (OperatorFixture operatorFixture = new OperatorFixture.Builder(baseDirTestWatcher).build()) {
            final BufferAllocator allocator = operatorFixture.allocator();
            final BatchSchema batchSchema = createTestSchema(NONE);
            final RowSet srcRowSet = createSrcRowSet(allocator);
            final VectorContainer destContainer = new VectorContainer(allocator, batchSchema);
            destContainer.setRecordCount(0);
            final RowSet expectedRowSet = createExpectedRowset(allocator);
            MockRecordBatch mockRecordBatch = null;
            try {
                mockRecordBatch = new MockRecordBatch.Builder().sendData(srcRowSet).build(operatorFixture.getFragmentContext());
                mockRecordBatch.next();
                final Copier copier = createCopier(mockRecordBatch, destContainer, null);
                copier.copyRecords(0, 3);
                new RowSetComparison(expectedRowSet).verify(DirectRowSet.fromContainer(destContainer));
            } finally {
                if (mockRecordBatch != null) {
                    mockRecordBatch.close();
                }
                srcRowSet.clear();
                destContainer.clear();
                expectedRowSet.clear();
            }
        }
    }

    @Test
    public void testAppendRecords() throws Exception {
        try (OperatorFixture operatorFixture = new OperatorFixture.Builder(baseDirTestWatcher).build()) {
            final BufferAllocator allocator = operatorFixture.allocator();
            final BatchSchema batchSchema = createTestSchema(NONE);
            final RowSet srcRowSet = createSrcRowSet(allocator);
            final VectorContainer destContainer = new VectorContainer(allocator, batchSchema);
            AbstractCopier.allocateOutgoing(destContainer, 3);
            destContainer.setRecordCount(0);
            final RowSet expectedRowSet = createExpectedRowset(allocator);
            MockRecordBatch mockRecordBatch = null;
            try {
                mockRecordBatch = new MockRecordBatch.Builder().sendData(srcRowSet).build(operatorFixture.getFragmentContext());
                mockRecordBatch.next();
                final Copier copier = createCopier(mockRecordBatch, destContainer, null);
                copier.appendRecord(0);
                copier.appendRecords(1, 2);
                new RowSetComparison(expectedRowSet).verify(DirectRowSet.fromContainer(destContainer));
            } finally {
                if (mockRecordBatch != null) {
                    mockRecordBatch.close();
                }
                srcRowSet.clear();
                destContainer.clear();
                expectedRowSet.clear();
            }
        }
    }
}

