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
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotEnabledException;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.VerySlowMapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
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
@Category({ VerySlowMapReduceTests.class, LargeTests.class })
public class TestTableMapReduce extends TestTableMapReduceBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableMapReduce.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTableMapReduce.class);

    /**
     * Pass the given key and processed record reduce
     */
    static class ProcessContentsMapper extends TableMapper<ImmutableBytesWritable, Put> {
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
            if (!(cf.containsKey(TestTableMapReduceBase.INPUT_FAMILY))) {
                throw new IOException((("Wrong input columns. Missing: '" + (Bytes.toString(TestTableMapReduceBase.INPUT_FAMILY))) + "'."));
            }
            // Get the original value and reverse it
            String originalValue = Bytes.toString(value.getValue(TestTableMapReduceBase.INPUT_FAMILY, TestTableMapReduceBase.INPUT_FAMILY));
            StringBuilder newValue = new StringBuilder(originalValue);
            newValue.reverse();
            // Now set the value to be collected
            Put outval = new Put(key.get());
            outval.addColumn(TestTableMapReduceBase.OUTPUT_FAMILY, null, Bytes.toBytes(newValue.toString()));
            context.write(key, outval);
        }
    }

    @Test(expected = TableNotEnabledException.class)
    public void testWritingToDisabledTable() throws IOException {
        try (Admin admin = TestTableMapReduceBase.UTIL.getConnection().getAdmin();Table table = TestTableMapReduceBase.UTIL.getConnection().getTable(TestTableMapReduceBase.TABLE_FOR_NEGATIVE_TESTS)) {
            admin.disableTable(table.getName());
            runTestOnTable(table);
            Assert.fail("Should not have reached here, should have thrown an exception");
        }
    }

    @Test(expected = TableNotFoundException.class)
    public void testWritingToNonExistentTable() throws IOException {
        try (Table table = TestTableMapReduceBase.UTIL.getConnection().getTable(TableName.valueOf("table-does-not-exist"))) {
            runTestOnTable(table);
            Assert.fail("Should not have reached here, should have thrown an exception");
        }
    }
}

