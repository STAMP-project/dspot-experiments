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
package org.apache.hadoop.hbase.regionserver.wal;


import java.io.IOException;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.CommonFSUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(LargeTests.class)
public class TestHBaseWalOnEC {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHBaseWalOnEC.class);

    private static final HBaseTestingUtility util = new HBaseTestingUtility();

    private static final String HFLUSH = "hflush";

    @Test
    public void testStreamCreate() throws IOException {
        try (FSDataOutputStream out = CommonFSUtils.createForWal(TestHBaseWalOnEC.util.getDFSCluster().getFileSystem(), new Path("/testStreamCreate"), true)) {
            Assert.assertTrue(CommonFSUtils.hasCapability(out, TestHBaseWalOnEC.HFLUSH));
        }
    }

    @Test
    public void testFlush() throws IOException {
        byte[] row = Bytes.toBytes("row");
        byte[] cf = Bytes.toBytes("cf");
        byte[] cq = Bytes.toBytes("cq");
        byte[] value = Bytes.toBytes("value");
        TableName name = TableName.valueOf(getClass().getSimpleName());
        Table t = TestHBaseWalOnEC.util.createTable(name, cf);
        t.put(new Put(row).addColumn(cf, cq, value));
        TestHBaseWalOnEC.util.getAdmin().flush(name);
        Assert.assertArrayEquals(value, t.get(new Get(row)).getValue(cf, cq));
    }
}

