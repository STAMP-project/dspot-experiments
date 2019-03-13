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
package org.apache.hadoop.hbase.client;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ipc.AbstractRpcClient;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Do some ops and prove that client and server can work w/o codecs; that we can pb all the time.
 * Good for third-party clients or simple scripts that want to talk direct to hbase.
 */
@Category({ MediumTests.class, ClientTests.class })
public class TestFromClientSideNoCodec {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFromClientSideNoCodec.class);

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testBasics() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final byte[][] fs = new byte[][]{ Bytes.toBytes("cf1"), Bytes.toBytes("cf2"), Bytes.toBytes("cf3") };
        Table ht = TestFromClientSideNoCodec.TEST_UTIL.createTable(tableName, fs);
        // Check put and get.
        final byte[] row = Bytes.toBytes("row");
        Put p = new Put(row);
        for (byte[] f : fs) {
            p.addColumn(f, f, f);
        }
        ht.put(p);
        Result r = ht.get(new Get(row));
        int i = 0;
        for (CellScanner cellScanner = r.cellScanner(); cellScanner.advance();) {
            Cell cell = cellScanner.current();
            byte[] f = fs[(i++)];
            Assert.assertTrue(Bytes.toString(f), Bytes.equals(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength(), f, 0, f.length));
        }
        // Check getRowOrBefore
        byte[] f = fs[0];
        Get get = new Get(row);
        get.addFamily(f);
        r = ht.get(get);
        Assert.assertTrue(r.toString(), r.containsColumn(f, f));
        // Check scan.
        ResultScanner scanner = ht.getScanner(new Scan());
        int count = 0;
        while ((r = scanner.next()) != null) {
            Assert.assertTrue(((r.listCells().size()) == 3));
            count++;
        } 
        Assert.assertTrue((count == 1));
    }

    @Test
    public void testNoCodec() {
        Configuration c = new Configuration();
        c.set("hbase.client.default.rpc.codec", "");
        String codec = AbstractRpcClient.getDefaultCodec(c);
        Assert.assertTrue(((codec == null) || ((codec.length()) == 0)));
    }
}

