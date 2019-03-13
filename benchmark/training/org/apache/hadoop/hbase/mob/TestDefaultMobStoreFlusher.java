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
package org.apache.hadoop.hbase.mob;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category(LargeTests.class)
public class TestDefaultMobStoreFlusher {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestDefaultMobStoreFlusher.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] row1 = Bytes.toBytes("row1");

    private static final byte[] row2 = Bytes.toBytes("row2");

    private static final byte[] family = Bytes.toBytes("family");

    private static final byte[] qf1 = Bytes.toBytes("qf1");

    private static final byte[] qf2 = Bytes.toBytes("qf2");

    private static final byte[] value1 = Bytes.toBytes("value1");

    private static final byte[] value2 = Bytes.toBytes("value2");

    @Rule
    public TestName name = new TestName();

    @Test
    public void testFlushNonMobFile() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        HColumnDescriptor hcd = new HColumnDescriptor(TestDefaultMobStoreFlusher.family);
        hcd.setMaxVersions(4);
        desc.addFamily(hcd);
        testFlushFile(desc);
    }

    @Test
    public void testFlushMobFile() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        HColumnDescriptor hcd = new HColumnDescriptor(TestDefaultMobStoreFlusher.family);
        hcd.setMobEnabled(true);
        hcd.setMobThreshold(3L);
        hcd.setMaxVersions(4);
        desc.addFamily(hcd);
        testFlushFile(desc);
    }
}

