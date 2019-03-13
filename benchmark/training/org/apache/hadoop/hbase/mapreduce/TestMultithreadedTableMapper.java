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
package org.apache.hadoop.hbase.mapreduce;


import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Map/Reduce job over HBase tables. The map/reduce process we're testing
 * on our tables is simple - take every row in the table, reverse the value of
 * a particular cell, and write it back to the table.
 */
@Category({ MapReduceTests.class, LargeTests.class })
public class TestMultithreadedTableMapper {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMultithreadedTableMapper.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMultithreadedTableMapper.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    static final TableName MULTI_REGION_TABLE_NAME = TableName.valueOf("mrtest");

    static final byte[] INPUT_FAMILY = Bytes.toBytes("contents");

    static final byte[] OUTPUT_FAMILY = Bytes.toBytes("text");

    static final int NUMBER_OF_THREADS = 10;

    /**
     * Pass the given key and processed record reduce
     */
    public static class ProcessContentsMapper extends TableMapper<ImmutableBytesWritable, Put> {
        /**
         * Pass the key, and reversed value to reduce
         *
         * @param key
         * 		
         * @param value
         * 		
         * @param context
         * 		
         * @throws IOException
         * 		
         */
        @Override
        public void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            if ((value.size()) != 1) {
                throw new IOException("There should only be one input column");
            }
            Map<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> cf = value.getMap();
            if (!(cf.containsKey(TestMultithreadedTableMapper.INPUT_FAMILY))) {
                throw new IOException((("Wrong input columns. Missing: '" + (Bytes.toString(TestMultithreadedTableMapper.INPUT_FAMILY))) + "'."));
            }
            // Get the original value and reverse it
            String originalValue = Bytes.toString(value.getValue(TestMultithreadedTableMapper.INPUT_FAMILY, TestMultithreadedTableMapper.INPUT_FAMILY));
            StringBuilder newValue = new StringBuilder(originalValue);
            newValue.reverse();
            // Now set the value to be collected
            Put outval = new Put(key.get());
            outval.addColumn(TestMultithreadedTableMapper.OUTPUT_FAMILY, null, Bytes.toBytes(newValue.toString()));
            context.write(key, outval);
        }
    }

    /**
     * Test multithreadedTableMappper map/reduce against a multi-region table
     *
     * @throws IOException
     * 		
     * @throws ClassNotFoundException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testMultithreadedTableMapper() throws IOException, ClassNotFoundException, InterruptedException {
        runTestOnTable(TestMultithreadedTableMapper.UTIL.getConnection().getTable(TestMultithreadedTableMapper.MULTI_REGION_TABLE_NAME));
    }
}

