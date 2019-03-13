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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.mapreduce;


import ErrorType.ERROR_DUPLICATE_PARTITION;
import ErrorType.ERROR_INVALID_PARTITION_VALUES;
import ErrorType.ERROR_MISSING_PARTITION_KEY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hive.hcatalog.common.HCatException;
import org.apache.hive.hcatalog.data.DefaultHCatRecord;
import org.apache.hive.hcatalog.data.HCatRecord;
import org.apache.hive.hcatalog.data.schema.HCatFieldSchema;
import org.apache.hive.hcatalog.data.schema.HCatSchemaUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class TestHCatPartitioned extends HCatMapReduceTest {
    private static List<HCatRecord> writeRecords;

    private static List<HCatFieldSchema> partitionColumns;

    public TestHCatPartitioned(String formatName, String serdeClass, String inputFormatClass, String outputFormatClass) throws Exception {
        super(formatName, serdeClass, inputFormatClass, outputFormatClass);
        tableName = "testHCatPartitionedTable_" + formatName;
        TestHCatPartitioned.writeRecords = new ArrayList<HCatRecord>();
        for (int i = 0; i < 20; i++) {
            List<Object> objList = new ArrayList<Object>();
            objList.add(i);
            objList.add(("strvalue" + i));
            TestHCatPartitioned.writeRecords.add(new DefaultHCatRecord(objList));
        }
        TestHCatPartitioned.partitionColumns = new ArrayList<HCatFieldSchema>();
        TestHCatPartitioned.partitionColumns.add(HCatSchemaUtils.getHCatFieldSchema(new org.apache.hadoop.hive.metastore.api.FieldSchema("c1", serdeConstants.INT_TYPE_NAME, "")));
        TestHCatPartitioned.partitionColumns.add(HCatSchemaUtils.getHCatFieldSchema(new org.apache.hadoop.hive.metastore.api.FieldSchema("c2", serdeConstants.STRING_TYPE_NAME, "")));
    }

    @Test
    public void testHCatPartitionedTable() throws Exception {
        Map<String, String> partitionMap = new HashMap<String, String>();
        partitionMap.put("part1", "p1value1");
        partitionMap.put("part0", "501");
        runMRCreate(partitionMap, TestHCatPartitioned.partitionColumns, TestHCatPartitioned.writeRecords, 10, true);
        partitionMap.clear();
        partitionMap.put("PART1", "p1value2");
        partitionMap.put("PART0", "502");
        runMRCreate(partitionMap, TestHCatPartitioned.partitionColumns, TestHCatPartitioned.writeRecords, 20, true);
        // Test for duplicate publish -- this will either fail on job creation time
        // and throw an exception, or will fail at runtime, and fail the job.
        IOException exc = null;
        try {
            Job j = runMRCreate(partitionMap, TestHCatPartitioned.partitionColumns, TestHCatPartitioned.writeRecords, 20, true);
            Assert.assertEquals((!(isTableImmutable())), j.isSuccessful());
        } catch (IOException e) {
            exc = e;
            assertTrue((exc instanceof HCatException));
            assertTrue(ERROR_DUPLICATE_PARTITION.equals(getErrorType()));
        }
        if (!(isTableImmutable())) {
            assertNull(exc);
        }
        // Test for publish with invalid partition key name
        exc = null;
        partitionMap.clear();
        partitionMap.put("px1", "p1value2");
        partitionMap.put("px0", "502");
        try {
            Job j = runMRCreate(partitionMap, TestHCatPartitioned.partitionColumns, TestHCatPartitioned.writeRecords, 20, true);
            assertFalse(j.isSuccessful());
        } catch (IOException e) {
            exc = e;
            assertNotNull(exc);
            assertTrue((exc instanceof HCatException));
            Assert.assertEquals(ERROR_MISSING_PARTITION_KEY, getErrorType());
        }
        // Test for publish with missing partition key values
        exc = null;
        partitionMap.clear();
        partitionMap.put("px", "512");
        try {
            runMRCreate(partitionMap, TestHCatPartitioned.partitionColumns, TestHCatPartitioned.writeRecords, 20, true);
        } catch (IOException e) {
            exc = e;
        }
        assertNotNull(exc);
        assertTrue((exc instanceof HCatException));
        Assert.assertEquals(ERROR_INVALID_PARTITION_VALUES, getErrorType());
        // Test for null partition value map
        exc = null;
        try {
            runMRCreate(null, TestHCatPartitioned.partitionColumns, TestHCatPartitioned.writeRecords, 20, false);
        } catch (IOException e) {
            exc = e;
        }
        assertTrue((exc == null));
        // assertTrue(exc instanceof HCatException);
        // assertEquals(ErrorType.ERROR_PUBLISHING_PARTITION, ((HCatException) exc).getErrorType());
        // With Dynamic partitioning, this isn't an error that the keyValues specified didn't values
        // Read should get 10 + 20 rows if immutable, 50 (10+20+20) if mutable
        if (isTableImmutable()) {
            runMRRead(30);
        } else {
            runMRRead(50);
        }
        // Read with partition filter
        runMRRead(10, "part1 = \"p1value1\"");
        runMRRead(10, "part0 = \"501\"");
        if (isTableImmutable()) {
            runMRRead(20, "part1 = \"p1value2\"");
            runMRRead(30, "part1 = \"p1value1\" or part1 = \"p1value2\"");
            runMRRead(20, "part0 = \"502\"");
            runMRRead(30, "part0 = \"501\" or part0 = \"502\"");
        } else {
            runMRRead(40, "part1 = \"p1value2\"");
            runMRRead(50, "part1 = \"p1value1\" or part1 = \"p1value2\"");
            runMRRead(40, "part0 = \"502\"");
            runMRRead(50, "part0 = \"501\" or part0 = \"502\"");
        }
        tableSchemaTest();
        columnOrderChangeTest();
        hiveReadTest();
    }
}

