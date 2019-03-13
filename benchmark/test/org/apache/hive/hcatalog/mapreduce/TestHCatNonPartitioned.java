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


import ErrorType.ERROR_INVALID_PARTITION_VALUES;
import ErrorType.ERROR_NON_EMPTY_TABLE;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class TestHCatNonPartitioned extends HCatMapReduceTest {
    private static List<HCatRecord> writeRecords;

    static List<HCatFieldSchema> partitionColumns;

    public TestHCatNonPartitioned(String formatName, String serdeClass, String inputFormatClass, String outputFormatClass) throws Exception {
        super(formatName, serdeClass, inputFormatClass, outputFormatClass);
        HCatMapReduceTest.dbName = null;// test if null dbName works ("default" is used)

        tableName = "testHCatNonPartitionedTable_" + formatName;
        TestHCatNonPartitioned.writeRecords = new ArrayList<HCatRecord>();
        for (int i = 0; i < 20; i++) {
            List<Object> objList = new ArrayList<Object>();
            objList.add(i);
            objList.add(("strvalue" + i));
            TestHCatNonPartitioned.writeRecords.add(new DefaultHCatRecord(objList));
        }
        TestHCatNonPartitioned.partitionColumns = new ArrayList<HCatFieldSchema>();
        TestHCatNonPartitioned.partitionColumns.add(HCatSchemaUtils.getHCatFieldSchema(new org.apache.hadoop.hive.metastore.api.FieldSchema("c1", serdeConstants.INT_TYPE_NAME, "")));
        TestHCatNonPartitioned.partitionColumns.add(HCatSchemaUtils.getHCatFieldSchema(new org.apache.hadoop.hive.metastore.api.FieldSchema("c2", serdeConstants.STRING_TYPE_NAME, "")));
    }

    @Test
    public void testHCatNonPartitionedTable() throws Exception {
        Map<String, String> partitionMap = new HashMap<String, String>();
        runMRCreate(null, TestHCatNonPartitioned.partitionColumns, TestHCatNonPartitioned.writeRecords, 10, true);
        // Test for duplicate publish -- this will either fail on job creation time
        // and throw an exception, or will fail at runtime, and fail the job.
        IOException exc = null;
        try {
            Job j = runMRCreate(null, TestHCatNonPartitioned.partitionColumns, TestHCatNonPartitioned.writeRecords, 20, true);
            Assert.assertEquals((!(isTableImmutable())), j.isSuccessful());
        } catch (IOException e) {
            exc = e;
            assertTrue((exc instanceof HCatException));
            Assert.assertEquals(ERROR_NON_EMPTY_TABLE, getErrorType());
        }
        if (!(isTableImmutable())) {
            assertNull(exc);
        }
        // Test for publish with invalid partition key name
        exc = null;
        partitionMap.clear();
        partitionMap.put("px", "p1value2");
        try {
            Job j = runMRCreate(partitionMap, TestHCatNonPartitioned.partitionColumns, TestHCatNonPartitioned.writeRecords, 20, true);
            assertFalse(j.isSuccessful());
        } catch (IOException e) {
            exc = e;
            assertTrue((exc != null));
            assertTrue((exc instanceof HCatException));
            Assert.assertEquals(ERROR_INVALID_PARTITION_VALUES, getErrorType());
        }
        // Read should get 10 rows if immutable, 30 if mutable
        if (isTableImmutable()) {
            runMRRead(10);
        } else {
            runMRRead(30);
        }
        hiveReadTest();
    }
}

