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
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;


/**
 * A base class for a test Map/Reduce job over HBase tables. The map/reduce process we're testing
 * on our tables is simple - take every row in the table, reverse the value of a particular cell,
 * and write it back to the table. Implements common components between mapred and mapreduce
 * implementations.
 */
public abstract class TestTableMapReduceBase {
    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    protected static final TableName MULTI_REGION_TABLE_NAME = TableName.valueOf("mrtest");

    protected static final TableName TABLE_FOR_NEGATIVE_TESTS = TableName.valueOf("testfailuretable");

    protected static final byte[] INPUT_FAMILY = Bytes.toBytes("contents");

    protected static final byte[] OUTPUT_FAMILY = Bytes.toBytes("text");

    protected static final byte[][] columns = new byte[][]{ TestTableMapReduceBase.INPUT_FAMILY, TestTableMapReduceBase.OUTPUT_FAMILY };

    /**
     * Test a map/reduce against a multi-region table
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testMultiRegionTable() throws IOException {
        runTestOnTable(TestTableMapReduceBase.UTIL.getConnection().getTable(TestTableMapReduceBase.MULTI_REGION_TABLE_NAME));
    }

    @Test
    public void testCombiner() throws IOException {
        Configuration conf = new Configuration(TestTableMapReduceBase.UTIL.getConfiguration());
        // force use of combiner for testing purposes
        conf.setInt("mapreduce.map.combine.minspills", 1);
        runTestOnTable(TestTableMapReduceBase.UTIL.getConnection().getTable(TestTableMapReduceBase.MULTI_REGION_TABLE_NAME));
    }
}

