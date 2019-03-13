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


import java.util.List;
import org.apache.hive.hcatalog.data.HCatRecord;
import org.apache.hive.hcatalog.data.schema.HCatFieldSchema;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHCatDynamicPartitioned extends HCatMapReduceTest {
    private static List<HCatRecord> writeRecords;

    private static List<HCatFieldSchema> dataColumns;

    private static final Logger LOG = LoggerFactory.getLogger(TestHCatDynamicPartitioned.class);

    protected static final int NUM_RECORDS = 20;

    protected static final int NUM_PARTITIONS = 5;

    public TestHCatDynamicPartitioned(String formatName, String serdeClass, String inputFormatClass, String outputFormatClass) throws Exception {
        super(formatName, serdeClass, inputFormatClass, outputFormatClass);
        tableName = "testHCatDynamicPartitionedTable_" + formatName;
        TestHCatDynamicPartitioned.generateWriteRecords(TestHCatDynamicPartitioned.NUM_RECORDS, TestHCatDynamicPartitioned.NUM_PARTITIONS, 0);
        TestHCatDynamicPartitioned.generateDataColumns();
    }

    /**
     * Run the dynamic partitioning test but with single map task
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHCatDynamicPartitionedTable() throws Exception {
        runHCatDynamicPartitionedTable(true, null);
    }

    /**
     * Run the dynamic partitioning test but with multiple map task. See HCATALOG-490
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHCatDynamicPartitionedTableMultipleTask() throws Exception {
        runHCatDynamicPartitionedTable(false, null);
    }
}

