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
package org.apache.drill.exec.store.parquet.columnreaders;


import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.store.parquet.columnreaders.batchsizing.BatchSizingMemoryUtil.ColumnMemoryUsageInfo;
import org.apache.drill.test.PhysicalOpUnitTestBase;
import org.apache.drill.test.rowSet.RowSet;
import org.junit.Test;


public class TestBatchSizingMemoryUtil extends PhysicalOpUnitTestBase {
    // private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestBatchSizingMemoryUtil.class);
    // Batch schema
    private static TupleMetadata schema;

    private static TupleMetadata nullableSchema;

    // Row set
    private RowSet.SingleRowSet rowSet;

    // Column memory usage information
    private final ColumnMemoryUsageInfo[] columnMemoryInfo = new ColumnMemoryUsageInfo[3];

    @Test
    public void testCanAddNewData() {
        try {
            testCanAddNewData(false);
        } finally {
            if ((rowSet) != null) {
                rowSet.clear();
            }
        }
    }

    @Test
    public void testCanAddNewNullabalData() {
        try {
            testCanAddNewData(true);
        } finally {
            if ((rowSet) != null) {
                rowSet.clear();
            }
        }
    }
}

